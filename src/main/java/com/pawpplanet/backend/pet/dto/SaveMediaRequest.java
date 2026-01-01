package com.pawpplanet.backend.pet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveMediaRequest {
    private Long petId;
    // URL nhận được từ Cloudinary sau khi upload thành công
    private String url;

    // Loại media: 'image' hoặc 'video'
    private String type;

    // Vai trò của media: 'avatar' hoặc 'gallery'
    private String role;
}