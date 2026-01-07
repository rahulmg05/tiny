package dev.rahulmg.tiny.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a URL mapping in the database.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "url_mapping", indexes = @Index(
    name = "idx_url_mapping_expires_at", columnList = "expires_at"))
public class UrlMapping {

  @Id
  private String shortCode;

  @Column(name = "original_url")
  private String originalUrl;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "expires_at")
  private Instant expiresAt;

  /**
   * Constructs a new UrlMapping.
   *
   * @param shortCode   The unique short code.
   * @param originalUrl The original long URL.
   */
  public UrlMapping(final String shortCode, final String originalUrl) {
    this.shortCode = shortCode;
    this.originalUrl = originalUrl;
    this.createdAt = Instant.now();
    this.expiresAt = this.createdAt.plus(30, ChronoUnit.DAYS);
  }
}