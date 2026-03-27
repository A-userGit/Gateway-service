package com.shop.gatewayservice.util;

import com.shop.gatewayservice.exception.ClientCallException;
import com.shop.gatewayservice.exception.ExternalServiceCallException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
public class ExternalServiceCaller {

  private final int backoff;
  private final int maxRetry;
  private final WebClient webClient;

  public <T,V> Mono<T> sendPostWithRetry(V requiredInfo, String externalUri, Class<T> returnType) {
    return webClient.post().uri(externalUri).bodyValue(requiredInfo).retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(
            ClientCallException.clientFail(clientResponse.statusCode())))
        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(
            ExternalServiceCallException.callFail(clientResponse.statusCode(), externalUri)))
        .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(
            ExternalServiceCallException.callFail(clientResponse.statusCode(), externalUri)))
        .bodyToMono(returnType).retryWhen(Retry.backoff(maxRetry, Duration.ofSeconds(backoff))
            .filter(throwable -> throwable instanceof ExternalServiceCallException)
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
              ExternalServiceCallException failure = (ExternalServiceCallException) retrySignal.failure();
              throw ExternalServiceCallException.multipleCallsFail(failure);
            }));
  }

  public <V> Mono<ResponseEntity<Void>> sendDeleteWithRetry(String idParamName, V idParamValue,
      String externalUri) {
    String fullUri = String.format("%s?%s=%s", externalUri, idParamName, idParamValue);
    return webClient.delete().uri(fullUri).retrieve().onStatus(HttpStatusCode::is4xxClientError,
            clientResponse -> Mono.error(ClientCallException.clientFail(clientResponse.statusCode())))
        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(
            ExternalServiceCallException.callFail(clientResponse.statusCode(), externalUri)))
        .toBodilessEntity().retryWhen(Retry.backoff(maxRetry, Duration.ofSeconds(backoff))
            .filter(throwable -> throwable instanceof ExternalServiceCallException)
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
              ExternalServiceCallException failure = (ExternalServiceCallException) retrySignal.failure();
              throw ExternalServiceCallException.multipleCallsFail(failure);
            }));
  }
}
