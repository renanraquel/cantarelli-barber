package com.gamecatarellibarber.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final AuthProperties authProperties;

    public AuthUserDetailsService(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = authProperties.getUsers().get(username);
        if (password == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        String role = authProperties.getRole(username);
        String authority = "ADMIN".equalsIgnoreCase(role) ? "ROLE_ADMIN" : "ROLE_BARBER";
        return new User(username, password, List.of(new SimpleGrantedAuthority(authority)));
    }
}
