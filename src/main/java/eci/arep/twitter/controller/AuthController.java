package eci.arep.twitter.controller;

import eci.arep.twitter.Services.AuthService;
import eci.arep.twitter.Utils.JwtService;
import eci.arep.twitter.Utils.LoginRequest;
import eci.arep.twitter.Utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService){
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        return authService.authenticate(request.getUsername(), request.getPassword())
                .map(u -> {
                    String token = jwtService.generateToken(u.getUsername());
                    return ResponseEntity.ok(Map.of("token", token));
                })
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error","Credenciales inválidas")));
    }
}
