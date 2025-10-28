package eci.arep.twitter.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdatePostRequest {

    @NotBlank(message = "El contenido no puede estar vacío")
    @Size(max = 280, message = "El contenido no puede exceder 280 caracteres")
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
