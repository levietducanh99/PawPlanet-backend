package com.pawpplanet.backend.post.service.impl;

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
import com.pawpplanet.backend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMediaRepository postMediaRepository;
    private final PostPetRepository postPetRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public PostServiceImpl(
            PostRepository postRepository,
            UserRepository userRepository,
            PostMediaRepository postMediaRepository,
            PostPetRepository postPetRepository,
            LikeRepository likeRepository,
            CommentRepository commentRepository
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMediaRepository = postMediaRepository;
        this.postPetRepository = postPetRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
    }

    // ================= CREATE =================
    @Override
    public PostResponse createPost(CreatePostRequest request) {

        UserEntity user = getCurrentUser();

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

        UserEntity user = getCurrentUser();

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
    // ================= READ =================

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUserId(Long userId) {

        UserEntity author = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        List<PostEntity> posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId);

        return posts.stream()
                .map(post -> buildPostResponse(post, author))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getMyPosts() {
        UserEntity currentUser = getCurrentUser();

        return getPostsByUserId(currentUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByPetId(Long petId) {
        List<PostEntity> posts = postRepository.findAllByPetId(petId);

        UserEntity currentUser = getCurrentUser();

        return posts.stream()
                .map(post -> buildPostResponse(post, currentUser))
                .toList();
    }

    // ================= BUILD RESPONSE =================
    private PostResponse buildPostResponse(PostEntity post, UserEntity viewer) {

        UserEntity author = userRepository.findById(post.getAuthorId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tác giả"));

        List<PostMediaEntity> media =
                postMediaRepository.findByPostId(post.getId());

        List<PostPetEntity> pets =
                postPetRepository.findByPostId(post.getId());

        int likeCount =
                likeRepository.countByPostId(post.getId());

        int commentCount =
                commentRepository.countByPostIdAndDeletedAtIsNull(post.getId());

        boolean liked = false;
        if (viewer != null) {
            liked = likeRepository.existsByPostIdAndUserId(
                    post.getId(),
                    viewer.getId()
            );
        }

        return PostMapper.toResponse(
                post,
                author,
                media,
                pets,
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
            postMedia.setUrl(mediaRequest.getUrl());
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

    private UserEntity getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Chưa đăng nhập"
            );
        }

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy user"
                        ));
    }
}
