package hexlet.code.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public final class UserRequestDto {
    /**
     * Имя пользователя.
     */
    @Size(min = 2, message = "Имя должно содержать минимум 2 символа")
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Size(min = 2, message = "Имя должно содержать минимум 2 символа")
    private String lastName;

    /**
     * Адрес электронной почты.
     */
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email обязателен")
    private String email;

    /**
     * Пароль.
     */
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 3, message = "Пароль должен содержать минимум 3 символа")
    private String password;
}
