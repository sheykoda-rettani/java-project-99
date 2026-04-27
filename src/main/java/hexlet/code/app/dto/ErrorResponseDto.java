package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "message", "details"})
public final class ErrorResponseDto {
    /**
     * Код ошибки.
     */
    private Integer code;
    /**
     * Сообщение об ошибке.
     */
    private String message;
    /**
     * Делали ошибки.
     */
    private Object details;

    public ErrorResponseDto(@NotNull final HttpStatus aStatus, final String aMessage, final Object aDetails) {
        this.code = aStatus.value();
        this.message = aMessage;
        this.details = aDetails;
    }

    public ErrorResponseDto(@NotNull final HttpStatus aStatus, final String aMessage) {
        this(aStatus, aMessage, null);
    }
}
