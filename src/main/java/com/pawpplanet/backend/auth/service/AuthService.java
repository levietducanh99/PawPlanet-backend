package com.pawpplanet.backend.auth.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.pawpplanet.backend.auth.dto.TokenType;
import com.pawpplanet.backend.auth.dto.request.*;
import com.pawpplanet.backend.auth.dto.response.AuthResponse;
import com.pawpplanet.backend.auth.dto.response.IntrospectResponse;
import com.pawpplanet.backend.auth.entity.InvalidatedToken;
import com.pawpplanet.backend.auth.repository.InvalidatedTokenRepository;
import com.pawpplanet.backend.common.exception.AppException;
import com.pawpplanet.backend.common.exception.ErrorCode;
import com.pawpplanet.backend.common.service.MailService;
import com.pawpplanet.backend.user.dto.UserResponse;
import com.pawpplanet.backend.user.entity.Role;
import com.pawpplanet.backend.user.entity.UserEntity;
import com.pawpplanet.backend.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private UserRepository userRepository;

    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Autowired
    private MailService mailService;


    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @Value("${jwt.key}")
    protected String SIGNER_KEY;

    @Value("${jwt.valid-ms}")
    protected Long TOKEN_VALID_MS;

    @Value("${jwt.refreshable-ms}")
    protected Long TOKEN_REFRESHABLE_MS;

    @Autowired
    public AuthService(UserRepository userRepository, InvalidatedTokenRepository invalidatedTokenRepository) {
        this.userRepository = userRepository;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }


    public UserEntity register(RegisterRequest request) throws MessagingException {
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
        String token = generateToken(userEntity, TokenType.EMAIL_VERIFICATION.name());
        mailService.sendVerifyEmail(request.getEmail(), token);
        return userRepository.save(userEntity);
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String encodedPassword = user.getPassword();
        if (!passwordEncoder.matches(request.getPassword(), encodedPassword)) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        String gerneratedToken = generateToken(user, TokenType.ACCESS.name());
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(gerneratedToken);
        authResponse.setAuthenticated(true);
        return authResponse;
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signedJWT = verifyToken(request.getToken(), true);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken = new InvalidatedToken();
            invalidatedToken.setId(jti);
            invalidatedToken.setExpiredAt(expirationTime);
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("token already invalidated");
        }

    }

    public void changePassword(ChangePasswordRequest request) throws ParseException, JOSEException {
        UserResponse currentUser = getCurrentUser();
        log.info("Changing password for user {}", currentUser.getEmail());
        UserEntity userEntity = userRepository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if(!passwordEncoder.matches(request.getOldPassword(), userEntity.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        if(request.getOldPassword().equals(request.getNewPassword())) {
            throw new AppException(ErrorCode.SAME_PASSWORD);
        }
        if(request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        userEntity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(userEntity);
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setToken(request.getToken());
        logout(logoutRequest);
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant() .plus(TOKEN_REFRESHABLE_MS, ChronoUnit.MILLIS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.INVALID_TOKEN);

        if (invalidatedTokenRepository
                .existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.INVALID_TOKEN);

        return signedJWT;
    }




    private String generateToken(UserEntity user, String type) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("pawplanet")
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .claim("type", type)
                .claim("scope", user.getRole())
                .expirationTime(new Date(
                        Instant.now().plus(TOKEN_VALID_MS, ChronoUnit.MILLIS).toEpochMilli()
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
        var token = request.getToken();

        try {
            verifyToken(token, false);
        } catch (AppException ex) {
            IntrospectResponse introspectResponse = new IntrospectResponse();
            introspectResponse.setValid(false);
            return introspectResponse;
        }
        IntrospectResponse introspectResponse = new IntrospectResponse();
        introspectResponse.setValid(true);

        return introspectResponse;
    }

    public AuthResponse refreshToken(IntrospectRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);
        var jti = signedJWT.getJWTClaimsSet().getJWTID();
        var expriationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = new InvalidatedToken();
        invalidatedToken.setId(jti);
        invalidatedToken.setExpiredAt(expriationTime);
        invalidatedTokenRepository.save(invalidatedToken);

        String userEmail = signedJWT.getJWTClaimsSet().getSubject();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String gerneratedToken = generateToken(userEntity, TokenType.REFRESH.name());
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(gerneratedToken);
        authResponse.setAuthenticated(true);
        return authResponse;
    }

    // Get current logged in user
    public UserResponse getCurrentUser(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String userEmail = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return getUserResponse(userEntity);
    }

    private UserResponse getUserResponse(UserEntity userEntity) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(userEntity.getUsername());
        userResponse.setEmail(userEntity.getEmail());
        userResponse.setBio(userEntity.getBio());
        userResponse.setAvatarUrl(userEntity.getAvatarUrl());
        userResponse.setRole(userEntity.getRole());
        return userResponse;
    }


    public UserResponse getUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        getCurrentUser();
        return getUserResponse(userEntity);
    }

    private String verifyToken(String token, String expectedType) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        String userEmail = signedJWT.getJWTClaimsSet().getSubject();
        String type = (String) signedJWT.getJWTClaimsSet().getClaim("type");
        if(!type.equals(expectedType)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        return userEmail;
    }

    public void verifyEmail(String token) throws ParseException {
        String userEmail = verifyToken(token, TokenType.EMAIL_VERIFICATION.name());
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userEntity.setIsVerified(true);
        userRepository.save(userEntity);
    }

    public void resetPassword(String token, ResetPasswordRequest request) throws ParseException, JOSEException {
        String userEmail = verifyToken(token, TokenType.PASSWORD_RESET.name());
        UserEntity userEntity = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userEntity.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(userEntity);
        logout(request.getLogoutRequest());
    }

    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String token = generateToken(userEntity, TokenType.PASSWORD_RESET.name());
        mailService.sendResetPasswordEmail(request.getEmail(), token);
    }
}
