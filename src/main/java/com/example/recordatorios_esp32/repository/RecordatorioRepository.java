package com.example.recordatorios_esp32.repository;

import com.example.recordatorios_esp32.model.Recordatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

    List<Recordatorio> findByActivoTrueAndMostradoFalse();

    @Query("SELECT r FROM Recordatorio r WHERE r.activo = true AND r.mostrado = false " +
            "AND (r.fecha < :hoy OR (r.fecha = :hoy AND r.hora <= :horaActual))")
    List<Recordatorio> findVencidos(@Param("hoy") LocalDate hoy, @Param("horaActual") LocalTime horaActual);
    @Query("SELECT r FROM Recordatorio r WHERE r.activo = true AND r.fecha = :hoy ORDER BY r.hora ASC")
    List<Recordatorio> findDeHoy(@Param("hoy") LocalDate hoy);
}