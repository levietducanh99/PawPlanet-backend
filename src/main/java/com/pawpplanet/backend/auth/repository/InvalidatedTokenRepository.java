package com.pawpplanet.backend.auth.repository;

import com.pawpplanet.backend.auth.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
