package dev.rahulmg.tiny;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the Tiny application.
 */
@SpringBootApplication
public class TinyApplication {

  /**
   * The main method that starts the Spring Boot application.
   *
   * @param args the command-line arguments
   */
  public static void main(final String[] args) {
    SpringApplication.run(TinyApplication.class, args);
  }
}
