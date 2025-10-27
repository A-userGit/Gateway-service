package com.innowise.gatewayservice.util;

import com.innowise.gatewayservice.exception.ClientCallException;
import com.innowise.gatewayservice.exception.ExternalServiceCallException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
public class ExternalServiceCaller<T, K> {

  private final int backoff;
  private final int maxRetry;
  private final WebClient webClient;
  private final Class<T> clazz;

  public Mono<T> sendPostWithRetry(K requiredInfo, String externalUri) {
    return webClient.post().uri(externalUri).bodyValue(requiredInfo).retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(
            ClientCallException.clientFail(clientResponse.statusCode())))
        .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(
            ExternalServiceCallException.callFail(clientResponse.statusCode(), externalUri)))
        .onStatus(HttpStatusCode::is3xxRedirection, clientResponse -> Mono.error(
            ExternalServiceCallException.callFail(clientResponse.statusCode(), externalUri)))
        .bodyToMono(clazz).retryWhen(Retry.backoff(maxRetry, Duration.ofSeconds(backoff))
            .filter(throwable -> throwable instanceof ExternalServiceCallException)
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
              ExternalServiceCallException failure = (ExternalServiceCallException) retrySignal.failure();
              throw ExternalServiceCallException.multipleCallsFail(failure);
            }));
  }

  public Mono<ResponseEntity<Void>> sendDeleteWithRetry(String idParamName, K idParamValue,
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
