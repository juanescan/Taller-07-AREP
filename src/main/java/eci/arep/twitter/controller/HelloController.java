package eci.arep.twitter.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/private/hello")
    public Map<String, String> privateHello(
            @AuthenticationPrincipal OidcUser oidcUser,
            OAuth2AuthenticationToken authToken
    ) {
        // Obtener el id_token directamente
        String idToken = oidcUser.getIdToken().getTokenValue();

        return Map.of(
                "name", oidcUser.getFullName(),
                "email", oidcUser.getEmail(),
                "id_token", idToken
        );
    }

    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hello public user!";
    }

    @GetMapping("/token")
public String showToken(@AuthenticationPrincipal OidcUser oidcUser) {
    // Devuelve el JWT completo
    return oidcUser.getIdToken().getTokenValue();
}
}
