package com.ms.stagerschemes.api;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
public interface AuthApi {

  @GetMapping("/me")
  ResponseEntity<Map<String, String>> getCurrentUser();
}
