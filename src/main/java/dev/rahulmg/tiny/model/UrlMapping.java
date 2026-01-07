package dev.rahulmg.tiny.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Entity representing a URL mapping in Cassandra.
 */
@Data
@NoArgsConstructor
@Table("url_mapping")
public class UrlMapping {

  @PrimaryKey
  private String shortCode;

  @Column("original_url")
  private String originalUrl;

  @Column("created_at")
  private Instant createdAt;

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
}