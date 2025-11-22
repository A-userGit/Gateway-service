package com.innowise.gatewayservice.sevice.impl;

import com.innowise.gatewayservice.config.RegistrationProperties;
import com.innowise.gatewayservice.dto.AccessDataDto;
import com.innowise.gatewayservice.dto.UserDataDto;
import com.innowise.gatewayservice.dto.external.AuthResponseDto;
import com.innowise.gatewayservice.dto.external.CreateUserDto;
import com.innowise.gatewayservice.dto.external.UserCredentialsDto;
import com.innowise.gatewayservice.dto.external.UserDto;
import com.innowise.gatewayservice.exception.ClientCallException;
import com.innowise.gatewayservice.sevice.AccessService;
import com.innowise.gatewayservice.util.ExternalServiceCaller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccessServiceImpl implements AccessService {

  private final WebClient webClient = WebClient.create();
  private final RegistrationProperties registrationProperties;

  @Override
  public Mono<AccessDataDto> signup(UserDataDto userData) {
    Mono<AuthResponseDto> authResponseMono = registerOnAuthServer(userData);
    return authResponseMono.flatMap((authResponse) -> {
      if (authResponse != null) {
        return registerOnUserService(userData, authResponse.userId())
            .map(userResponse -> new AccessDataDto(userResponse.id()))
            .doOnError(e -> rollBackUserRegistration(authResponse.tempAbortCode()));
      } else {
        return Mono.error(ClientCallException.clientFail(HttpStatusCode.valueOf(500)));
      }
    });
  }

  @Override
  public void rollBackUserRegistration(String tempDeletionCode) {
    ExternalServiceCaller<AuthResponseDto, String> caller = new ExternalServiceCaller<>(
        registrationProperties.getBackoffDelay(), registrationProperties.getMaxAttempts(),
        webClient, AuthResponseDto.class);
    caller.sendDeleteWithRetry(registrationProperties.getParam().getAuthServerDelete(),
        tempDeletionCode,
        registrationProperties.getUri().getAuthServerDelete()).subscribe();
  }

  @Override
  public Mono<AuthResponseDto> registerOnAuthServer(UserDataDto userData) {
    UserCredentialsDto credentialsDto = new UserCredentialsDto(userData.email(),
        userData.password());
    ExternalServiceCaller<AuthResponseDto, UserCredentialsDto> caller = new ExternalServiceCaller<>(
        registrationProperties.getBackoffDelay(), registrationProperties.getMaxAttempts(),
        webClient, AuthResponseDto.class);
    return caller.sendPostWithRetry(credentialsDto,
        registrationProperties.getUri().getAuthServerCreate());
  }

  @Override
  public Mono<UserDto> registerOnUserService(UserDataDto userData, long externalId) {
    CreateUserDto createUserDto = new CreateUserDto(userData.name(), userData.surname(), externalId,
        userData.birthDate(),
        userData.email());
    ExternalServiceCaller<UserDto, CreateUserDto> caller = new ExternalServiceCaller<>(
        registrationProperties.getBackoffDelay(), registrationProperties.getMaxAttempts(),
        webClient, UserDto.class);
    return caller.sendPostWithRetry(createUserDto,
        registrationProperties.getUri().getUserServiceCreate());
  }
}
