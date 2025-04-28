// Theme toggle functionality
document.addEventListener('DOMContentLoaded', function () {
  const themeToggleBtn = document.getElementById('themeToggle');
  const themeIcon = document.getElementById('themeIcon');
  const themeText = document.getElementById('themeText');
  const htmlElement = document.documentElement;

  // Check if user has a saved preference
  const savedTheme = localStorage.getItem('theme');

  // Apply saved theme or default to light
  if (savedTheme === 'dark') {
    htmlElement.classList.add('dark-mode');
    themeIcon.textContent = '☀️';
    themeText.textContent = 'Light Mode';
  }

  // Toggle theme when button is clicked
  themeToggleBtn.addEventListener('click', function () {
    // Toggle dark mode class
    htmlElement.classList.toggle('dark-mode');

    // Check if dark mode is now active
    const isDarkMode = htmlElement.classList.contains('dark-mode');

    // Update button text and icon
    if (isDarkMode) {
      themeIcon.textContent = '☀️';
      themeText.textContent = 'Light Mode';
      localStorage.setItem('theme', 'dark');
    } else {
      themeIcon.textContent = '🌙';
      themeText.textContent = 'Dark Mode';
      localStorage.setItem('theme', 'light');
    }
  });
});

// Store the current short URL for the copy function
let currentShortUrl = '';

// Set up clear button event listener
document.getElementById('clearBtn').addEventListener('click', function () {
  // Clear all input fields
  document.getElementById('longUrl').value = '';
  document.getElementById('customAlias').value = '';
  document.getElementById('expirationMinutes').value = '';

  // Clear error message
  document.getElementById('error').textContent = '';

  // Reset result fields
  document.getElementById('shortUrl').textContent = '';
  document.getElementById('shortUrl').href = '#';
  document.getElementById('originalUrl').textContent = '';
  document.getElementById('shortCodeDisplay').textContent = '';
  document.getElementById('expiresAt').textContent = 'Never';

  // Hide result
  document.getElementById('result').style.display = 'none';
});

// Set up copy button event listener
document.getElementById('copyBtn').addEventListener('click', function () {
  // Create a temporary textarea element to copy from
  const textarea = document.createElement('textarea');
  textarea.value = currentShortUrl;
  textarea.style.position = 'fixed';  // Prevent scrolling to bottom
  document.body.appendChild(textarea);
  textarea.select();

  try {
    // Execute the copy command
    document.execCommand('copy');
    const originalText = this.textContent;
    this.textContent = 'Copied!';
    setTimeout(() => {
      this.textContent = originalText;
    }, 2000);
  } catch (err) {
    console.error('Could not copy text: ', err);
  } finally {
    // Clean up
    document.body.removeChild(textarea);
  }
});

// Set up shorten button event listener
document.getElementById('shortenBtn').addEventListener('click', function () {
  const longUrl = document.getElementById('longUrl').value;
  const customAlias = document.getElementById('customAlias').value.trim();
  const expirationMinutes = document.getElementById('expirationMinutes').value.trim();
  const errorDiv = document.getElementById('error');
  const resultDiv = document.getElementById('result');

  // Reset previous results
  errorDiv.textContent = '';
  resultDiv.style.display = 'none';

  // Validate URL
  if (!longUrl) {
    errorDiv.textContent = 'Please enter a URL';
    return;
  }

  // Check if the URL is in a valid format
  const urlPattern = /^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$/i;
  if (!urlPattern.test(longUrl)) {
    errorDiv.textContent = 'Invalid URL format. URL must start with http://, https://, or ftp://';
    return;
  }

  // Validate custom alias if provided
  if (customAlias) {
    const aliasPattern = /^[a-zA-Z0-9_-]+$/;
    if (!aliasPattern.test(customAlias)) {
      errorDiv.textContent = 'Custom alias can only contain letters, numbers, hyphens, and underscores';
      return;
    }
  }

  // Validate expiration time if provided
  if (expirationMinutes) {
    const expirationValue = parseInt(expirationMinutes, 10);
    if (isNaN(expirationValue) || expirationValue <= 0) {
      errorDiv.textContent = 'Expiration time must be a positive number';
      return;
    }
  }

  // Prepare request payload
  const payload = {
    url: longUrl
  };

  // Add optional fields if provided
  if (customAlias) {
    payload.customAlias = customAlias;
  }

  if (expirationMinutes) {
    payload.expirationMinutes = parseInt(expirationMinutes, 10);
  }

  // Call the API to shorten the URL
  fetch('/api/urls', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  })
      .then(response => {
        if (!response.ok) {
          // Try to get more detailed error information
          return response.json().then(errorData => {
            console.log('Error data:', errorData); // Log the error data for debugging
            // Check if errorData has a message property
            if (errorData && errorData.message) {
              return Promise.reject(new Error(errorData.message));
            } else {
              // If no message property, stringify the entire errorData object
              return Promise.reject(new Error(JSON.stringify(errorData) || 'Failed to shorten URL'));
            }
          }).catch(parseError => {
            // Only handle JSON parsing errors here
            if (parseError instanceof SyntaxError) {
              console.error('Error parsing JSON:', parseError);
              // If we can't parse the error response as JSON
              throw new Error(`Failed to shorten URL: ${response.status} ${response.statusText}`);
            }
            // Re-throw other errors to be caught by the outer catch
            throw parseError;
          });
        }
        return response.json();
      })
      .then(data => {
        // Check if the response has the expected properties
        if (!data.shortUrl || !data.originalUrl) {
          throw new Error('Invalid response from server');
        }

        // Store the current short URL
        currentShortUrl = data.shortUrl;

        // Display the result
        const shortUrlElement = document.getElementById('shortUrl');
        shortUrlElement.textContent = data.shortUrl;
        shortUrlElement.href = data.shortUrl;
        document.getElementById('originalUrl').textContent = data.originalUrl;

        // Display the short code
        if (data.shortCode) {
          document.getElementById('shortCodeDisplay').textContent = data.shortCode;
        } else {
          // Extract short code from the short URL if not provided directly
          const shortCode = data.shortUrl.split('/').pop();
          document.getElementById('shortCodeDisplay').textContent = shortCode;
        }

        // Handle expiration information
        if (data.expiresAt) {
          const expiresDate = new Date(data.expiresAt);
          document.getElementById('expiresAt').textContent = expiresDate.toLocaleString();
        } else {
          document.getElementById('expiresAt').textContent = 'Never';
        }

        resultDiv.style.display = 'block';
      })
      .catch(error => {
        console.error('Error:', error);
        // Display the error message to the user
        errorDiv.textContent = error.message;
      });
});