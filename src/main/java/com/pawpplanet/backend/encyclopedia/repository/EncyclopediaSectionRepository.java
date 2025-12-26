package com.pawpplanet.backend.encyclopedia.repository;

import com.pawpplanet.backend.encyclopedia.entity.EncyclopediaSectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EncyclopediaSectionRepository extends JpaRepository<EncyclopediaSectionEntity, UUID> {
}

