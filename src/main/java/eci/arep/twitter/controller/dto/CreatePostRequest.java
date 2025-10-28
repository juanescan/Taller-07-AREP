package eci.arep.twitter.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePostRequest {

    @NotBlank(message = "El contenido no puede estar vacío")
    @Size(max = 280, message = "El contenido no puede exceder 280 caracteres")
    private String content;

    // opcional: si viene, es una respuesta
    private String parentPostId;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getParentPostId() { return parentPostId; }
    public void setParentPostId(String parentPostId) { this.parentPostId = parentPostId; }
}
