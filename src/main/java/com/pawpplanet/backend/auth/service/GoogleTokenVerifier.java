package com.pawpplanet.backend.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.pawpplanet.backend.auth.dto.GoogleUserInfo;
import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Service
public class GoogleTokenVerifier {

    @Value("${google.client-id}")
    private String googleClientId;

    public GoogleUserInfo verifyToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            
            if (idToken == null) {
                log.error("Invalid Google ID token");
                throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            // Validate required fields
            String userId = payload.getSubject();
            String email = payload.getEmail();
            Boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            if (userId == null || email == null) {
                log.error("Missing required fields in Google token");
                throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
            }

            GoogleUserInfo userInfo = new GoogleUserInfo();
            userInfo.setSub(userId);
            userInfo.setEmail(email);
            userInfo.setEmailVerified(emailVerified != null ? emailVerified : false);
            userInfo.setName(name);
            userInfo.setPicture(pictureUrl);

            log.info("Successfully verified Google token for user: {}", email);
            return userInfo;

        } catch (GeneralSecurityException e) {
            log.error("Security error while verifying Google token", e);
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
        } catch (IOException e) {
            log.error("I/O error while verifying Google token", e);
            throw new AppException(ErrorCode.INVALID_GOOGLE_TOKEN);
        }
    }
}
