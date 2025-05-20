package com.ecomarket.ms_usuarios.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ecomarket.ms_usuarios.model.Usuario;
import com.ecomarket.ms_usuarios.repository.UsuarioRepository;

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

    public Usuario guardarUsuario(Usuario usuario) {
        return repository.save(usuario);
    }

    public void eliminarUsuario(UUID id) {
        repository.deleteById(id);
    }

public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
    return repository.findByEmail(email);
}

    }

