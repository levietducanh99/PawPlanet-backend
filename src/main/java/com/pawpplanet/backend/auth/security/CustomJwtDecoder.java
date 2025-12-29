package com.pawpplanet.backend.auth.security;

import com.nimbusds.jose.JOSEException;
import com.pawpplanet.backend.auth.dto.request.IntrospectRequest;
import com.pawpplanet.backend.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;


import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.key}")
    private String signerKey;

    @Autowired
    private AuthService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {

        IntrospectRequest introspectRequest = new IntrospectRequest();
        introspectRequest.setToken(token);

        try {

            var response = authenticationService.introspect(introspectRequest);

            if (!response.isValid())
                throw new JwtException("Token invalid");
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}