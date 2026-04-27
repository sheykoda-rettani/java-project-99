package hexlet.code.app.dto;

import lombok.Data;

@Data
public final class ValidationErrorDescriptionDto {
    /**
     * Название поля.
     */
    private String field;
    /**
     * Сообщение об ошибке.
     */
    private String message;
}
