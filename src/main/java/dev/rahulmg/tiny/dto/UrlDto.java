package dev.rahulmg.tiny.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for URL creation requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

  @NotBlank(message = "URL cannot be empty")
  @Pattern(
    regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
    message = "Invalid URL format. URL must start with http://, https://, or ftp://"
  )
  private String url;

  // Optional custom alias for the short URL
  private String customAlias;

  // Optional expiration time in minutes (null means no expiration)
  private Integer expirationMinutes;
}