package dev.rahulmg.tiny.service;

import dev.rahulmg.tiny.repository.UrlMappingRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for cleaning up expired URL mappings.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UrlCleanupService {

  private final UrlMappingRepository urlMappingRepository;

  /**
   * Deletes expired URLs.
   * Runs every day at midnight.
   */
  @Scheduled(cron = "0 0 0 * * *")
  @Transactional
  public void cleanupExpiredUrls() {
    log.info("Starting expired URL cleanup job");
    final Instant now = Instant.now();
    urlMappingRepository.deleteByExpiresAtBefore(now);
    log.info("Completed expired URL cleanup job");
  }
}
