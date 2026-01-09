package com.pawpplanet.backend.explore.service.impl;

import com.pawpplanet.backend.explore.dto.*;
import com.pawpplanet.backend.explore.repository.ExplorePetRepository;
import com.pawpplanet.backend.explore.repository.ExplorePostRepository;
import com.pawpplanet.backend.explore.repository.ExploreUserRepository;
import com.pawpplanet.backend.explore.service.ExploreService;
import com.pawpplanet.backend.utils.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExploreServiceImpl implements ExploreService {

    private final ExplorePostRepository explorePostRepository;
    private final ExplorePetRepository explorePetRepository;
    private final ExploreUserRepository exploreUserRepository;
    private final SecurityHelper securityHelper;

    // Default ratios
    private static final double POST_RATIO = 0.60;
    private static final double PET_RATIO = 0.25;
    private static final double USER_RATIO = 0.15;

    private static final int DEFAULT_LIMIT = 30;
    private static final int MAX_LIMIT = 50;

    @Override
    @Transactional(readOnly = true)
    public ExploreResponse getExploreFeed(Integer limit, String seed, String include) {
        // Validate and set limit
        int finalLimit = (limit == null || limit <= 0) ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);

        // Parse include types
        Set<String> includeTypes = parseIncludeTypes(include);

        // Generate or parse seed
        Long seedValue = parseSeed(seed);
        String newSeed = generateSeedString(seedValue);

        // Get current user (optional)
        Long currentUserId = securityHelper.getCurrentUserIdFromTokenOrNull();

        // Calculate counts for each entity type based on ratios
        int postCount = includeTypes.contains("post") ? (int) Math.ceil(finalLimit * POST_RATIO) : 0;
        int petCount = includeTypes.contains("pet") ? (int) Math.ceil(finalLimit * PET_RATIO) : 0;
        int userCount = includeTypes.contains("user") ? (int) Math.ceil(finalLimit * USER_RATIO) : 0;

        // Fetch data using optimized queries (single query per type with all needed data)
        List<Map<String, Object>> posts = postCount > 0
            ? explorePostRepository.findRandomPostsOptimized(seedValue, postCount, currentUserId)
            : Collections.emptyList();

        List<Map<String, Object>> pets = petCount > 0
            ? explorePetRepository.findRandomPetsOptimized(seedValue, petCount, currentUserId)
            : Collections.emptyList();

        List<Map<String, Object>> users = userCount > 0
            ? exploreUserRepository.findRandomUsersOptimized(seedValue, userCount, currentUserId)
            : Collections.emptyList();

        // Build response items
        List<ExploreItemDTO> items = new ArrayList<>();

        // Convert posts - fetch media in batch
        if (!posts.isEmpty()) {
            List<Long> postIds = posts.stream()
                    .map(p -> ((Number) p.get("postId")).longValue())
                    .collect(Collectors.toList());

            // Batch fetch media
            List<Map<String, Object>> allMedia = explorePostRepository.findMediaByPostIds(postIds);
            Map<Long, List<PostExploreDTO.MediaDTO>> mediaByPostId = new HashMap<>();

            for (Map<String, Object> media : allMedia) {
                Long postId = ((Number) media.get("postId")).longValue();
                mediaByPostId.computeIfAbsent(postId, k -> new ArrayList<>()).add(
                    PostExploreDTO.MediaDTO.builder()
                        .url((String) media.get("url"))
                        .type((String) media.get("type"))
                        .build()
                );
            }

            for (Map<String, Object> post : posts) {
                items.add(ExploreItemDTO.builder()
                        .type("post")
                        .data(buildPostExploreDTO(post, mediaByPostId))
                        .build());
            }
        }

        // Convert pets
        for (Map<String, Object> pet : pets) {
            items.add(ExploreItemDTO.builder()
                    .type("pet")
                    .data(buildPetExploreDTO(pet))
                    .build());
        }

        // Convert users
        for (Map<String, Object> user : users) {
            items.add(ExploreItemDTO.builder()
                    .type("user")
                    .data(buildUserExploreDTO(user))
                    .build());
        }

        // Shuffle items to mix types (but deterministically based on seed)
        Collections.shuffle(items, new Random(seedValue));

        // Trim to exact limit
        if (items.size() > finalLimit) {
            items = items.subList(0, finalLimit);
        }

        return ExploreResponse.builder()
                .seed(newSeed)
                .items(items)
                .build();
    }

    private Set<String> parseIncludeTypes(String include) {
        if (include == null || include.trim().isEmpty()) {
            return Set.of("post", "pet", "user");
        }

        Set<String> types = Arrays.stream(include.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(t -> t.equals("post") || t.equals("pet") || t.equals("user"))
                .collect(Collectors.toSet());

        if (types.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid include parameter");
        }

        return types;
    }

    private Long parseSeed(String seed) {
        if (seed == null || seed.trim().isEmpty()) {
            return System.currentTimeMillis() % 999983;
        }

        try {
            // Try to parse as long
            return Long.parseLong(seed.replaceAll("[^0-9]", "")) % 999983;
        } catch (NumberFormatException e) {
            // Use hashCode as fallback
            return (long) Math.abs(seed.hashCode()) % 999983;
        }
    }

    private String generateSeedString(Long seed) {
        // Generate a short, URL-friendly seed string
        return String.format("%x", seed);
    }

    private PostExploreDTO buildPostExploreDTO(Map<String, Object> data,
                                                Map<Long, List<PostExploreDTO.MediaDTO>> mediaByPostId) {
        Long postId = ((Number) data.get("postId")).longValue();

        // Convert Timestamp to LocalDateTime
        LocalDateTime createdAt = null;
        Object createdAtObj = data.get("createdAt");
        if (createdAtObj instanceof Timestamp) {
            createdAt = ((Timestamp) createdAtObj).toLocalDateTime();
        }

        // Get like/comment counts
        Integer likeCount = data.get("likeCount") != null
            ? ((Number) data.get("likeCount")).intValue() : 0;
        Integer commentCount = data.get("commentCount") != null
            ? ((Number) data.get("commentCount")).intValue() : 0;

        // Get liked status
        Boolean liked = false;
        Object likedObj = data.get("liked");
        if (likedObj instanceof Boolean) {
            liked = (Boolean) likedObj;
        } else if (likedObj instanceof Number) {
            liked = ((Number) likedObj).intValue() > 0;
        }

        return PostExploreDTO.builder()
                .id(postId)
                .content((String) data.get("content"))
                .createdAt(createdAt)
                .authorId(((Number) data.get("authorId")).longValue())
                .authorUsername((String) data.get("authorUsername"))
                .authorAvatarUrl((String) data.get("authorAvatarUrl"))
                .likeCount(likeCount)
                .commentCount(commentCount)
                .liked(liked)
                .media(mediaByPostId.getOrDefault(postId, Collections.emptyList()))
                .build();
    }

    private PetExploreDTO buildPetExploreDTO(Map<String, Object> data) {
        Long petId = ((Number) data.get("petId")).longValue();
        Long ownerId = ((Number) data.get("ownerId")).longValue();

        Long followerCount = 0L;
        Object followerCountObj = data.get("followerCount");
        if (followerCountObj instanceof Number) {
            followerCount = ((Number) followerCountObj).longValue();
        }

        return PetExploreDTO.builder()
                .id(petId)
                .name((String) data.get("name"))
                .species((String) data.get("speciesName"))
                .breed((String) data.get("breedName"))
                .avatarUrl((String) data.get("avatarUrl"))
                .owner(PetExploreDTO.OwnerDTO.builder()
                        .id(ownerId)
                        .username((String) data.get("ownerUsername"))
                        .build())
                .followerCount(followerCount)
                .build();
    }

    private UserExploreDTO buildUserExploreDTO(Map<String, Object> data) {
        Long userId = ((Number) data.get("userId")).longValue();

        Long petCount = 0L;
        Object petCountObj = data.get("petCount");
        if (petCountObj instanceof Number) {
            petCount = ((Number) petCountObj).longValue();
        }

        Long followerCount = 0L;
        Object followerCountObj = data.get("followerCount");
        if (followerCountObj instanceof Number) {
            followerCount = ((Number) followerCountObj).longValue();
        }

        return UserExploreDTO.builder()
                .id(userId)
                .username((String) data.get("username"))
                .avatarUrl((String) data.get("avatarUrl"))
                .petCount(petCount)
                .followerCount(followerCount)
                .build();
    }
}

