package eci.arep.twitter.controller;

import eci.arep.twitter.services.AuthService;
import eci.arep.twitter.utils.LoginRequest;
import eci.arep.twitter.utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(Map.of("message", "Usuario registrado con éxito"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Map<String, String> tokens = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(tokens);
    }
}
