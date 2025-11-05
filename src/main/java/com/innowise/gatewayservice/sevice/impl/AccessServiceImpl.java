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
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccessServiceImpl implements AccessService {

  private final RegistrationProperties registrationProperties;
  private final ExternalServiceCaller caller;

  @Override
  public Mono<AccessDataDto> signup(UserDataDto userData) {
    Mono<AuthResponseDto> authResponseMono = registerOnAuthServer(userData);
    return authResponseMono.flatMap((authResponse) -> {
      if (authResponse != null) {
        return registerOnUserService(userData, authResponse.getUserId())
            .map(userResponse -> new AccessDataDto(userResponse.getId()))
            .doOnError(e -> rollBackUserRegistration(authResponse.getTempAbortCode()));
      } else {
        return Mono.error(ClientCallException.clientFail(HttpStatusCode.valueOf(500)));
      }
    });
  }

  @Override
  public void rollBackUserRegistration(String tempDeletionCode) {
    caller.sendDeleteWithRetry(registrationProperties.getParam().getAuthServerDelete(),
        tempDeletionCode,
        registrationProperties.getUri().getAuthServerDelete()).subscribe();
  }

  @Override
  public Mono<AuthResponseDto> registerOnAuthServer(UserDataDto userData) {
    UserCredentialsDto credentialsDto = new UserCredentialsDto(userData.email(),
        userData.password());
    return caller.sendPostWithRetry(credentialsDto,
            registrationProperties.getUri().getAuthServerCreate(), AuthResponseDto.class);
  }

  @Override
  public Mono<UserDto> registerOnUserService(UserDataDto userData, long externalId) {
    CreateUserDto createUserDto = new CreateUserDto(userData.name(), userData.surname(), externalId,
        userData.birthDate(),
        userData.email());
    return caller.sendPostWithRetry(createUserDto,
        registrationProperties.getUri().getUserServiceCreate(), UserDto.class);
  }
}
