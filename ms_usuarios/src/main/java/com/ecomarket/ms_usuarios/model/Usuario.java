package com.ecomarket.ms_usuarios.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue
    private UUID id;

    private String nombre;
    private String email;
    private String contraseña;
    private String direccion;
    private String telefono;

    @Enumerated(EnumType.STRING)
    private Rol rol;
}
