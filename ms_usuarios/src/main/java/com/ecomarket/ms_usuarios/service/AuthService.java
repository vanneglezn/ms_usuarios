package com.ecomarket.ms_usuarios.service;

import com.ecomarket.ms_usuarios.model.Usuario;
import com.ecomarket.ms_usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository repository;

    public AuthService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> login(String email, String contraseña) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!usuario.getContraseña().equals(contraseña)) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        String token = "fake-jwt-" + UUID.randomUUID(); // Token simulado
        LocalDateTime expiracion = LocalDateTime.now().plusHours(2);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("expiracion", expiracion);
        response.put("usuario", usuario.getEmail());

        return response;
    }
}
