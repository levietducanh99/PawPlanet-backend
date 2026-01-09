package com.pawpplanet.backend.post.service.impl;

import com.pawpplanet.backend.encyclopedia.repository.BreedRepository;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesRepository;
import com.pawpplanet.backend.pet.entity.PetEntity;
import com.pawpplanet.backend.pet.repository.PetMediaRepository;
import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.post.dto.CreatePostRequest;
import com.pawpplanet.backend.post.dto.MediaUrlRequest;
import com.pawpplanet.backend.post.dto.PostResponse;
import com.pawpplanet.backend.post.dto.UpdatePostRequest;
import com.pawpplanet.backend.post.entity.PostEntity;
import com.pawpplanet.backend.post.entity.PostMediaEntity;
import com.pawpplanet.backend.post.entity.PostPetEntity;
import com.pawpplanet.backend.post.mapper.PostMapper;
import com.pawpplanet.backend.post.repository.*;
import com.pawpplanet.backend.post.service.PostService;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.FollowUserRepository;
import com.pawpplanet.backend.user.repository.UserRepository;
import com.pawpplanet.backend.utils.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMediaRepository postMediaRepository;
    private final PostPetRepository postPetRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final PetRepository petRepository;
    private final PetMediaRepository petMediaRepository;
    private final SecurityHelper securityHelper;
    private final FollowUserRepository followUserRepository;
    private final PostMapper postMapper;



    // ================= CREATE =================
    @Override
    public PostResponse createPost(CreatePostRequest request) {

        UserEntity user = securityHelper.getCurrentUser();

        PostEntity post = new PostEntity();
        post.setAuthorId(user.getId());
        post.setContent(request.getContent());
        post.setHashtags(request.getHashtags());
        post.setType(request.getType());
        post.setContactInfo(request.getContactInfo());
        post.setLocation(request.getLocation());

        PostEntity savedPost = postRepository.save(post);

        savePostMedia(savedPost.getId(), request.getMediaUrls());
        savePostPets(savedPost.getId(), request.getPetIds());

        return buildPostResponse(savedPost, user);
    }

    // ================= UPDATE =================
    @Override
    public PostResponse updatePost(Long postId, UpdatePostRequest request) {

        UserEntity user = securityHelper.getCurrentUser();

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết"));

        if (!post.getAuthorId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền sửa");
        }

        post.setContent(request.getContent());
        post.setHashtags(request.getHashtags());
        post.setType(request.getType());
        post.setContactInfo(request.getContactInfo());
        post.setLocation(request.getLocation());

        postMediaRepository.deleteByPostId(postId);
        postPetRepository.deleteByPostId(postId);

        savePostMedia(postId, request.getMediaUrls());
        savePostPets(postId, request.getPetIds());

        return buildPostResponse(postRepository.save(post), user);
    }

    // ================= DELETE =================
    @Override
    public void deletePost(Long postId) {
        UserEntity user = securityHelper.getCurrentUser();

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết"));

        if (!post.getAuthorId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền xóa");
        }

        // Soft delete the post
        post.setIsDeleted(true);
        post.setDeletedAt(LocalDateTime.now());
        post.setDeletedBy(user.getId());

        postRepository.save(post);

        // Soft delete associated media (do NOT delete files from cloud storage)
        List<PostMediaEntity> mediaList = postMediaRepository.findByPostId(postId);
        for (PostMediaEntity media : mediaList) {
            media.setIsDeleted(true);
            media.setDeletedAt(LocalDateTime.now());
            media.setDeletedBy(user.getId());
        }
        if (!mediaList.isEmpty()) {
            postMediaRepository.saveAll(mediaList);
        }
    }

    // ================= READ =================

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUserId(Long userId) {

        UserEntity author = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        List<PostEntity> posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId);

        // Use getCurrentUserIdOrNull to get the viewer ID (who is viewing, not the author)
        Long viewerId = securityHelper.getCurrentUserIdOrNull();
        UserEntity viewer = viewerId != null ? userRepository.findById(viewerId).orElse(null) : null;

        return posts.stream()
                .map(post -> buildPostResponse(post, viewer))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getMyPosts() {
        UserEntity currentUser = securityHelper.getCurrentUser();

        return getPostsByUserId(currentUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByPetId(Long petId) {
        List<PostEntity> posts = postRepository.findAllByPetId(petId);

        UserEntity currentUser = securityHelper.getCurrentUser();

        return posts.stream()
                .map(post -> buildPostResponse(post, currentUser))
                .toList();
    }
    // Thêm vào trong PostServiceImpl.java

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết"));

        UserEntity currentUser = securityHelper.getCurrentUser();

        return buildPostResponse(post, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getNewsFeed() {
        UserEntity currentUser = securityHelper.getCurrentUser();

        List<Long> followingIds = followUserRepository.findFollowingIdsByFollowerId(currentUser.getId());

        if (followingIds.isEmpty()) {
            return new ArrayList<>();
        }


        List<PostEntity> posts = postRepository.findByAuthorIdInOrderByCreatedAtDesc(followingIds);

        return posts.stream()
                .map(post -> buildPostResponse(post, currentUser))
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getExploreFeed() {
        UserEntity currentUser = securityHelper.getCurrentUser();
        Long currentUserId = (currentUser != null) ? currentUser.getId() : -1L;

        // 1. Lấy danh sách đang follow
        List<Long> followingIds = (currentUser == null) ? new ArrayList<>()
                : followUserRepository.findFollowingIdsByFollowerId(currentUserId);

        // Tránh lỗi SQL NOT IN rỗng
        List<Long> filterIds = new ArrayList<>(followingIds);
        if (filterIds.isEmpty()) filterIds.add(-1L);

        // 2. Lấy Pool ứng viên (Lấy 50 bài mới nhất mỗi loại để F5 ra bài khác nhau)
        List<Long> followPool = postRepository.findRecentFollowedIds(filterIds, PageRequest.of(0, 50));
        List<Long> explorePool = postRepository.findRecentExploreIds(filterIds, currentUserId, PageRequest.of(0, 50));

        // 3. Chọn ngẫu nhiên 10 ID từ mỗi Pool
        List<Long> selectedIds = new ArrayList<>();
        selectedIds.addAll(pickRandom(followPool, 10));
        selectedIds.addAll(pickRandom(explorePool, 10));

        if (selectedIds.isEmpty()) return new ArrayList<>();

        // 4. Lấy dữ liệu PostEntities
        List<PostEntity> posts = postRepository.findAllByIdIn(selectedIds);

        // --- BƯỚC QUAN TRỌNG: BATCH FETCHING (CHỐNG 40 GIÂY) ---
        // Lấy tất cả Author ID có trong 20 bài post này
        List<Long> authorIds = posts.stream().map(PostEntity::getAuthorId).distinct().toList();

        // Query 1 lần lấy tất cả User
        Map<Long, UserEntity> userMap = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, u -> u));

        // Query 1 lần lấy tất cả Media của 20 bài post
        Map<Long, List<PostMediaEntity>> mediaMap = postMediaRepository.findByPostIdIn(selectedIds).stream()
                .collect(Collectors.groupingBy(PostMediaEntity::getPostId));
        // -----------------------------------------------------

        // Trộn lẫn bài viết cho tự nhiên
        Collections.shuffle(posts);

        return posts.stream()
                .map(post -> {
                    UserEntity author = userMap.get(post.getAuthorId());
                    List<PostMediaEntity> medias = mediaMap.getOrDefault(post.getId(), List.of());
                    return buildPostResponse(post, author, medias, currentUser);
                })
                .toList();
    }

    // Hàm chọn ngẫu nhiên
    private List<Long> pickRandom(List<Long> list, int n) {
        if (list.size() <= n) return new ArrayList<>(list);
        List<Long> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }

    // Hàm build response không được gọi Repository bên trong nữa
    private PostResponse buildPostResponse(PostEntity post, UserEntity author,
                                           List<PostMediaEntity> medias, UserEntity currentUser) {
        PostResponse response = new PostResponse();
        // Map dữ liệu từ post sang response ở đây
        // Map thông tin từ author (ví dụ: name, avatar)
        // Map danh sách medias
        return response;
    }









    // ================= BUILD RESPONSE =================
    private PostResponse buildPostResponse(PostEntity post, UserEntity viewer) {

        if (post.getAuthorId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Post không có author");
        }

        UserEntity author = userRepository.findById(post.getAuthorId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tác giả"));

        List<PostMediaEntity> media =
                postMediaRepository.findByPostId(post.getId());

        List<PostPetEntity> pets =
                postPetRepository.findByPostId(post.getId());

        List<PostResponse.PostPetDTO> petDtos = pets.stream().map(link -> {
            PetEntity pet = petRepository.findById(link.getPetId()).orElse(null);
            PostResponse.PostPetDTO dto = new PostResponse.PostPetDTO();

            if (pet != null) {
                dto.setId(pet.getId());
                dto.setName(pet.getName());
                dto.setOwnerId(pet.getOwnerId());

                if (pet.getSpeciesId() != null) {
                    speciesRepository.findById(pet.getSpeciesId())
                            .ifPresent(s -> dto.setSpeciesName(s.getName()));
                }

                if (pet.getBreedId() != null) {
                    breedRepository.findById(pet.getBreedId())
                            .ifPresent(b -> dto.setBreedName(b.getName()));
                }

                if (pet.getOwnerId() != null) {
                    userRepository.findById(pet.getOwnerId())
                            .ifPresent(u -> dto.setOwnerUsername(u.getUsername()));
                }

                petMediaRepository.findByPetId(pet.getId()).stream()
                        .filter(m -> "avatar".equals(m.getRole()))
                        .findFirst()
                        .ifPresent(m -> dto.setAvatarUrl(m.getUrl()));


            }
            return dto;
        }).toList();

        int likeCount =
                likeRepository.countByPostId(post.getId());

        int commentCount =
                commentRepository.countByPostId(post.getId());

        boolean liked = false;
        if (viewer != null && viewer.getId() != null) {
            liked = likeRepository.existsByPostIdAndUserId(
                    post.getId(),
                    viewer.getId()
            );
        }

        return postMapper.toResponse(
                post,
                author,
                media,
                petDtos,
                likeCount,
                commentCount,
                liked
        );
    }





    // ================= HELPER =================
    private void savePostMedia(Long postId, List<MediaUrlRequest> mediaUrls) {
        if (mediaUrls == null || mediaUrls.isEmpty()) return;

        List<PostMediaEntity> postMediaList = new ArrayList<>();

        for (int i = 0; i < mediaUrls.size(); i++) {
            MediaUrlRequest mediaRequest = mediaUrls.get(i);

            PostMediaEntity postMedia = new PostMediaEntity();
            postMedia.setPostId(postId);
            postMedia.setType(mediaRequest.getType() != null ? mediaRequest.getType() : "image");
            postMedia.setPublicId(mediaRequest.getPublicId());  // Save public_id instead of URL
            postMedia.setUrl(null);  // URL will be built dynamically from publicId
            postMedia.setDisplayOrder(i);

            postMediaList.add(postMedia);
        }

        postMediaRepository.saveAll(postMediaList);
    }

    private void savePostPets(Long postId, List<Long> petIds) {
        if (petIds == null || petIds.isEmpty()) return;

        List<PostPetEntity> postPets = petIds.stream()
                .map(petId -> new PostPetEntity(postId, petId))
                .toList();

        postPetRepository.saveAll(postPets);
    }

}
