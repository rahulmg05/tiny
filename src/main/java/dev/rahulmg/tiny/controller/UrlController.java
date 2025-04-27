package dev.rahulmg.tiny.controller;

import dev.rahulmg.tiny.dto.UrlDto;
import dev.rahulmg.tiny.dto.UrlResponseDto;
import dev.rahulmg.tiny.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for URL operations.
 */
@RestController
@RequiredArgsConstructor
public class UrlController {

  private final UrlService urlService;

  /**
   * Create a short URL.
   *
   * @param urlDto the URL data transfer object
   * @return the response with the short URL information
   */
  @PostMapping("/api/urls")
  public ResponseEntity<UrlResponseDto> createShortUrl(@Valid @RequestBody final UrlDto urlDto) {
    final UrlResponseDto response = urlService.createShortUrl(urlDto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * Redirect to the original URL.
   *
   * @param shortCode the short code
   * @return a redirect view to the original URL
   */
  @GetMapping("/{shortCode:[a-zA-Z0-9]{6}}")
  public RedirectView redirectToOriginalUrl(@PathVariable final String shortCode) {
    final String originalUrl = urlService.getOriginalUrl(shortCode);
    return new RedirectView(originalUrl);
  }

  /**
   * Get URL information by short code.
   *
   * @param shortCode the short code
   * @return the URL information
   */
  @GetMapping("/api/urls/{shortCode}")
  public ResponseEntity<UrlResponseDto> getUrlInfo(@PathVariable final String shortCode) {
    // This would require an additional method in the service to return the full URL info
    // For now, we'll just get the original URL and return a simplified response
    final String originalUrl = urlService.getOriginalUrl(shortCode);
    final UrlResponseDto response = new UrlResponseDto();
    response.setOriginalUrl(originalUrl);
    response.setShortCode(shortCode);
    return ResponseEntity.ok(response);
  }

  /**
   * Update the expiration time of a URL by its short code.
   *
   * @param shortCode the short code of the URL to update
   * @param expirationMinutes the new expiration time in minutes (null means no expiration)
   * @return the updated URL information
   */
  @PatchMapping("/api/urls/{shortCode}")
  public ResponseEntity<UrlResponseDto> updateUrlExpiration(
      @PathVariable final String shortCode,
      @RequestParam(required = false) final Integer expirationMinutes) {
    final UrlResponseDto response = urlService.updateUrlExpiration(shortCode, expirationMinutes);
    return ResponseEntity.ok(response);
  }
}
