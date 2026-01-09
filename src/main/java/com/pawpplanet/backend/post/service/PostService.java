package com.pawpplanet.backend.post.service;


import com.pawpplanet.backend.post.dto.CreatePostRequest;
import com.pawpplanet.backend.post.dto.PostResponse;
import com.pawpplanet.backend.post.dto.UpdatePostRequest;
import java.util.List;


public interface PostService {
    PostResponse createPost(CreatePostRequest request);

    PostResponse updatePost(Long postId, UpdatePostRequest request);

    void deletePost(Long postId);

    List<PostResponse> getPostsByUserId(Long userId);

    List<PostResponse> getMyPosts();

    List<PostResponse> getPostsByPetId(Long petId);

    PostResponse getPostById(Long id);

    List<PostResponse> getNewsFeed();

}
