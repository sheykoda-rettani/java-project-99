package hexlet.code.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public final class LoginRequestDto {
    /**
     * Имя пользователя. Совпадает с email
     */
    @Email(message = "Имя пользователя должно иметь формат email")
    @NotBlank(message = "Имя пользователя(Email) обязателен")
    private String username;

    /**
     * Пароль.
     */
    @NotBlank(message = "Пароль обязателен")
    private String password;
}
