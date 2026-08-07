package com.example.recordatorios_esp32.config;

import com.example.recordatorios_esp32.model.DispositivoToken;
import com.example.recordatorios_esp32.model.Recordatorio;
import com.example.recordatorios_esp32.repository.DispositivoTokenRepository;
import com.example.recordatorios_esp32.repository.RecordatorioRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class PushNotificationService {

    private static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");

    @Autowired
    private RecordatorioRepository recordatorioRepo;

    @Autowired
    private DispositivoTokenRepository tokenRepo;

    // Corre cada 60 segundos
    @Scheduled(fixedRate = 60000)
    public void revisarYNotificar() {
        LocalDate hoy = LocalDate.now(ZONA_COLOMBIA);
        LocalTime horaActual = LocalTime.now(ZONA_COLOMBIA);

        List<Recordatorio> vencidosSinNotificar =
                recordatorioRepo.findVencidosSinNotificar(hoy, horaActual);

        if (vencidosSinNotificar.isEmpty()) return;

        List<DispositivoToken> tokens = tokenRepo.findAll();

        for (Recordatorio recordatorio : vencidosSinNotificar) {
            for (DispositivoToken dispositivo : tokens) {
                enviarPush(dispositivo.getToken(), recordatorio);
            }
            recordatorio.setNotificadoPush(true);
            recordatorioRepo.save(recordatorio);
        }
    }

    private void enviarPush(String token, Recordatorio recordatorio) {
        try {
            Message mensaje = Message.builder()
                    .setToken(token)
                    .putData("titulo", recordatorio.getTitulo())
                    .putData("id", String.valueOf(recordatorio.getId()))
                    .build();

            FirebaseMessaging.getInstance().send(mensaje);
            System.out.println("Push enviado para recordatorio: " + recordatorio.getTitulo());
        } catch (Exception e) {
            System.out.println("Error enviando push: " + e.getMessage());
        }
    }
}