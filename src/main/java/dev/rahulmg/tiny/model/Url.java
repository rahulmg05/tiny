package dev.rahulmg.tiny.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class representing a URL mapping in the system.
 * Each URL has an original long URL and a shortened code.
 */
@Entity
@Table(name = "urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Url {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Original URL cannot be empty")
  @Column(nullable = false, length = 2048)
  private String originalUrl;

  @Column(nullable = false, unique = true, length = 10)
  private String shortCode;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime expiresAt;

  @Column(nullable = false)
  private int clickCount = 0;
}