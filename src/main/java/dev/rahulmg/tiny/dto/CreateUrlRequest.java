package dev.rahulmg.tiny.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * Request object for creating a short URL.
 *
 * @param originalUrl The original long URL to be shortened.
 */
public record CreateUrlRequest(
    @NotBlank(message = "Original URL cannot be blank")
    @URL(message = "Invalid URL format")
    String originalUrl) {
}