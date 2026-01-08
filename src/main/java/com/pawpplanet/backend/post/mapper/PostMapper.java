package com.pawpplanet.backend.post.mapper;

import com.pawpplanet.backend.media.service.CloudinaryUrlBuilder;
import com.pawpplanet.backend.post.dto.PostResponse;
import com.pawpplanet.backend.post.entity.PostEntity;
import com.pawpplanet.backend.post.entity.PostMediaEntity;
import com.pawpplanet.backend.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final CloudinaryUrlBuilder urlBuilder;

    public PostResponse toResponse(
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

                        // Build URL from publicId if available, otherwise use stored URL
                        String url = m.getPublicId() != null
                            ? urlBuilder.buildOptimizedUrl(m.getPublicId(), m.getType())
                            : m.getUrl();
                        dto.setUrl(url);

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

