package dev.rahulmg.tiny.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import org.hibernate.validator.constraints.URL;

/**
 * Request object for creating a short URL.
 *
 * @param originalUrl The original long URL to be shortened.
 * @param alias       Optional custom alias. Must be 3-20 characters, alphanumeric with hyphens.
 * @param expiresAt   Optional custom expiration date. Must be in the future.
 */
public record CreateUrlRequest(
    @NotBlank(message = "Original URL cannot be blank")
    @URL(message = "Invalid URL format")
    String originalUrl,

    @Size(min = 3, max = 20, message = "Alias must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Alias must be alphanumeric with hyphens")
    String alias,

    @Future(message = "Expiration date must be in the future")
    Instant expiresAt
) {
}