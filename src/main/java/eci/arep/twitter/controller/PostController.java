package eci.arep.twitter.controller;

import eci.arep.twitter.controller.dto.CreatePostRequest;
import eci.arep.twitter.controller.dto.UpdatePostRequest;
import eci.arep.twitter.model.Post;
import eci.arep.twitter.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
                       @AuthenticationPrincipal Jwt jwt) {
        String authorId = extractAuthorId(jwt);
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
                       @AuthenticationPrincipal Jwt jwt) {
        String authorId = extractAuthorId(jwt);
        return service.update(id, req.getContent(), authorId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String authorId = extractAuthorId(jwt);
        service.delete(id, authorId);
    }

    @PostMapping("/{id}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    public Post replyTo(@PathVariable String id,
                        @Validated @RequestBody UpdatePostRequest req,
                        @AuthenticationPrincipal Jwt jwt) {
        // Reusamos UpdatePostRequest solo con content
        String authorId = extractAuthorId(jwt);
        // Validar existencia del padre dentro del servicio
        return service.create(req.getContent(), authorId, id);
    }

    private String extractAuthorId(Jwt jwt) {
        if (jwt == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) return email;
        String username = jwt.getClaimAsString("cognito:username");
        if (username != null && !username.isBlank()) return username;
        String sub = jwt.getSubject();
        if (sub != null && !sub.isBlank()) return sub;
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se pudo determinar el usuario del token");
    }
}
