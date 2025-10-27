package eci.arep.twitter.repository;

import eci.arep.twitter.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findByParentPostId(String parentPostId, Pageable pageable);
    Page<Post> findByAuthor(String author, Pageable pageable);
}
