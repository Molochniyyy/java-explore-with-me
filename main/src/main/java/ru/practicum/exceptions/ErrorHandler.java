package ru.practicum.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException e) {
        log.info("Объект не найден: {}", e.getMessage());
        final HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(e, status, "Ошибка. NotFoundException");
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleConflictException(final ConflictException e) {
        log.info("Объект не удовлетворяет правилам: {}", e.getMessage());
        final HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse errorResponse = new ErrorResponse(e, status, "Ошибка. ConflictException");
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleValidationException(final ValidationException e) {
        log.info("Некорректный запрос: {}", e.getMessage());
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(e, status, "Ошибка. ValidationException");
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleThrowable(final Throwable e) {
        log.info("Ошибка: {}", e.getMessage());
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(e, status, "Ошибка. Throwable");
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            final MissingServletRequestParameterException e) {
        log.info("Ошибка: {}", e.getMessage());
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(e, status, "Ошибка. MissingServletRequestParameterException");
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException e) {
        log.info("409 {}", e.getMessage(), e);
        final HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse errorResponse = new ErrorResponse(e, status, "Ошибка. IllegalArgumentException");
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.info("400 {}", e.getMessage(), e);
        final HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse errorResponse = new ErrorResponse(e, status, "Ошибка. DataIntegrityViolationException");
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.info("400 {}", e.getMessage(), e);
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(e, status, "Ошибка. MethodArgumentNotValidException");
        return new ResponseEntity<>(errorResponse, status);
    }
}
