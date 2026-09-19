package com.ms.stagerschemes.controller;

import com.ms.stagerschemes.api.AuthApi;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

  @Override
  public ResponseEntity<Map<String, String>> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return ResponseEntity.ok(Map.of("username", authentication.getName()));
  }
}
