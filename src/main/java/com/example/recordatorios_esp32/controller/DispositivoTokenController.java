package com.example.recordatorios_esp32.controller;

import com.example.recordatorios_esp32.model.DispositivoToken;
import com.example.recordatorios_esp32.repository.DispositivoTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dispositivos")
public class DispositivoTokenController {

    @Autowired
    private DispositivoTokenRepository repo;

    @PostMapping("/token")
    public void registrarToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isBlank()) return;

        // Evitamos guardar el mismo token 2 veces
        boolean yaExiste = repo.findAll().stream()
                .anyMatch(d -> d.getToken().equals(token));

        if (!yaExiste) {
            DispositivoToken nuevo = new DispositivoToken();
            nuevo.setToken(token);
            repo.save(nuevo);
        }
    }

    @GetMapping("/tokens")
    public List<DispositivoToken> listar() {
        return repo.findAll();
    }
}