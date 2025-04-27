package dev.rahulmg.tiny.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle URL exceptions.
   *
   * @param ex the exception
   * @return the error response
   */
  @ExceptionHandler(UrlException.class)
  public ResponseEntity<ErrorResponse> handleUrlException(final UrlException ex) {
    final ErrorResponse errorResponse =
      new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), LocalDateTime.now());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle validation exceptions.
   *
   * @param ex the exception
   * @return the error response with validation errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(final MethodArgumentNotValidException ex) {
    final Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      final String fieldName = ((FieldError) error).getField();
      final String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    final ValidationErrorResponse errorResponse =
      new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", LocalDateTime.now(), errors);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle all other exceptions.
   *
   * @return the error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException() {
    final ErrorResponse errorResponse =
      new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred", LocalDateTime.now());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Error response class.
   */
  public static class ErrorResponse {
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;

    public ErrorResponse(final int status, final String message, final LocalDateTime timestamp) {
      this.status = status;
      this.message = message;
      this.timestamp = timestamp;
    }

    public int getStatus() {
      return status;
    }

    public String getMessage() {
      return message;
    }

    public LocalDateTime getTimestamp() {
      return timestamp;
    }
  }

  /**
   * Validation error response class.
   */
  public static class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    public ValidationErrorResponse(final int status, final String message, final LocalDateTime timestamp,
                                   final Map<String, String> errors) {
      super(status, message, timestamp);
      this.errors = errors;
    }

    public Map<String, String> getErrors() {
      return errors;
    }
  }
}