package dev.rahulmg.tiny.model;

import java.time.Instant;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Entity representing a URL mapping in Cassandra.
 */
@Table("url_mapping")
public class UrlMapping {

  @PrimaryKey
  private String shortCode;

  @Column("original_url")
  private String originalUrl;

  @Column("created_at")
  private Instant createdAt;

  /**
   * Default constructor for Spring Data Cassandra.
   */
  public UrlMapping() {
  }

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
  }

  public String getShortCode() {
    return shortCode;
  }

  public void setShortCode(final String shortCode) {
    this.shortCode = shortCode;
  }

  public String getOriginalUrl() {
    return originalUrl;
  }

  public void setOriginalUrl(final String originalUrl) {
    this.originalUrl = originalUrl;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(final Instant createdAt) {
    this.createdAt = createdAt;
  }
}