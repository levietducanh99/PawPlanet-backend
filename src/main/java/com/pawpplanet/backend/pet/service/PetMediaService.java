package com.pawpplanet.backend.pet.service;

import com.pawpplanet.backend.pet.dto.PetMediaDTO;
import com.pawpplanet.backend.pet.dto.SaveMediaRequest;
import java.util.Map;

public interface PetMediaService {
    Map<String, Object> getUploadSignature();

}
