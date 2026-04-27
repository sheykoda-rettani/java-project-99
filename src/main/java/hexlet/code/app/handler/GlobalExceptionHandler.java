package hexlet.code.app.handler;

import hexlet.code.app.dto.ErrorResponseDto;
import hexlet.code.app.dto.ValidationErrorDescriptionDto;
import jakarta.ws.rs.NotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public final class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateKey(final DuplicateKeyException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponseDto responseDto = new ErrorResponseDto(status, ex.getMessage());
        return new ResponseEntity<>(responseDto, status);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(final NotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponseDto responseDto = new ErrorResponseDto(status, ex.getMessage());
        return new ResponseEntity<>(responseDto, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleNotValid(final MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<ValidationErrorDescriptionDto> errors = ex.getBindingResult().getAllErrors().stream().
                map(this::extractErrorDescription).toList();
        ErrorResponseDto responseDto = new ErrorResponseDto(status,
                "Ошибка валидации данных запроса",
                errors);
        return new ResponseEntity<>(responseDto, status);
    }

    private ValidationErrorDescriptionDto extractErrorDescription(final ObjectError error) {
        if (error == null) {
            return null;
        }
        ValidationErrorDescriptionDto result = new ValidationErrorDescriptionDto();
        result.setField(((FieldError) error).getField());
        result.setMessage(error.getDefaultMessage());
        return result;
    }

}
