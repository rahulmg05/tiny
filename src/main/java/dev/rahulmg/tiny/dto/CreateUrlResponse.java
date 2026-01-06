package dev.rahulmg.tiny.dto;

/**
 * Response object containing the generated short URL.
 *
 * @param shortUrl The full short URL.
 */
public record CreateUrlResponse(String shortUrl) {
}