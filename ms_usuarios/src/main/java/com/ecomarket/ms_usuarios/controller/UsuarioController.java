package com.ecomarket.ms_usuarios.controller;

import com.ecomarket.ms_usuarios.model.Usuario;
import com.ecomarket.ms_usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return ResponseEntity.ok(service.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable UUID id) {
        return service.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(service.guardarUsuario(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody Usuario usuario
    ) {
        return service.obtenerUsuarioPorId(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNombre(usuario.getNombre());
                    usuarioExistente.setEmail(usuario.getEmail());
                    usuarioExistente.setContraseña(usuario.getContraseña()); // <- CAMBIO AQUÍ
                    usuarioExistente.setDireccion(usuario.getDireccion());
                    usuarioExistente.setTelefono(usuario.getTelefono());
                    usuarioExistente.setRol(usuario.getRol());
                    return ResponseEntity.ok(service.guardarUsuario(usuarioExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable UUID id) {
        if (!service.obtenerUsuarioPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
