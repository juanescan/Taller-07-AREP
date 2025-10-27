package eci.arep.twitter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/login", "/oauth2/**", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/muro").authenticated()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            // Login con OAuth2 (Cognito) y redirección al muro luego de autenticarse
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/muro", true)
            )
            // Mantener soporte para JWT en /api/** (por Postman u otros clientes)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
