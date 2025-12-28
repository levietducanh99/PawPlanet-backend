package com.pawpplanet.backend.user.controller;

import com.pawpplanet.backend.user.dto.UserProfileDTO;
import com.pawpplanet.backend.user.dto.UpdateProfileRequestDTO;
import com.pawpplanet.backend.user.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getMyProfile() {
        return ResponseEntity.ok(userServiceImpl.viewProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateMyInfo(@RequestBody UpdateProfileRequestDTO dto) {
        return ResponseEntity.ok(userServiceImpl.updateMyInformation(dto));
    }
}