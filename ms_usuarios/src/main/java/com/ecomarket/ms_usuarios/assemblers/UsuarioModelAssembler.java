package com.ecomarket.ms_usuarios.assemblers;

import com.ecomarket.ms_usuarios.controller.UsuarioControllerV2;
import com.ecomarket.ms_usuarios.dto.UsuarioModel;
import com.ecomarket.ms_usuarios.model.Usuario;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuarioModelAssembler extends RepresentationModelAssemblerSupport<Usuario, UsuarioModel> {

    public UsuarioModelAssembler() {
        super(UsuarioControllerV2.class, UsuarioModel.class);
    }

    @Override
    public @NonNull UsuarioModel toModel(@NonNull Usuario usuario) {
        UsuarioModel model = new UsuarioModel(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getDireccion(),
                usuario.getTelefono(),
                usuario.getRol()
        );

        model.add(linkTo(methodOn(UsuarioControllerV2.class).obtenerPorId(usuario.getId())).withSelfRel());
        model.add(linkTo(methodOn(UsuarioControllerV2.class).obtenerTodos()).withRel("usuarios"));
        model.add(linkTo(methodOn(UsuarioControllerV2.class).eliminarUsuario(usuario.getId())).withRel("eliminar"));
        model.add(linkTo(methodOn(UsuarioControllerV2.class).actualizarUsuario(usuario.getId(), usuario)).withRel("actualizar"));

        return model;
    }
}
