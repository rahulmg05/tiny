# Tiny URL Service

A lightweight, scalable URL shortening service built with Java 25, Spring Boot 3, and PostgreSQL. It is designed to run in a containerized environment (Kubernetes).

## Features

*   **Shorten URLs:** Convert long URLs into compact 8-character codes.
*   **Custom Aliases:** Users can define their own custom short codes (e.g., `tiny.com/my-link`).
*   **Redirection:** High-performance redirection from short codes to original URLs.
*   **Expiration:** Links expire automatically after 15 days by default, with support for custom expiration dates.
*   **Validation:** Strict input validation to prevent malformed URLs and aliases.
*   **Cleanup:** Automated daily background job to remove expired links.

## API Documentation

### 1. Create Short URL

Creates a new short URL.

*   **Endpoint:** `POST /api/v1/urls`
*   **Content-Type:** `application/json`

**Request Body Parameters:**

*   `originalUrl` (Required): The full URL to be shortened. Must be a valid URL format.
*   `alias` (Optional): A custom string to use as the short code.
    *   Constraints: 3-20 characters, alphanumeric with hyphens.
*   `expiresAt` (Optional): A specific ISO-8601 timestamp for expiration.
    *   Constraints: Must be in the future and no more than 30 days from now.

**Example Request:**
```json
{
  "originalUrl": "https://www.example.com/very/long/path",
  "alias": "my-custom-link",
  "expiresAt": "2026-01-20T12:00:00Z"
}
```

**Success Response (200 OK):**
```json
{
  "shortUrl": "http://localhost:8080/api/v1/urls/my-custom-link"
}
```

**Error Responses:**
*   `400 Bad Request`: Invalid input format (e.g., invalid URL, alias too short, date in past).
*   `409 Conflict`: The requested alias is already in use.

---

### 2. Redirect URL

Redirects the client to the original URL associated with the short code.

*   **Endpoint:** `GET /api/v1/urls/{shortCode}`

**Behavior:**

*   **Found (302):** If the short code exists and is valid, returns a `302 Found` status with the `Location` header set to the original URL.
*   **Not Found (404):** If the short code does not exist or the link has expired.

**Example Usage:**
Open `http://localhost:8080/api/v1/urls/aX9zP1` in a browser.

## Running Locally

1.  **Prerequisites:** Java 25, Docker (for database).
2.  **Database:** Ensure a PostgreSQL instance is running and configured in `application.properties`.
3.  **Build & Run:**
    ```bash
    ./mvnw clean package
    java -jar target/tiny-0.0.1-SNAPSHOT.jar
    ```
