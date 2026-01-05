package com.pawpplanet.backend.post.dto;

import java.time.LocalDateTime;
import java.util.List;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDetailResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private LocalDateTime createdAt;
    private List<CommentDetailResponse> replies; // Danh sách các câu trả lời
}
