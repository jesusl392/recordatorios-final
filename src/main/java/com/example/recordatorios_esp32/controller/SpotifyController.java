package com.example.recordatorios_esp32.controller;

import com.example.recordatorios_esp32.model.SpotifyToken;
import com.example.recordatorios_esp32.repository.SpotifyTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {

    @Autowired
    private SpotifyTokenRepository tokenRepo;

    private final String clientId = System.getenv("SPOTIFY_CLIENT_ID");
    private final String clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET");
    private final String redirectUri = "https://recordatorios-final.onrender.com/spotify/callback";

    @GetMapping("/login")
    public RedirectView login() {
        String scope = "user-read-currently-playing user-read-playback-state";
        String url = "https://accounts.spotify.com/authorize?response_type=code" +
                "&client_id=" + clientId +
                "&scope=" + scope.replace(" ", "%20") +
                "&redirect_uri=" + redirectUri;
        return new RedirectView(url);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code) {
        RestTemplate rest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        String auth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        headers.set("Authorization", "Basic " + auth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        Map<String, Object> respuesta = rest.postForObject(
                "https://accounts.spotify.com/api/token", request, Map.class);

        String refreshToken = (String) respuesta.get("refresh_token");

        SpotifyToken token = new SpotifyToken();
        token.setRefreshToken(refreshToken);
        tokenRepo.save(token);

        return "Spotify conectado correctamente, ya puedes cerrar esta pestaña.";
    }

    private String obtenerAccessToken() {
        List<SpotifyToken> tokens = tokenRepo.findAll();
        if (tokens.isEmpty()) return null;
        String refreshToken = tokens.get(0).getRefreshToken();

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String auth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        headers.set("Authorization", "Basic " + auth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        Map<String, Object> respuesta = rest.postForObject(
                "https://accounts.spotify.com/api/token", request, Map.class);

        return (String) respuesta.get("access_token");
    }

    @GetMapping("/actual")
    public Map<String, Object> actual() {
        String accessToken = obtenerAccessToken();
        if (accessToken == null) {
            return Map.of("reproduciendo", false);
        }

        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> respuesta = rest.exchange(
                    "https://api.spotify.com/v1/me/player/currently-playing",
                    HttpMethod.GET, request, Map.class);

            if (respuesta.getStatusCode() == HttpStatus.NO_CONTENT || respuesta.getBody() == null) {
                return Map.of("reproduciendo", false);
            }

            Map<String, Object> item = (Map<String, Object>) respuesta.getBody().get("item");
            String nombreCancion = (String) item.get("name");
            List<Map<String, Object>> artistas = (List<Map<String, Object>>) item.get("artists");
            String artista = (String) artistas.get(0).get("name");
            boolean reproduciendo = (Boolean) respuesta.getBody().get("is_playing");

            return Map.of(
                    "reproduciendo", reproduciendo,
                    "cancion", nombreCancion,
                    "artista", artista
            );
        } catch (Exception e) {
            return Map.of("reproduciendo", false);
        }
    }
}