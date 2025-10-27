package eci.arep.twitter.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/muro")
    public String muro(@AuthenticationPrincipal OidcUser oidcUser,
                       @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient client,
                       Model model) {
        String email = oidcUser != null ? oidcUser.getEmail() : null;
        String idToken = oidcUser != null ? oidcUser.getIdToken().getTokenValue() : null;
        String accessToken = client != null ? client.getAccessToken().getTokenValue() : null;
        model.addAttribute("email", email);
        // Usaremos id_token para hablar con el backend que espera claim email
        model.addAttribute("token", idToken != null ? idToken : accessToken);
        return "muro";
    }
}
