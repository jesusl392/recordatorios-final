package com.example.recordatorios_esp32.controller;

import com.example.recordatorios_esp32.model.Recordatorio;
import com.example.recordatorios_esp32.repository.RecordatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/recordatorios")
public class RecordatorioController {

    @Autowired
    private RecordatorioRepository repo;

    @GetMapping("/pendientes")
    public List<Recordatorio> pendientes() {
        return repo.findByActivoTrueAndMostradoFalse();
    }

    @PostMapping
    public Recordatorio crear(@RequestBody Recordatorio r) {
        return repo.save(r);
    }

    @PutMapping("/{id}/completar")
    public Recordatorio completar(@PathVariable Long id) {
        Recordatorio r = repo.findById(id).orElseThrow();
        r.setMostrado(true);
        return repo.save(r);
    }
}