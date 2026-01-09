package dev.rahulmg.tiny.controller;

import dev.rahulmg.tiny.dto.CreateUrlRequest;
import dev.rahulmg.tiny.dto.CreateUrlResponse;
import dev.rahulmg.tiny.service.UrlService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * REST Controller for URL shortening operations.
 */
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {

  private final UrlService urlService;

  /**
   * Creates a short URL from a long URL.
   *
   * @param request The request containing the original URL.
   * @return The response containing the short URL.
   */
  @PostMapping
  public ResponseEntity<CreateUrlResponse> createShortUrl(
      final @Valid @RequestBody CreateUrlRequest request) {
    final String shortCode = urlService.shortenUrl(request.originalUrl());

    // Construct the full URL. In a real scenario, the base URL might be configured.
    // For now, we use the current request's base URI.
    final String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/api/v1/urls/{shortCode}")
        .buildAndExpand(shortCode)
        .toUriString();

    return ResponseEntity.ok(new CreateUrlResponse(shortUrl));
  }

  /**
   * Redirects to the original URL associated with the given short code.
   *
   * @param shortCode The short code to look up.
   * @return A 302 Found response redirecting to the original URL, or 404 Not Found.
   */
  @GetMapping("/{shortCode}")
  public ResponseEntity<Void> redirect(@PathVariable final String shortCode) {
    try {
      final String originalUrl = urlService.getOriginalUrl(shortCode);
      return ResponseEntity.status(HttpStatus.FOUND)
          .location(URI.create(originalUrl))
          .build();
    } catch (final NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    }
  }
}