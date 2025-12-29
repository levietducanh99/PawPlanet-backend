package com.pawpplanet.backend.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(schema = "auth", name = "invalidated_tokens")
public class InvalidatedToken {
    @Id
    private String id;
    private Date expiredAt;
}
