package com.ecomarket.ms_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecomarket.ms_usuarios.model.Rol;
import com.ecomarket.ms_usuarios.model.Usuario;
import com.ecomarket.ms_usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class) // Indica que se probará solo el UsuarioController
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular peticiones HTTP

    @MockBean
    private UsuarioService usuarioService; // Mock del servicio para aislar el controlador

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos Java a JSON y viceversa

    // UUIDs de ejemplo para las pruebas
    private final UUID USER_ID_1 = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
    private final UUID NON_EXISTENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-000000000000");

    private Usuario testUsuario1;
    private Usuario testUsuario2;

    @BeforeEach
    void setUp() {
        testUsuario1 = new Usuario(USER_ID_1, "Juan Perez", "juan@example.com", "pass123", "Calle Falsa 123", "111222333", Rol.CLIENTE);
        testUsuario2 = new Usuario(UUID.fromString("123e4567-e89b-12d3-a456-556642440001"), "Maria Lopez", "maria@example.com", "pass456", "Avenida Siempre Viva", "444555666", Rol.VENDEDOR);
    }

    @Test
    void testObtenerTodos() throws Exception {
        // 1. Preparación (Arrange)
        when(usuarioService.listarUsuarios()).thenReturn(Arrays.asList(testUsuario1, testUsuario2));

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(get("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").value(testUsuario1.getNombre()))
                .andExpect(jsonPath("$[1].email").value(testUsuario2.getEmail()));

        verify(usuarioService, times(1)).listarUsuarios(); // Verifica que el servicio fue llamado
    }

    @Test
    void testObtenerPorIdExistente() throws Exception {
        // 1. Preparación (Arrange)
        when(usuarioService.obtenerUsuarioPorId(USER_ID_1)).thenReturn(Optional.of(testUsuario1));

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(get("/api/usuarios/{id}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID_1.toString()))
                .andExpect(jsonPath("$.nombre").value(testUsuario1.getNombre()));

        verify(usuarioService, times(1)).obtenerUsuarioPorId(USER_ID_1);
    }

    @Test
    void testObtenerPorIdNoExistente() throws Exception {
        // 1. Preparación (Arrange)
        when(usuarioService.obtenerUsuarioPorId(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(get("/api/usuarios/{id}", NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera un 404 Not Found

        verify(usuarioService, times(1)).obtenerUsuarioPorId(NON_EXISTENT_ID);
    }

    @Test
    void testCrearUsuarioExitoso() throws Exception {
        // 1. Preparación (Arrange)
        Usuario nuevoUsuario = new Usuario(null, "Carlos Ruiz", "carlos@example.com", "pass7890", "Av. Siempre Viva 123", "555111222", Rol.ADMINISTRADOR);
        Usuario usuarioGuardado = new Usuario(USER_ID_1, "Carlos Ruiz", "carlos@example.com", "pass7890", "Av. Siempre Viva 123", "555111222", Rol.ADMINISTRADOR);

        when(usuarioService.guardarUsuario(any(Usuario.class))).thenReturn(usuarioGuardado);

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoUsuario))) // Convierte el objeto a JSON para el cuerpo de la petición
                .andExpect(status().isOk()) // Espera un 200 OK
                .andExpect(jsonPath("$.id").value(USER_ID_1.toString()))
                .andExpect(jsonPath("$.nombre").value(nuevoUsuario.getNombre()));

        verify(usuarioService, times(1)).guardarUsuario(any(Usuario.class));
    }

    @Test
    void testCrearUsuarioConValidacionFalla() throws Exception {
        // 1. Preparación (Arrange)
        // Usuario inválido: nombre muy corto, email inválido, contraseña muy corta, rol nulo
        Usuario usuarioInvalido = new Usuario(null, "C", "email-invalido", "short", null, "123", null);

        // No mockeamos el servicio para un caso de validación en el controlador,
        // ya que la validación `@Valid` se ejecuta antes de llamar al servicio.
        // Esperamos que el controlador mismo rechace la petición con 400 Bad Request.

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioInvalido)))
                .andExpect(status().isBadRequest()) // Espera un 400 Bad Request
                .andExpect(jsonPath("$").isArray()); // Espera una lista de errores de validación del GlobalExceptionHandler

        verify(usuarioService, never()).guardarUsuario(any(Usuario.class)); // Asegura que el servicio nunca fue llamado
    }

    @Test
    void testActualizarUsuarioExitoso() throws Exception {
        // 1. Preparación (Arrange)
        Usuario usuarioActualizadoDatos = new Usuario(USER_ID_1, "Juan Perez Editado", "juan.editado@example.com", "newpass123", "Nueva Dir 456", "999888777", Rol.VENDEDOR);

        // Mockear que el usuario existe antes de intentar actualizar
        when(usuarioService.obtenerUsuarioPorId(USER_ID_1)).thenReturn(Optional.of(testUsuario1));
        // Mockear que el servicio guarda el usuario actualizado
        when(usuarioService.guardarUsuario(any(Usuario.class))).thenReturn(usuarioActualizadoDatos);

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(put("/api/usuarios/{id}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioActualizadoDatos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID_1.toString()))
                .andExpect(jsonPath("$.nombre").value("Juan Perez Editado"))
                .andExpect(jsonPath("$.email").value("juan.editado@example.com"));

        verify(usuarioService, times(1)).obtenerUsuarioPorId(USER_ID_1);
        verify(usuarioService, times(1)).guardarUsuario(any(Usuario.class));
    }

    @Test
    void testActualizarUsuarioNoExistente() throws Exception {
        // 1. Preparación (Arrange)
        Usuario usuarioActualizadoDatos = new Usuario(NON_EXISTENT_ID, "Usuario No Existente", "noexistente@example.com", "validpass123", "dir", "987654321", Rol.CLIENTE);
        when(usuarioService.obtenerUsuarioPorId(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(put("/api/usuarios/{id}", NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioActualizadoDatos)))
                .andExpect(status().isNotFound()); // Espera un 404 Not Found

        verify(usuarioService, times(1)).obtenerUsuarioPorId(NON_EXISTENT_ID);
        verify(usuarioService, never()).guardarUsuario(any(Usuario.class)); // Asegura que el servicio de guardar nunca fue llamado
    }

    @Test
    void testEliminarUsuarioExitoso() throws Exception {
        // 1. Preparación (Arrange)
        when(usuarioService.obtenerUsuarioPorId(USER_ID_1)).thenReturn(Optional.of(testUsuario1)); // Simula que el usuario existe
        doNothing().when(usuarioService).eliminarUsuario(USER_ID_1); // Mockea el método void del servicio

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(delete("/api/usuarios/{id}", USER_ID_1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Espera un 204 No Content

        verify(usuarioService, times(1)).obtenerUsuarioPorId(USER_ID_1);
        verify(usuarioService, times(1)).eliminarUsuario(USER_ID_1);
    }

    @Test
    void testEliminarUsuarioNoExistente() throws Exception {
        // 1. Preparación (Arrange)
        when(usuarioService.obtenerUsuarioPorId(NON_EXISTENT_ID)).thenReturn(Optional.empty()); // Simula que el usuario NO existe

        // 2. Ejecución y 3. Verificación (Act & Assert)
        mockMvc.perform(delete("/api/usuarios/{id}", NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera un 404 Not Found

        verify(usuarioService, times(1)).obtenerUsuarioPorId(NON_EXISTENT_ID);
        verify(usuarioService, never()).eliminarUsuario(any(UUID.class)); // Asegura que el servicio de eliminar nunca fue llamado
    }
}