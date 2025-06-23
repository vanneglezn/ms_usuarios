package com.ecomarket.ms_usuarios.service;

import com.ecomarket.ms_usuarios.model.Rol;
import com.ecomarket.ms_usuarios.model.Usuario;
import com.ecomarket.ms_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    // UUIDs de ejemplo para las pruebas
    private final UUID USER_ID_1 = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");
    private final UUID USER_ID_2 = UUID.fromString("123e4567-e89b-12d3-a456-556642440001");
    private final UUID NON_EXISTENT_ID = UUID.fromString("123e4567-e89b-12d3-a456-000000000000");

    @BeforeEach
    void setUp() {
        // Inicializa los mocks antes de cada método de prueba
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarUsuarios() {
        // 1. Preparación (Arrange)
        Usuario usuario1 = new Usuario(USER_ID_1, "Juan Perez", "juan@example.com", "pass123", "Calle Falsa 123", "111222333", Rol.CLIENTE);
        Usuario usuario2 = new Usuario(USER_ID_2, "Maria Lopez", "maria@example.com", "pass456", "Avenida Siempre Viva", "444555666", Rol.VENDEDOR);
        List<Usuario> usuariosEsperados = Arrays.asList(usuario1, usuario2);

        when(usuarioRepository.findAll()).thenReturn(usuariosEsperados);

        // 2. Ejecución (Act)
        List<Usuario> resultado = usuarioService.listarUsuarios();

        // 3. Verificación (Assert)
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactlyInAnyOrder(usuario1, usuario2);
        // Verifica que el método findAll del repositorio fue llamado exactamente una vez
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testObtenerUsuarioPorIdExistente() {
        // 1. Preparación (Arrange)
        Usuario usuarioExistente = new Usuario(USER_ID_1, "Juan Perez", "juan@example.com", "pass123", "Calle Falsa 123", "111222333", Rol.CLIENTE);
        when(usuarioRepository.findById(USER_ID_1)).thenReturn(Optional.of(usuarioExistente));

        // 2. Ejecución (Act)
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(USER_ID_1);

        // 3. Verificación (Assert)
        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isEqualTo(usuarioExistente);
        verify(usuarioRepository, times(1)).findById(USER_ID_1);
    }

    @Test
    void testObtenerUsuarioPorIdNoExistente() {
        // 1. Preparación (Arrange)
        when(usuarioRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        // 2. Ejecución (Act)
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(NON_EXISTENT_ID);

        // 3. Verificación (Assert)
        assertThat(resultado).isNotPresent();
        verify(usuarioRepository, times(1)).findById(NON_EXISTENT_ID);
    }

    @Test
    void testObtenerUsuarioPorEmailExistente() {
        // 1. Preparación (Arrange)
        Usuario usuarioExistente = new Usuario(USER_ID_1, "Juan Perez", "juan@example.com", "pass123", "Calle Falsa 123", "111222333", Rol.CLIENTE);
        when(usuarioRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuarioExistente));

        // 2. Ejecución (Act)
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorEmail("juan@example.com");

        // 3. Verificación (Assert)
        assertThat(resultado).isPresent();
        assertThat(resultado.get()).isEqualTo(usuarioExistente);
        verify(usuarioRepository, times(1)).findByEmail("juan@example.com");
    }

    @Test
    void testObtenerUsuarioPorEmailNoExistente() {
        // 1. Preparación (Arrange)
        when(usuarioRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        // 2. Ejecución (Act)
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorEmail("noexiste@example.com");

        // 3. Verificación (Assert)
        assertThat(resultado).isNotPresent();
        verify(usuarioRepository, times(1)).findByEmail("noexiste@example.com");
    }

    @Test
    void testGuardarNuevoUsuarioExitoso() {
        // 1. Preparación (Arrange)
        Usuario nuevoUsuario = new Usuario(null, "Pedro Gomez", "pedro@example.com", "newpass", "Nueva Direccion", "777888999", Rol.CLIENTE);
        Usuario usuarioGuardado = new Usuario(USER_ID_1, "Pedro Gomez", "pedro@example.com", "newpass", "Nueva Direccion", "777888999", Rol.CLIENTE);

        when(usuarioRepository.findByEmail(nuevoUsuario.getEmail())).thenReturn(Optional.empty()); // No existe email
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        // 2. Ejecución (Act)
        Usuario resultado = usuarioService.guardarUsuario(nuevoUsuario);

        // 3. Verificación (Assert)
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(USER_ID_1);
        assertThat(resultado.getEmail()).isEqualTo("pedro@example.com");
        assertThat(resultado.getRol()).isEqualTo(Rol.CLIENTE);
        verify(usuarioRepository, times(1)).findByEmail(nuevoUsuario.getEmail());
        verify(usuarioRepository, times(1)).save(nuevoUsuario);
    }

    @Test
    void testGuardarUsuarioExistenteActualizar() {
        // 1. Preparación (Arrange)
        Usuario usuarioExistente = new Usuario(USER_ID_1, "Juan Perez", "juan@example.com", "oldpass", "Calle Falsa 123", "111222333", Rol.CLIENTE);
        Usuario usuarioActualizado = new Usuario(USER_ID_1, "Juan Perez Actualizado", "juan@example.com", "newpass", "Nueva Direccion", "111222333", Rol.CLIENTE);

        when(usuarioRepository.findByEmail(usuarioActualizado.getEmail())).thenReturn(Optional.of(usuarioExistente)); // Mismo email
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        // 2. Ejecución (Act)
        Usuario resultado = usuarioService.guardarUsuario(usuarioActualizado);

        // 3. Verificación (Assert)
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(USER_ID_1);
        assertThat(resultado.getNombre()).isEqualTo("Juan Perez Actualizado");
        verify(usuarioRepository, times(1)).findByEmail(usuarioActualizado.getEmail());
        verify(usuarioRepository, times(1)).save(usuarioActualizado);
    }

    @Test
    void testGuardarUsuarioConEmailExistenteParaOtroUsuarioLanzaExcepcion() {
        // 1. Preparación (Arrange)
        Usuario usuario1 = new Usuario(USER_ID_1, "Juan Perez", "juan@example.com", "pass1", "Dir1", "111", Rol.CLIENTE);
        Usuario usuarioNuevoConEmailExistente = new Usuario(USER_ID_2, "Otro Nombre", "juan@example.com", "pass2", "Dir2", "222", Rol.VENDEDOR);

        when(usuarioRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuario1)); // El email ya existe para usuario1

        // 2. Ejecución y 3. Verificación (Act & Assert)
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.guardarUsuario(usuarioNuevoConEmailExistente),
                "Se esperaba IllegalArgumentException al intentar guardar un usuario con email ya existente para otro ID"
        );
        assertThat(thrown.getMessage()).isEqualTo("Ya existe un usuario con ese correo");
        verify(usuarioRepository, times(1)).findByEmail("juan@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class)); // Asegura que save nunca fue llamado
    }

    @Test
    void testGuardarUsuarioConRolNuloLanzaExcepcion() {
        // 1. Preparación (Arrange)
        Usuario usuarioConRolNulo = new Usuario(null, "Test User", "test@example.com", "pass", "Dir", "123", null);

        // 2. Ejecución y 3. Verificación (Act & Assert)
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.guardarUsuario(usuarioConRolNulo),
                "Se esperaba IllegalArgumentException si el rol es nulo"
        );
        assertThat(thrown.getMessage()).isEqualTo("El rol es obligatorio");
        verify(usuarioRepository, never()).findByEmail(anyString()); // No debería llegar a verificar email
        verify(usuarioRepository, never()).save(any(Usuario.class)); // Asegura que save nunca fue llamado
    }

    @Test
    void testGuardarUsuarioLanzaRuntimeExceptionPorDataAccessException() {
        // 1. Preparación (Arrange)
        Usuario nuevoUsuario = new Usuario(null, "Pedro Gomez", "pedro@example.com", "newpass", "Nueva Direccion", "777888999", Rol.CLIENTE);

        when(usuarioRepository.findByEmail(nuevoUsuario.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenThrow(new DataIntegrityViolationException("Error de BD simulado"));

        // 2. Ejecución y 3. Verificación (Act & Assert)
        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> usuarioService.guardarUsuario(nuevoUsuario),
                "Se esperaba RuntimeException por DataAccessException"
        );
        assertThat(thrown.getMessage()).contains("Error al guardar el usuario");
        assertThat(thrown.getCause()).isInstanceOf(DataAccessException.class);
        verify(usuarioRepository, times(1)).findByEmail(nuevoUsuario.getEmail());
        verify(usuarioRepository, times(1)).save(nuevoUsuario);
    }

    @Test
    void testEliminarUsuarioExitoso() {
        // 1. Preparación (Arrange)
        when(usuarioRepository.existsById(USER_ID_1)).thenReturn(true);
        // Mockear el comportamiento de deleteById, no devuelve nada
        doNothing().when(usuarioRepository).deleteById(USER_ID_1);

        // 2. Ejecución (Act)
        usuarioService.eliminarUsuario(USER_ID_1);

        // 3. Verificación (Assert)
        // Verifica que existsById fue llamado y luego deleteById
        verify(usuarioRepository, times(1)).existsById(USER_ID_1);
        verify(usuarioRepository, times(1)).deleteById(USER_ID_1);
    }

    @Test
    void testEliminarUsuarioNoEncontradoLanzaExcepcion() {
        // 1. Preparación (Arrange)
        when(usuarioRepository.existsById(NON_EXISTENT_ID)).thenReturn(false);

        // 2. Ejecución y 3. Verificación (Act & Assert)
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.eliminarUsuario(NON_EXISTENT_ID),
                "Se esperaba IllegalArgumentException si el usuario no existe"
        );
        assertThat(thrown.getMessage()).isEqualTo("Usuario con ID no encontrado: " + NON_EXISTENT_ID);
        verify(usuarioRepository, times(1)).existsById(NON_EXISTENT_ID);
        // Asegura que deleteById nunca fue llamado
        verify(usuarioRepository, never()).deleteById(any(UUID.class));
    }
}