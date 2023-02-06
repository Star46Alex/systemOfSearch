package com.alex_star.systemofsearch.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StartPagesController {

  @GetMapping("/admin")
  public String admin() {
    return "index";
  }
}
