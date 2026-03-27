package com.shop.gatewayservice.exception.handler;

import com.shop.gatewayservice.exception.ClientCallException;
import com.shop.gatewayservice.exception.ExternalServiceCallException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ErrorHandlerController {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Mono<ResponseEntity<Map<String, String>>> handleValidationExceptions(final
  MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage()));
    return Mono.just(new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public Mono<ResponseEntity<Map<String, String>>> handleConstraintViolationException(
      final ConstraintViolationException e) {
    Map<String, String> errors = new HashMap<>();
    for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
      errors.put(constraintViolation.getRootBeanClass().getSimpleName() + "."
          + constraintViolation.getPropertyPath(), e.getMessage());
    }
    return Mono.just(new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST));
  }


  @ExceptionHandler(ClientCallException.class)
  public Mono<ResponseEntity<String>> handleClientCallException(final ClientCallException e) {
    return Mono.just(ResponseEntity.badRequest().header("error", e.getMessage()).build());
  }

  @ExceptionHandler(ExternalServiceCallException.class)
  public Mono<ResponseEntity<String>> handleExternalServiceCallException(
      final ExternalServiceCallException e) {
    return Mono.just(ResponseEntity.badRequest().header("error", e.getMessage()).build());
  }

  @ExceptionHandler(Forbidden.class)
  public Mono<ResponseEntity<String>> handleForbiddenException(final Forbidden e) {
    return Mono.just(new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN));
  }

  @ExceptionHandler(Unauthorized.class)
  public Mono<ResponseEntity<String>> handleUnauthorizedException(final Unauthorized e) {
    return Mono.just(new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED));
  }
}
