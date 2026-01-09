package com.pawpplanet.backend.post.service.impl;

import com.pawpplanet.backend.encyclopedia.repository.BreedRepository;
import com.pawpplanet.backend.encyclopedia.repository.SpeciesRepository;
import com.pawpplanet.backend.pet.entity.PetEntity;
import com.pawpplanet.backend.pet.repository.PetMediaRepository;
import com.pawpplanet.backend.pet.repository.PetRepository;
import com.pawpplanet.backend.post.constant.PostType;
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
    public List<PostResponse> getUrgentPosts() {
        List<PostEntity> posts = postRepository.findByTypeOrderByCreatedAtDesc(PostType.URGENT);

        Long viewerId = securityHelper.getCurrentUserIdOrNull();
        UserEntity viewer = viewerId != null ? userRepository.findById(viewerId).orElse(null) : null;

        return posts.stream()
                .map(post -> buildPostResponse(post, viewer))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUrgentPosts() {
        return postRepository.countByType(PostType.URGENT);
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
