package dev.rahulmg.tiny.dto;

/**
 * Request object for creating a short URL.
 *
 * @param originalUrl The original long URL to be shortened.
 */
public record CreateUrlRequest(String originalUrl) {
}