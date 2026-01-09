package dev.rahulmg.tiny.service;

import dev.rahulmg.tiny.model.UrlMapping;
import dev.rahulmg.tiny.repository.UrlMappingRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * Service for managing URL shortenings.
 */
@Service
@RequiredArgsConstructor
public class UrlService {

  private final UrlMappingRepository urlMappingRepository;
  private final SecureRandom secureRandom = new SecureRandom();

  /**
   * Generates a short URL for the given original URL.
   *
   * @param originalUrl The original long URL.
   * @return The generated short code.
   */
  public String shortenUrl(final String originalUrl) {
    String shortCode;
    boolean inserted = false;

    // Retry loop in case of collision
    // In a real-world high-concurrency scenario, we might want a limit on retries
    while (!inserted) {
      shortCode = generateShortCode();
      final UrlMapping mapping = new UrlMapping(shortCode, originalUrl);

      try {
        urlMappingRepository.save(mapping);
        inserted = true;
        return shortCode;
      } catch (final DataIntegrityViolationException e) {
        // Collision detected, retry with a new code
        inserted = false;
      }
    }

    throw new RuntimeException("Failed to generate a unique short code after multiple attempts");
  }

  /**
   * Retrieves the original URL for a given short code, if it exists and has not expired.
   *
   * @param shortCode The short code to look up.
   * @return The original long URL.
   * @throws NoSuchElementException if the URL is not found or has expired.
   */
  public String getOriginalUrl(final String shortCode) {
    return urlMappingRepository.findByShortCodeAndExpiresAtAfter(shortCode, Instant.now())
        .map(UrlMapping::getOriginalUrl)
        .orElseThrow(() -> new NoSuchElementException("URL not found or expired: " + shortCode));
  }

  private String generateShortCode() {
    final byte[] randomBytes = new byte[6]; // 6 bytes -> 8 base64 chars
    secureRandom.nextBytes(randomBytes);
    // URL-safe Base64 encoder, without padding
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }
}
