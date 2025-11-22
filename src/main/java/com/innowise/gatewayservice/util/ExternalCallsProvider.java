package com.innowise.gatewayservice.util;

import com.innowise.gatewayservice.config.RegistrationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ExternalCallsProvider {

  private final RegistrationProperties properties;

  @Bean
  public ExternalServiceCaller externalServiceCaller() {
    return new ExternalServiceCaller(properties.getBackoffDelay(), properties.getMaxAttempts(),
        WebClient.create());
  }
}
