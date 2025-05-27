package com.ecomarket.ms_usuarios.controller;

import com.ecomarket.ms_usuarios.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.getOrDefault("email", "").trim();
        String contraseña = credentials.getOrDefault("contraseña", "").trim();

        if (email.isEmpty() || contraseña.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El email y la contraseña son obligatorios"));
        }

        return ResponseEntity.ok(service.login(email, contraseña));
    }
}
