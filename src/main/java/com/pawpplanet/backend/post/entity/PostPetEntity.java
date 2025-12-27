package com.pawpplanet.backend.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_pet", schema = "social")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PostPetId.class)
public class PostPetEntity {

    @Id
    @Column(name = "post_id")
    private Long postId;

    @Id
    @Column(name = "pet_id")
    private Long petId;
}

