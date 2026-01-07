package dev.rahulmg.tiny.controller;

import dev.rahulmg.tiny.dto.CreateUrlRequest;
import dev.rahulmg.tiny.dto.CreateUrlResponse;
import dev.rahulmg.tiny.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
      final @RequestBody CreateUrlRequest request) {
    final String shortCode = urlService.shortenUrl(request.originalUrl());

    // Construct the full URL. In a real scenario, the base URL might be configured.
    // For now, we use the current request's base URI.
    final String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/{shortCode}")
        .buildAndExpand(shortCode)
        .toUriString();

    return ResponseEntity.ok(new CreateUrlResponse(shortUrl));
  }
}