package eci.arep.twitter.service;

import eci.arep.twitter.model.Post;
import eci.arep.twitter.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Service
public class PostService {

    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public Post create(String content, String authorId, String parentPostId) {
        if (parentPostId != null && !parentPostId.isBlank()) {
            // validar que exista post padre
            repository.findById(parentPostId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "El post padre no existe"));
        } else {
            parentPostId = null;
        }
        Post post = new Post(content, authorId, parentPostId);
        return repository.save(post);
    }

    public Page<Post> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "_id"));
        return repository.findAll(pageable);
    }

    public Page<Post> listMine(String authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "_id"));
        return repository.findByAuthor(authorId, pageable);
    }

    public Post getOne(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post no encontrado"));
    }

    public Page<Post> listReplies(String postId, int page, int size) {
        // validar que exista padre
        getOne(postId);
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "_id"));
        return repository.findByParentPostId(postId, pageable);
    }

    public Post update(String id, String newContent, String requesterAuthorId) {
        Post existing = getOne(id);
        if (!equalsEmail(existing.getAuthor(), requesterAuthorId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes editar un post de otro usuario");
        }
        existing.setContent(newContent);
        return repository.save(existing);
    }

    public void delete(String id, String requesterAuthorId) {
        Post existing = getOne(id);
        if (!equalsEmail(existing.getAuthor(), requesterAuthorId)) {
            throw new ResponseStatusException(FORBIDDEN, "No puedes eliminar un post de otro usuario");
        }
        deleteRecursively(id);
    }

    private void deleteRecursively(String id) {
        // borrar hijos primero
        Pageable all = PageRequest.of(0, 200);
        Page<Post> replies = repository.findByParentPostId(id, all);
        replies.forEach(reply -> deleteRecursively(reply.getId()));
        // borra el nodo
        repository.deleteById(id);
    }

    private boolean equalsEmail(String a, String b) {
        if (a == null || b == null) return false;
        return Objects.equals(a.toLowerCase(), b.toLowerCase());
    }
}
