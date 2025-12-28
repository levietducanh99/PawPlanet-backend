package com.pawpplanet.backend.auth.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.pawpplanet.backend.auth.dto.*;
import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.user.dto.UserResponse;
import com.pawpplanet.backend.user.entity.Role;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @Value("${jwt.key}")
    protected String SIGNER_KEY;

    public UserEntity register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }


        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setUsername(request.getUsername());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(Role.USER.name());
        userEntity.setBio(request.getBio());
        userEntity.setAvatarUrl(request.getAvatarUrl());
        return userRepository.save(userEntity);
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String encodedPassword = user.getPassword();
        if (!passwordEncoder.matches(request.getPassword(), encodedPassword)) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        String gerneratedToken = generateToken(user);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(gerneratedToken);
        authResponse.setAuthenticated(true);
        return authResponse;
    }




    private String generateToken(UserEntity userEntity) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userEntity.getEmail())
                .issuer("pawplanet")
                .issueTime(new Date())
                .claim("scope", userEntity.getRole())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()
                ))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try{
            MACSigner signer = new MACSigner(SIGNER_KEY.getBytes());
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean isExpired = expirationTime.before(new Date());
        boolean verified =  signedJWT.verify(verifier);

        IntrospectResponse introspectResponse = new IntrospectResponse();
        introspectResponse.setValid(!isExpired && verified);

        return introspectResponse;
    }

    public UserResponse getUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(userEntity.getUsername());
        userResponse.setEmail(userEntity.getEmail());
        userResponse.setBio(userEntity.getBio());
        userResponse.setAvatarUrl(userEntity.getAvatarUrl());
        userResponse.setRole(userEntity.getRole());

        return userResponse;
    }
}
