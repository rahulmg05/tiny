package dev.rahulmg.tiny.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for home page navigation.
 */
@Controller
public class HomeController {

  /**
   * Redirect the root path to the index.html page.
   *
   * @return a redirect view to the index.html page
   */
  @GetMapping("/")
  public RedirectView home() {
    return new RedirectView("/index.html");
  }
}