package dev.rahulmg.tiny.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for URL response.
 * Contains information about the created short URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponseDto {

  private String originalUrl;
  private String shortUrl;
  private String shortCode;
  private LocalDateTime createdAt;
  private LocalDateTime expiresAt;

  /**
   * Static factory method to create a response with the base URL.
   *
   * @param originalUrl the original URL
   * @param shortCode   the generated short code
   * @param baseUrl     the base URL of the service
   * @param createdAt   when the URL was created
   * @param expiresAt   when the URL expires (can be null)
   * @return a new UrlResponseDto
   */
  public static UrlResponseDto of(final String originalUrl, final String shortCode, final String baseUrl,
                                  final LocalDateTime createdAt, final LocalDateTime expiresAt) {
    final String shortUrl = baseUrl + "/" + shortCode;
    return new UrlResponseDto(originalUrl, shortUrl, shortCode, createdAt, expiresAt);
  }
}