package com.pawpplanet.backend.pet.service.impl;

import com.cloudinary.Cloudinary;
import com.pawpplanet.backend.pet.service.PetMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PetMediaServiceImpl implements PetMediaService {

    // Spring giờ đã biết cách "Inject" cái này nhờ file Config ở trên
    private final Cloudinary cloudinary;

    @Override
    public Map<String, Object> getUploadSignature() {
        long timestamp = System.currentTimeMillis() / 1000;

        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", timestamp);

        // Lấy apiSecret trực tiếp từ config của đối tượng cloudinary
        String apiSecret = (String) cloudinary.config.apiSecret;

        // Sử dụng class Cloudinary để ký (sign) thay vì CloudinaryUtils không tồn tại
        String signature = cloudinary.apiSignRequest(params, apiSecret);

        Map<String, Object> response = new HashMap<>();
        response.put("apiKey", cloudinary.config.apiKey);
        response.put("cloudName", cloudinary.config.cloudName);
        response.put("timestamp", timestamp);
        response.put("signature", signature);

        return response;
    }
}