package com.etec.zl.conecta.Infraestructure.Security.Models;

import com.etec.zl.conecta.Domain.Entities.Users.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record UserPrincipal(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getTipo().name()));
    }

    @Override
    public @Nullable String getPassword() {
        return user.getSenha().password();
    }

    @Override
    public String getUsername() {
        return user.getId();
    }

    public String getId(){
        return user.getId();
    }
}
