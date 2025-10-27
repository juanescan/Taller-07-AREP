package eci.arep.twitter.Config;

import eci.arep.twitter.Utils.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 🚫 Desactiva CSRF para permitir peticiones POST desde Postman o front
                .csrf(csrf -> csrf.disable())

                // 🔐 Define las rutas públicas y protegidas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll() // login/register
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll() // base de datos H2
                        .requestMatchers(new AntPathRequestMatcher("/login.html")).permitAll() // login page
                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll() // estilos
                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll() // scripts
                        .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll() // imágenes
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll() // ruta raíz
                        .anyRequest().permitAll() // todo lo demás requiere autenticación
                )

                // ⚙️ Permite ver la consola H2 (sin esto lanza error de frame)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 🧩 Añade el filtro JWT antes del UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    // 🧠 Expone el AuthenticationManager como bean para usarlo en AuthService o controladores
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 🔑 Bean del encriptador de contraseñas (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
