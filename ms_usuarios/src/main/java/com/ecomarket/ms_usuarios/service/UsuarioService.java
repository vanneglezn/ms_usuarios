package com.ecomarket.ms_usuarios.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ecomarket.ms_usuarios.model.Usuario;
import com.ecomarket.ms_usuarios.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<Usuario> listarUsuarios() {
        return repository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(UUID id) {
        return repository.findById(id);
    }

    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional
    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getRol() == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        Optional<Usuario> existente = repository.findByEmail(usuario.getEmail());
        if (existente.isPresent() && !existente.get().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        try {
            return repository.save(usuario);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al guardar el usuario: " + ex.getMessage(), ex);
        }
    }

    public void eliminarUsuario(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Usuario con ID no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
