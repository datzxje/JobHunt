package com.jobhunt.controller;

import com.jobhunt.payload.Response;
import com.jobhunt.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/languages")
@RequiredArgsConstructor
public class LanguageController {

  private final LanguageService languageService;

  @GetMapping
  public ResponseEntity<?> getAllActiveLanguages() {
    return ResponseEntity.ok(Response.ofSucceeded(languageService.getAllActiveLanguages()));
  }

  @GetMapping("/search")
  public ResponseEntity<?> searchLanguages(@RequestParam String keyword) {
    return ResponseEntity.ok(Response.ofSucceeded(languageService.searchLanguages(keyword)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getLanguageById(@PathVariable Long id) {
    return ResponseEntity.ok(Response.ofSucceeded(languageService.getLanguageById(id)));
  }
}