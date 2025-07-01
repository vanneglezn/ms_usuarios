package com.ecomarket.ms_usuarios.dto;

import com.ecomarket.ms_usuarios.model.Rol;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

public class UsuarioModel extends RepresentationModel<UsuarioModel> {
    private UUID id;
    private String nombre;
    private String email;
    private String direccion;
    private String telefono;
    private Rol rol;

    public UsuarioModel(UUID id, String nombre, String email, String direccion, String telefono, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.rol = rol;
    }

    // Getters
    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getDireccion() { return direccion; }
    public String getTelefono() { return telefono; }
    public Rol getRol() { return rol; }
}
