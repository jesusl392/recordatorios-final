package com.example.recordatorios_esp32.repository;

import com.example.recordatorios_esp32.model.SpotifyToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotifyTokenRepository extends JpaRepository<SpotifyToken, Long> {
}