package com.innowise.gatewayservice.controller;

import com.innowise.gatewayservice.dto.AccessDataDto;
import com.innowise.gatewayservice.dto.UserDataDto;
import com.innowise.gatewayservice.sevice.AccessService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "gateway_security")
@RequestMapping("/api/v1/access/")
public class AccessController {

  private final AccessService accessService;

  @PostMapping("/signup/")
  public Mono<AccessDataDto> signup(@Valid @RequestBody UserDataDto userData) {
    return accessService.signup(userData);
  }

}
