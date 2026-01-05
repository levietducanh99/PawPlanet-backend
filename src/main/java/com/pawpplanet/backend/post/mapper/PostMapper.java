package com.pawpplanet.backend.post.mapper;

import com.pawpplanet.backend.post.dto.PostResponse;
import com.pawpplanet.backend.post.entity.PostEntity;
import com.pawpplanet.backend.post.entity.PostMediaEntity;
import com.pawpplanet.backend.post.entity.PostPetEntity;
import com.pawpplanet.backend.user.entity.UserEntity;

import java.util.List;

public class PostMapper {

    public static PostResponse toResponse(
            PostEntity post,
            UserEntity author,
            List<PostMediaEntity> media,
            List<PostResponse.PostPetDTO> petDtos,
            int likeCount,
            int commentCount,
            boolean liked
    ) {
        PostResponse res = new PostResponse();

        res.setId(post.getId());
        res.setAuthorId(author.getId());
        res.setAuthorUsername(author.getUsername());
        res.setAuthorAvatarUrl(author.getAvatarUrl());

        res.setContent(post.getContent());
        res.setHashtags(post.getHashtags());
        res.setType(post.getType());
        res.setContactInfo(post.getContactInfo());
        res.setLocation(post.getLocation());
        res.setCreatedAt(post.getCreatedAt());

        if (media != null) {
            res.setMedia(
                    media.stream().map(m -> {
                        PostResponse.PostMediaDTO dto =
                                new PostResponse.PostMediaDTO();
                        dto.setId(m.getId());
                        dto.setUrl(m.getUrl());
                        dto.setType(m.getType());
                        dto.setDisplayOrder(m.getDisplayOrder());
                        return dto;
                    }).toList()
            );
        }

        res.setPets(petDtos);
        res.setLikeCount(likeCount);
        res.setCommentCount(commentCount);
        res.setLiked(liked);

        return res;
    }
}

