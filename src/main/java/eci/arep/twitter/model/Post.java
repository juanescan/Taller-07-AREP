package eci.arep.twitter.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    @NotBlank(message = "El contenido del post no puede estar vacío")
    @Size(max = 280, message = "El post no puede exceder 280 caracteres")
    private String content;

    @NotBlank(message = "El autor no puede estar vacío")
    @Size(max = 50, message = "El nombre del autor no puede exceder 50 caracteres")
    private String author;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("parent_post_id")
    private String parentPostId;

    @Field("reply_ids")
    private List<String> replyIds = new ArrayList<>();

    @Field("reply_count")
    private int replyCount = 0;

    // Constructores
    public Post() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Post(String content, String author) {
        this();
        this.content = content;
        this.author = author;
    }

    public Post(String content, String author, String parentPostId) {
        this(content, author);
        this.parentPostId = parentPostId;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getParentPostId() {
        return parentPostId;
    }

    public void setParentPostId(String parentPostId) {
        this.parentPostId = parentPostId;
    }

    public List<String> getReplyIds() {
        return replyIds;
    }

    public void setReplyIds(List<String> replyIds) {
        this.replyIds = replyIds;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    // Métodos helper
    public boolean isReply() {
        return parentPostId != null && !parentPostId.isEmpty();
    }

    public void addReplyId(String replyId) {
        if (!replyIds.contains(replyId)) {
            replyIds.add(replyId);
            replyCount++;
        }
    }

    public void removeReplyId(String replyId) {
        if (replyIds.remove(replyId)) {
            replyCount--;
        }
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", createdAt=" + createdAt +
                ", isReply=" + isReply() +
                ", replyCount=" + replyCount +
                '}';
    }
}