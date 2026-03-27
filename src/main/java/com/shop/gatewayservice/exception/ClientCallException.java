package com.shop.gatewayservice.exception;

import org.springframework.http.HttpStatusCode;

public class ClientCallException extends RuntimeException {

  public ClientCallException(String message) {
    super(message);
  }

  public static ClientCallException clientFail(HttpStatusCode code) {
    String message = String.format(
        "Call to external service failed due to the client fail with error %s", code);
    return new ClientCallException(message);
  }

}
