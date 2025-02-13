package com.example.elice_3rd.favorite.entity;

import jakarta.persistence.*;

@Entity
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id", nullable = false)
    private Long favoriteId;
}
