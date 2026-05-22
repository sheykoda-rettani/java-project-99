package hexlet.code.handler;

import hexlet.code.dto.ErrorResponseDto;
import hexlet.code.dto.ValidationErrorDescriptionDto;
import hexlet.code.exception.UnableToDeleteException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolations(final ConstraintViolationException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        var violations = ex.getConstraintViolations();
        List<ValidationErrorDescriptionDto> errors = violations.stream().map(this::extractErrorDescription).toList();
        ErrorResponseDto responseDto = new ErrorResponseDto(status,
                "Ошибка валидации данных запроса",
                errors);
        return new ResponseEntity<>(responseDto, status);
    }

    @ExceptionHandler(UnableToDeleteException.class)
    public ResponseEntity<ErrorResponseDto> handleUnableToDelete(final UnableToDeleteException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponseDto responseDto = new ErrorResponseDto(status,
                ex.getMessage(),
                ex.getCause().getMessage());
        return new ResponseEntity<>(responseDto, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationsInDb(
            final DataIntegrityViolationException ex) {
        HttpStatus status = HttpStatus.CONFLICT;

        String messageForUser = "Произошла ошибка при попытке изменить БД.";
        ErrorResponseDto responseDto = new ErrorResponseDto(status, messageForUser, ex.getMessage());
        return new ResponseEntity<>(responseDto, status);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorizationException(final AuthorizationDeniedException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        String messageForUser = "У вас нет прав на выполнение данной операции.";
        ErrorResponseDto responseDto = new ErrorResponseDto(status, messageForUser, ex.getMessage());

        return new ResponseEntity<>(responseDto, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllOtherExceptions(final Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        String messageForUser = "Произошла непредвиденная ошибка на сервере.";
        ErrorResponseDto responseDto = new ErrorResponseDto(status, messageForUser, ex.getMessage());

        return new ResponseEntity<>(responseDto, status);
    }

    private ValidationErrorDescriptionDto extractErrorDescription(final ConstraintViolation<?> violation) {
        if (violation == null) {
            return null;
        }
        ValidationErrorDescriptionDto result = new ValidationErrorDescriptionDto();
        result.setField(violation.getPropertyPath().toString());
        result.setMessage(violation.getMessage());
        return result;
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
