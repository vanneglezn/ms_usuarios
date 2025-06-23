package com.ecomarket.ms_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecomarket.ms_usuarios.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class) // Prueba solo el AuthController
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService; // Mock del AuthService

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLoginExitoso() throws Exception {
        // 1. Preparación (Arrange)
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "test@example.com");
        credentials.put("contraseña", "password123");

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("token", "fake-jwt-token");
        serviceResponse.put("usuario", "test@example.com");

        when(authService.login("test@example.com", "password123")).thenReturn(serviceResponse);

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.usuario").value("test@example.com"));

        verify(authService, times(1)).login("test@example.com", "password123");
    }

    @Test
    void testLoginConCredencialesInvalidasDesdeServicio() throws Exception {
        // 1. Preparación (Arrange)
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "wrong@example.com");
        credentials.put("contraseña", "wrongpass");

        // Simula que el servicio lanza una excepción por credenciales incorrectas
        when(authService.login("wrong@example.com", "wrongpass"))
                .thenThrow(new IllegalArgumentException("Credenciales incorrectas"));

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest()) // Espera 400 Bad Request
                .andExpect(jsonPath("$.message").value("Credenciales incorrectas")); // Verifica el mensaje de error del GlobalExceptionHandler

        verify(authService, times(1)).login("wrong@example.com", "wrongpass");
    }

    @Test
    void testLoginConEmailVacio() throws Exception {
        // 1. Preparación (Arrange)
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "");
        credentials.put("contraseña", "password123");

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest()) // Espera 400 Bad Request
                .andExpect(jsonPath("$.error").value("El email y la contraseña son obligatorios")); // Verifica el mensaje de error directamente del controlador

        verify(authService, never()).login(anyString(), anyString()); // Asegura que el servicio nunca fue llamado
    }

    @Test
    void testLoginConContrasenaVacia() throws Exception {
        // 1. Preparación (Arrange)
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "test@example.com");
        credentials.put("contraseña", "");

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest()) // Espera 400 Bad Request
                .andExpect(jsonPath("$.error").value("El email y la contraseña son obligatorios"));

        verify(authService, never()).login(anyString(), anyString()); // Asegura que el servicio nunca fue llamado
    }
}