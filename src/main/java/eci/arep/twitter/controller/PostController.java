package eci.arep.twitter.controller;

import eci.arep.twitter.controller.dto.CreatePostRequest;
import eci.arep.twitter.controller.dto.UpdatePostRequest;
import eci.arep.twitter.model.Post;
import eci.arep.twitter.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@Validated @RequestBody CreatePostRequest req,
                       @AuthenticationPrincipal Object principal,
                       Authentication authentication) {
        String authorId = extractAuthorId(principal, authentication);
        return service.create(req.getContent(), authorId, req.getParentPostId());
    }

    @GetMapping
    public Page<Post> list(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size) {
        return service.listAll(page, size);
    }

    @GetMapping("/{id}")
    public Post getOne(@PathVariable String id) {
        return service.getOne(id);
    }

    @GetMapping("/{id}/replies")
    public Page<Post> listReplies(@PathVariable String id,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        return service.listReplies(id, page, size);
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable String id,
                       @Validated @RequestBody UpdatePostRequest req,
                       @AuthenticationPrincipal Object principal,
                       Authentication authentication) {
        String authorId = extractAuthorId(principal, authentication);
        return service.update(id, req.getContent(), authorId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id,
                       @AuthenticationPrincipal Object principal,
                       Authentication authentication) {
        String authorId = extractAuthorId(principal, authentication);
        service.delete(id, authorId);
    }

    @PostMapping("/{id}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    public Post replyTo(@PathVariable String id,
                        @Validated @RequestBody UpdatePostRequest req,
                        @AuthenticationPrincipal Object principal,
                        Authentication authentication) {
        // Reusamos UpdatePostRequest solo con content
        String authorId = extractAuthorId(principal, authentication);
        // Validar existencia del padre dentro del servicio
        return service.create(req.getContent(), authorId, id);
    }

    private String extractAuthorId(Object principal, Authentication authentication) {
        String email = tryEmailFromJwt(principal);
        if (email != null) return email;
        email = tryEmailFromOidcUser(principal);
        if (email != null) return email;
        if (authentication != null) {
            email = tryEmailFromOidcUser(authentication.getPrincipal());
            if (email != null) return email;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se pudo determinar el usuario autenticado");
    }

    private String tryEmailFromJwt(Object principal) {
        if (principal instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            if (email != null && !email.isBlank()) return email;
            String username = jwt.getClaimAsString("cognito:username");
            if (username != null && !username.isBlank()) return username;
            String sub = jwt.getSubject();
            if (sub != null && !sub.isBlank()) return sub;
        }
        return null;
    }

    private String tryEmailFromOidcUser(Object principal) {
        if (principal instanceof OidcUser oidcUser) {
            String email = oidcUser.getEmail();
            if (email != null && !email.isBlank()) return email;
            Object username = oidcUser.getClaims().get("cognito:username");
            if (username instanceof String s && !s.isBlank()) return s;
            String sub = oidcUser.getSubject();
            if (sub != null && !sub.isBlank()) return sub;
        }
        return null;
    }
}
