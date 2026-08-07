package com.example.recordatorios_esp32.repository;

import com.example.recordatorios_esp32.model.DispositivoToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DispositivoTokenRepository extends JpaRepository<DispositivoToken, Long> {
    List<DispositivoToken> findAll();
}