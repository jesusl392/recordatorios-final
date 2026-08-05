package com.example.recordatorios_esp32.controller;

import com.example.recordatorios_esp32.model.Recordatorio;
import com.example.recordatorios_esp32.repository.RecordatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/recordatorios")
public class RecordatorioController {

    private static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");

    @Autowired
    private RecordatorioRepository repo;

    @GetMapping("/pendientes")
    public List<Recordatorio> pendientes() {
        LocalDate hoy = LocalDate.now(ZONA_COLOMBIA);
        LocalTime horaActual = LocalTime.now(ZONA_COLOMBIA);
        return repo.findVencidos(hoy, horaActual);
    }

    @PostMapping
    public Recordatorio crear(@RequestBody Recordatorio r) {
        return repo.save(r);
    }

    @PutMapping("/{id}/completar")
    public Recordatorio completar(@PathVariable Long id) {
        Recordatorio r = repo.findById(id).orElseThrow();
        r.setMostrado(true);
        r.setActivo(false);
        return repo.save(r);
    }

    @GetMapping("/hoy")
    public List<Recordatorio> deHoy() {
        LocalDate hoy = LocalDate.now(ZONA_COLOMBIA);
        return repo.findDeHoy(hoy);
    }

    @PutMapping("/{id}/posponer")
    public Recordatorio posponer(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int minutos,
            @RequestParam(defaultValue = "0") int dias
    ) {
        Recordatorio r = repo.findById(id).orElseThrow();

        // Calculamos el nuevo momento a partir de AHORA (no de la hora original),
        // sumándole los minutos/días que el usuario eligió en el ESP32
        LocalDateTime nuevoMomento = LocalDateTime.now(ZONA_COLOMBIA)
                .plusMinutes(minutos)
                .plusDays(dias);

        r.setFecha(nuevoMomento.toLocalDate());
        r.setHora(nuevoMomento.toLocalTime());
        r.setMostrado(false); // para que vuelva a aparecer cuando llegue esa nueva hora
        r.setActivo(true);    // se queda activo, solo se movió la fecha/hora

        return repo.save(r);
    }
}