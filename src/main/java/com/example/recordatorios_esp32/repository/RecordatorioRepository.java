package com.example.recordatorios_esp32.repository;

import com.example.recordatorios_esp32.model.Recordatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {
    List<Recordatorio> findByActivoTrueAndMostradoFalse();
}