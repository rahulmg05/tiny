package dev.rahulmg.tiny.repository;

import dev.rahulmg.tiny.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Url entity.
 * Provides methods to interact with the database for URL operations.
 */
@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

  /**
   * Find a URL by its short code.
   *
   * @param shortCode the short code to search for
   * @return an Optional containing the URL if found, or empty if not found
   */
  Optional<Url> findByShortCode(String shortCode);

  /**
   * Check if a short code already exists.
   *
   * @param shortCode the short code to check
   * @return true if the short code exists, false otherwise
   */
  boolean existsByShortCode(String shortCode);

  /**
   * Find a URL by its original URL.
   *
   * @param originalUrl the original URL to search for
   * @return an Optional containing the URL if found, or empty if not found
   */
  Optional<Url> findByOriginalUrl(String originalUrl);
}