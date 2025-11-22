package com.innowise.gatewayservice.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
public class SecurityConfig {

  private final SecurityProperties properties;

  private static final String[] AUTH_WHITE_LIST = {"/actuator/health/**", "/swagger-resources/**",
      "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs", "/webjars/**",
      "/favicon.ico", "/oauth2/**", "/login**", "/auth-server/**", "/api/v1/access/**", "/error"};

  @Bean
  public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
    http.csrf(CsrfSpec::disable).cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeExchange(
            exchanges -> exchanges.pathMatchers(AUTH_WHITE_LIST).permitAll().anyExchange()
                .authenticated())
        .oauth2ResourceServer((oauth2) -> oauth2
            .jwt(Customizer.withDefaults()))
        .oauth2Login(withDefaults());
    return http.build();
  }

  @Bean
  public ReactiveJwtDecoder jwtDecoder() {
    return ReactiveJwtDecoders.fromIssuerLocation(properties.getIssuerUri());
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedHeader("Access-Control-Allow-Origin");
    config.addAllowedHeader("Authorization");
    config.addAllowedHeader("Content-Type");
    config.setAllowedMethods(
        Arrays.asList("GET", "POST", "OPTIONS", "PATCH", "HEAD", "PUT", "DELETE"));
    config.setAllowedOriginPatterns(
        Arrays.asList("http://localhost:8080*", "http://localhost:8082*", "null",
            "http://auth-service:8082*", "http://gateway-service:8084*", "http://localhost:8084*",
            "http://user-service:8080*", "http://order-service:8083*", "http://localhost:8083*"));
    config.setAllowCredentials(true);
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
