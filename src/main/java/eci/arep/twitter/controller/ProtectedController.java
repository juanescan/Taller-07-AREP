

package eci.arep.twitter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
public class ProtectedController {

    @GetMapping("/protected")
    public String protectedEndpoint(@AuthenticationPrincipal UserDetails user) {
        return "Hola " + user.getUsername() + ", ¡accediste a un endpoint protegido!";
    }
}

