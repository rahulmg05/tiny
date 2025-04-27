package dev.rahulmg.tiny.service;

import dev.rahulmg.tiny.dto.UrlDto;
import dev.rahulmg.tiny.dto.UrlResponseDto;
import dev.rahulmg.tiny.exception.UrlException;
import dev.rahulmg.tiny.model.Url;
import dev.rahulmg.tiny.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for URL operations.
 */
@Service
@RequiredArgsConstructor
public class UrlService {

  // Characters used for generating short codes
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final int SHORT_CODE_LENGTH = 6;
  private static final SecureRandom RANDOM = new SecureRandom();
  private final UrlRepository urlRepository;
  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;

  /**
   * Create a short URL from the original URL.
   *
   * @param urlDto the URL data transfer object
   * @return the response with the short URL information
   */
  @Transactional
  public UrlResponseDto createShortUrl(final UrlDto urlDto) {
    // Check if URL already exists
    final Optional<Url> existingUrl = urlRepository.findByOriginalUrl(urlDto.getUrl());

    return existingUrl.map(url -> handleExistingUrl(url, urlDto)).orElseGet(() -> createNewShortUrl(urlDto));
  }

  /**
   * Handle the case when the URL already exists in the database.
   * 
   * @param url the existing URL entity
   * @param urlDto the URL data transfer object
   * @return the response with the short URL information
   */
  private UrlResponseDto handleExistingUrl(final Url url, final UrlDto urlDto) {
    boolean updated = false;

    // Check if a new custom alias is provided
    if (!ObjectUtils.isEmpty(urlDto.getCustomAlias())) {
      String newAlias = urlDto.getCustomAlias();

      // If the alias is different from the current one, update it
      if (!newAlias.equals(url.getShortCode())) {
        // Check if the new alias is already in use by another URL
        if (urlRepository.existsByShortCode(newAlias)) {
          throw new UrlException("Custom alias already in use");
        }

        // Update the short code
        url.setShortCode(newAlias);
        updated = true;
      }
    }

    // Check if a new expiration time is provided
    if (!ObjectUtils.isEmpty(urlDto.getExpirationMinutes())) {
      LocalDateTime newExpiresAt = calculateExpirationTime(urlDto, url.getCreatedAt());

      // Update the expiration time
      url.setExpiresAt(newExpiresAt);
      updated = true;
    }

    // Save the URL if any changes were made
    if (updated) {
      urlRepository.save(url);
    }

    return UrlResponseDto.of(url.getOriginalUrl(), url.getShortCode(), baseUrl, url.getCreatedAt(), url.getExpiresAt());
  }

  /**
   * Create a new short URL when the original URL doesn't exist in the database.
   * 
   * @param urlDto the URL data transfer object
   * @return the response with the short URL information
   */
  private UrlResponseDto createNewShortUrl(final UrlDto urlDto) {
    final String shortCode = determineShortCode(urlDto);

    final LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime expiresAt = calculateExpirationTime(urlDto, createdAt);

    final Url url = new Url();
    url.setOriginalUrl(urlDto.getUrl());
    url.setShortCode(shortCode);
    url.setCreatedAt(createdAt);
    url.setExpiresAt(expiresAt);
    url.setClickCount(0);

    urlRepository.save(url);

    return UrlResponseDto.of(url.getOriginalUrl(), url.getShortCode(), baseUrl, url.getCreatedAt(), url.getExpiresAt());
  }

  /**
   * Determine the short code to use based on the URL DTO.
   * 
   * @param urlDto the URL data transfer object
   * @return the short code to use
   */
  private String determineShortCode(final UrlDto urlDto) {
    if (!ObjectUtils.isEmpty(urlDto.getCustomAlias())) {
      // Use custom alias if provided
      String customAlias = urlDto.getCustomAlias();
      if (urlRepository.existsByShortCode(customAlias)) {
        throw new UrlException("Custom alias already in use");
      }
      return customAlias;
    } else {
      // Generate a random short code
      return generateShortCode();
    }
  }

  /**
   * Calculate the expiration time based on the URL DTO.
   * 
   * @param urlDto the URL data transfer object
   * @param createdAt the creation time
   * @return the expiration time, or null if no expiration
   */
  private LocalDateTime calculateExpirationTime(final UrlDto urlDto, final LocalDateTime createdAt) {
    if (!ObjectUtils.isEmpty(urlDto.getExpirationMinutes()) && urlDto.getExpirationMinutes() > 0) {
      return createdAt.plusMinutes(urlDto.getExpirationMinutes());
    }
    return null;
  }

  /**
   * Generate a random short code.
   *
   * @return a unique short code
   */
  private String generateShortCode() {
    while (true) {
      final StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
      for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
        sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
      }
      final String shortCode = sb.toString();

      // Check if the generated code already exists
      if (!urlRepository.existsByShortCode(shortCode)) {
        return shortCode;
      }
      // If it exists, loop and try again
    }
  }

  /**
   * Get the original URL from the short code.
   *
   * @param shortCode the short code
   * @return the original URL
   * @throws UrlException if the URL is not found or has expired
   */
  @Transactional
  public String getOriginalUrl(final String shortCode) {
    final Url url = urlRepository.findByShortCode(shortCode)
      .orElseThrow(() -> new UrlException("URL not found! Did you generate one ?"));

    // Check if URL has expired
    if (!ObjectUtils.isEmpty(url.getExpiresAt()) && url.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new UrlException("URL has expired");
    }

    // Increment click count
    url.setClickCount(url.getClickCount() + 1);
    urlRepository.save(url);

    return url.getOriginalUrl();
  }

  /**
   * Update the expiration time of a URL by its short code.
   *
   * @param shortCode the short code of the URL to update
   * @param expirationMinutes the new expiration time in minutes (null means no expiration)
   * @return the updated URL information
   * @throws UrlException if the URL is not found
   */
  @Transactional
  public UrlResponseDto updateUrlExpiration(final String shortCode, final Integer expirationMinutes) {
    final Url url = urlRepository.findByShortCode(shortCode)
      .orElseThrow(() -> new UrlException("URL not found! Did you generate one ?"));

    // Calculate new expiration time
    LocalDateTime expiresAt = null;
    if (!ObjectUtils.isEmpty(expirationMinutes) && expirationMinutes > 0) {
      expiresAt = url.getCreatedAt().plusMinutes(expirationMinutes);
    }

    // Update expiration time
    url.setExpiresAt(expiresAt);
    urlRepository.save(url);

    return UrlResponseDto.of(url.getOriginalUrl(), url.getShortCode(), baseUrl, url.getCreatedAt(), url.getExpiresAt());
  }
}
