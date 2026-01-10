package dev.rahulmg.tiny.repository;

import dev.rahulmg.tiny.model.UrlMapping;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing UrlMapping data in the database.
 */
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {
  /**
   * Finds a UrlMapping by its short code if the expiration date is after the given time.
   *
   * @param shortCode The short code to search for.
   * @param now       The current time to check expiration against.
   * @return An Optional containing the UrlMapping if found and valid, otherwise empty.
   */
  Optional<UrlMapping> findByShortCodeAndExpiresAtAfter(String shortCode, Instant now);

  /**
   * Deletes all UrlMappings that have expired before the given time.
   *
   * @param now The current time.
   */
  void deleteByExpiresAtBefore(Instant now);
}