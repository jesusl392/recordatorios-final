package com.example.recordatorios_esp32.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Component
public class FirebaseConfig {

    @PostConstruct
    public void inicializar() {
        try {
            String credencialesJson = System.getenv("FIREBASE_CREDENTIALS");

            if (credencialesJson == null || credencialesJson.isBlank()) {
                System.out.println("FIREBASE_CREDENTIALS no configurada, Firebase no se inicializó.");
                return;
            }

            if (FirebaseApp.getApps().isEmpty()) {
                GoogleCredentials credenciales = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(credencialesJson.getBytes(StandardCharsets.UTF_8))
                );

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credenciales)
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase inicializado correctamente.");
            }
        } catch (Exception e) {
            System.out.println("Error inicializando Firebase: " + e.getMessage());
        }
    }
}