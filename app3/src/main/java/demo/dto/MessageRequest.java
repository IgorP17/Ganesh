package demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MessageRequest {

    @NotBlank(message = "Сообщение не может быть пустым")
    @Size(max = 1000, message = "Сообщение не может быть длиннее 1000 символов")
    private String message;

    // Геттеры и сеттеры
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}