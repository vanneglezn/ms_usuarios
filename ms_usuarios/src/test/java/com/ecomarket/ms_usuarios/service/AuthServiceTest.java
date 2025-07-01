package com.ecomarket.ms_usuarios.service;

import com.ecomarket.ms_usuarios.model.Rol;
import com.ecomarket.ms_usuarios.model.Usuario;
import com.ecomarket.ms_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UsuarioRepository repository;
    private AuthService authService;

    private final Usuario usuarioEjemplo = new Usuario(
            UUID.randomUUID(),
            "Juan Pérez",
            "juan@example.com",
            "password123", // contraseña simulada
            "Calle Falsa 123",
            "123456789",
            Rol.CLIENTE
    );

    @BeforeEach
    void setUp() {
        repository = mock(UsuarioRepository.class);
        authService = new AuthService(repository);
    }

    @Test
    void loginExitoso_devuelveTokenYDatos() {
        // Arrange
        when(repository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuarioEjemplo));

        // Act
        Map<String, Object> result = authService.login("juan@example.com", "password123");

        // Assert
        assertNotNull(result.get("token"));
        assertNotNull(result.get("expiracion"));
        assertEquals("juan@example.com", result.get("usuario"));
    }

    @Test
    void loginConEmailNoExistente_lanzaExcepcion() {
        // Arrange
        when(repository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authService.login("noexiste@example.com", "password123"));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void loginConContraseñaIncorrecta_lanzaExcepcion() {
        // Arrange
        when(repository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuarioEjemplo));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authService.login("juan@example.com", "otraContraseña"));
        assertEquals("Credenciales incorrectas", ex.getMessage());
    }
}
