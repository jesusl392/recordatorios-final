package com.example.recordatorios_esp32.controller;

import com.example.recordatorios_esp32.model.Recordatorio;
import com.example.recordatorios_esp32.repository.RecordatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
}