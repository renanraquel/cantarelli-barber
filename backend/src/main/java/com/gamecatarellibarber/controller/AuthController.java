package com.gamecatarellibarber.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import com.gamecatarellibarber.config.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          SecurityContextRepository securityContextRepository,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.jwtService = jwtService;
    }

    public record LoginRequest(String username, String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest,
                                   HttpServletResponse httpResponse) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            HttpSession session = httpRequest.getSession(true);
            String sessionId = session.getId();
            log.info("[AUTH] login ok user={} sessionId={} sessionNew={}", request.username(), sessionId, session.isNew());

            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            log.info("[AUTH] saveContext done sessionId={}", sessionId);

            String role = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> a.equals("ROLE_ADMIN")) ? "ADMIN" : "BARBER";
            String token = jwtService.generateToken(request.username(), role);
            return ResponseEntity.ok(Map.of("username", request.username(), "role", role, "token", token));
        } catch (AuthenticationException e) {
            log.warn("[AUTH] login failed user={}", request.username());
            return ResponseEntity.status(401).body(Map.of("error", "Usuário ou senha inválidos"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        String sessionId = session != null ? session.getId() : null;
        String cookieHeader = httpRequest.getHeader("Cookie");
        boolean hasCookie = cookieHeader != null && !cookieHeader.isBlank();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth != null ? auth.getPrincipal() : null;
        boolean authenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(principal);

        log.info("[AUTH] /me sessionId={} hasCookie={} principal={} authenticated={}",
                sessionId, hasCookie, principal, authenticated);

        if (!authenticated) {
            log.warn("[AUTH] /me 401 sessionId={} hasCookie={} principal={}", sessionId, hasCookie, principal);
            return ResponseEntity.status(401).body(Map.of("error", "Não autenticado"));
        }
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN")) ? "ADMIN" : "BARBER";
        return ResponseEntity.ok(Map.of("username", auth.getName(), "role", role));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
