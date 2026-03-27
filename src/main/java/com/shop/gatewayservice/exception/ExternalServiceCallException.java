package com.shop.gatewayservice.exception;

import org.springframework.http.HttpStatusCode;

public class ExternalServiceCallException extends RuntimeException {

  public ExternalServiceCallException(String message) {
    super(message);
  }

  public static ExternalServiceCallException callFail(HttpStatusCode code, String endpoint) {
    String message = String.format("Call to endpoint %s failed with error %s", endpoint, code);
    return new ExternalServiceCallException(message);
  }

  public static ExternalServiceCallException multipleCallsFail(Exception e) {
    String message = String.format("Call to external resource exhausted all retries with error %s",
        e.getMessage());
    return new ExternalServiceCallException(message);
  }

}
