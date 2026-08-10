package com.example.recordatorios_esp32.model;

import jakarta.persistence.*;

@Entity
public class SpotifyToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}