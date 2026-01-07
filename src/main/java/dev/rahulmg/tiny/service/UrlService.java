package dev.rahulmg.tiny.service;

import dev.rahulmg.tiny.model.UrlMapping;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.stereotype.Service;

/**
 * Service for managing URL shortenings.
 */
@Service
@RequiredArgsConstructor
public class UrlService {

  private final CassandraTemplate cassandraTemplate;
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

      // Use LWT (Lightweight Transaction) to ensure we don't overwrite an existing code
      // IF NOT EXISTS
      final var writeResult = cassandraTemplate.insert(mapping,
          InsertOptions.builder().withIfNotExists().build());

      inserted = writeResult.wasApplied();

      if (inserted) {
        return shortCode;
      }
    }

    throw new RuntimeException("Failed to generate a unique short code after multiple attempts");
  }

  private String generateShortCode() {
    final byte[] randomBytes = new byte[6]; // 6 bytes -> 8 base64 chars
    secureRandom.nextBytes(randomBytes);
    // URL-safe Base64 encoder, without padding
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }
}