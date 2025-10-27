package eci.arep.twitter.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs = 1000L * 60 * 60 * 24; // 24 horas

    public JwtUtil(@Value("${jwt.secret:default_dev_secret_must_change_please_use_env}") String secret) {
        byte[] keyBytes = Arrays.copyOf(secret.getBytes(StandardCharsets.UTF_8), 32); // 256 bits
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("⚠️ Token expirado");
        } catch (UnsupportedJwtException e) {
            System.out.println("⚠️ Token no soportado");
        } catch (MalformedJwtException e) {
            System.out.println("⚠️ Token mal formado");
        } catch (SecurityException e) {
            System.out.println("⚠️ Firma JWT inválida");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Token vacío o nulo");
        }
        return false;
    }
}
