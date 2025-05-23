package dev.rahulmg.tiny.service;

import dev.rahulmg.tiny.dto.UrlDto;
import dev.rahulmg.tiny.dto.UrlResponseDto;
import dev.rahulmg.tiny.exception.UrlException;
import dev.rahulmg.tiny.model.Url;
import dev.rahulmg.tiny.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

  private final String baseUrl = "http://localhost:8080";
  private final String originalUrl = "https://example.com";
  private final String existingShortCode = "abc123";
  private final String newAlias = "newAlias";
  @Mock
  private UrlRepository urlRepository;
  @InjectMocks
  private UrlService urlService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(urlService, "baseUrl", baseUrl);
  }

  @Test
  void shouldUpdateAliasWhenUrlExistsAndNewAliasProvided() {
    // Arrange
    final UrlDto urlDto = new UrlDto(originalUrl, newAlias, null);

    final Url existingUrl = new Url();
    existingUrl.setOriginalUrl(originalUrl);
    existingUrl.setShortCode(existingShortCode);
    existingUrl.setCreatedAt(LocalDateTime.now());
    existingUrl.setClickCount(0);

    when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existingUrl));
    when(urlRepository.existsByShortCode(newAlias)).thenReturn(false);

    // Act
    final UrlResponseDto result = urlService.createShortUrl(urlDto);

    // Assert
    verify(urlRepository).findByOriginalUrl(originalUrl);
    verify(urlRepository).existsByShortCode(newAlias);
    verify(urlRepository).save(existingUrl);

    assertEquals(newAlias, existingUrl.getShortCode());
    assertEquals(newAlias, result.getShortCode());
    assertEquals(originalUrl, result.getOriginalUrl());
  }

  @Test
  void shouldThrowExceptionWhenNewAliasAlreadyInUse() {
    // Arrange
    final UrlDto urlDto = new UrlDto(originalUrl, newAlias, null);

    final Url existingUrl = new Url();
    existingUrl.setOriginalUrl(originalUrl);
    existingUrl.setShortCode(existingShortCode);
    existingUrl.setCreatedAt(LocalDateTime.now());
    existingUrl.setClickCount(0);

    when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existingUrl));
    when(urlRepository.existsByShortCode(newAlias)).thenReturn(true);

    // Act & Assert
    final UrlException exception = assertThrows(UrlException.class, () -> urlService.createShortUrl(urlDto));
    assertEquals("Custom alias already in use", exception.getMessage());

    verify(urlRepository).findByOriginalUrl(originalUrl);
    verify(urlRepository).existsByShortCode(newAlias);
    verify(urlRepository, never()).save(any(Url.class));
  }

  @Test
  void shouldNotUpdateAliasWhenSameAliasProvided() {
    // Arrange
    final UrlDto urlDto = new UrlDto(originalUrl, existingShortCode, null);

    final Url existingUrl = new Url();
    existingUrl.setOriginalUrl(originalUrl);
    existingUrl.setShortCode(existingShortCode);
    existingUrl.setCreatedAt(LocalDateTime.now());
    existingUrl.setClickCount(0);

    when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existingUrl));

    // Act
    final UrlResponseDto result = urlService.createShortUrl(urlDto);

    // Assert
    verify(urlRepository).findByOriginalUrl(originalUrl);
    verify(urlRepository, never()).existsByShortCode(anyString());
    verify(urlRepository, never()).save(any(Url.class));

    assertEquals(existingShortCode, result.getShortCode());
    assertEquals(originalUrl, result.getOriginalUrl());
  }

  @Test
  void shouldUpdateExpirationTimeWhenUrlExistsAndNewExpirationProvided() {
    // Arrange
    final int expirationMinutes = 60;
    final UrlDto urlDto = new UrlDto(originalUrl, null, expirationMinutes);

    final LocalDateTime createdAt = LocalDateTime.now();
    final Url existingUrl = new Url();
    existingUrl.setOriginalUrl(originalUrl);
    existingUrl.setShortCode(existingShortCode);
    existingUrl.setCreatedAt(createdAt);
    existingUrl.setClickCount(0);

    when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existingUrl));

    // Act
    final UrlResponseDto result = urlService.createShortUrl(urlDto);

    // Assert
    verify(urlRepository).findByOriginalUrl(originalUrl);
    verify(urlRepository).save(existingUrl);

    assertEquals(existingShortCode, result.getShortCode());
    assertEquals(originalUrl, result.getOriginalUrl());
    assertEquals(createdAt.plusMinutes(expirationMinutes), existingUrl.getExpiresAt());
    assertEquals(createdAt.plusMinutes(expirationMinutes), result.getExpiresAt());
  }
}
