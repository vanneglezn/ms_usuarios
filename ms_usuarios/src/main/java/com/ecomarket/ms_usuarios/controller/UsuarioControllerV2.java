package com.ecomarket.ms_usuarios.controller;

import com.ecomarket.ms_usuarios.assemblers.UsuarioModelAssembler;
import com.ecomarket.ms_usuarios.dto.UsuarioModel;
import com.ecomarket.ms_usuarios.model.Usuario;
import com.ecomarket.ms_usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/usuarios") // 👈 NUEVA VERSIÓN
public class UsuarioControllerV2 {

    private final UsuarioService service;
    private final UsuarioModelAssembler assembler;

    public UsuarioControllerV2(UsuarioService service, UsuarioModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<UsuarioModel>> obtenerTodos() {
        List<Usuario> usuarios = service.listarUsuarios();
        List<UsuarioModel> modelos = usuarios.stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(modelos, linkTo(methodOn(UsuarioControllerV2.class).obtenerTodos()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> obtenerPorId(@PathVariable UUID id) {
        return service.obtenerUsuarioPorId(id)
                .map(usuario -> ResponseEntity.ok(assembler.toModel(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioModel> crearUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevo = service.guardarUsuario(usuario);
        return ResponseEntity.ok(assembler.toModel(nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioModel> actualizarUsuario(@PathVariable UUID id,
                                                          @Valid @RequestBody Usuario usuario) {
        return service.obtenerUsuarioPorId(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNombre(usuario.getNombre());
                    usuarioExistente.setEmail(usuario.getEmail());
                    usuarioExistente.setContraseña(usuario.getContraseña());
                    usuarioExistente.setDireccion(usuario.getDireccion());
                    usuarioExistente.setTelefono(usuario.getTelefono());
                    usuarioExistente.setRol(usuario.getRol());
                    Usuario actualizado = service.guardarUsuario(usuarioExistente);
                    return ResponseEntity.ok(assembler.toModel(actualizado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable UUID id) {
        return service.obtenerUsuarioPorId(id)
        .map(usuario -> {
            service.eliminarUsuario(id);
            return usuario;
        })
        .map(u -> ResponseEntity.noContent().<Void>build())
        .orElse(ResponseEntity.notFound().build());

    }
}
