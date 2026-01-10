package dev.rahulmg.tiny.exception;

/**
 * Exception thrown when a requested custom alias is already in use.
 */
public class AliasAlreadyTakenException extends RuntimeException {
  /**
   * Constructs a new AliasAlreadyTakenException.
   *
   * @param message The error message.
   */
  public AliasAlreadyTakenException(final String message) {
    super(message);
  }
}
