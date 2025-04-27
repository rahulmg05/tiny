package dev.rahulmg.tiny.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class for URL-related errors.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UrlException extends RuntimeException {

  public UrlException(final String message) {
    super(message);
  }
}
