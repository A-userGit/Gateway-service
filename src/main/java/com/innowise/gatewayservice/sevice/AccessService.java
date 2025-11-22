package com.innowise.gatewayservice.sevice;

import com.innowise.gatewayservice.dto.AccessDataDto;
import com.innowise.gatewayservice.dto.UserDataDto;
import com.innowise.gatewayservice.dto.external.AuthResponseDto;
import com.innowise.gatewayservice.dto.external.UserDto;
import reactor.core.publisher.Mono;

public interface AccessService {

  Mono<AccessDataDto> signup(UserDataDto userData);

  void rollBackUserRegistration(String tempDeletionCode);

  Mono<AuthResponseDto> registerOnAuthServer(UserDataDto userData);

  Mono<UserDto> registerOnUserService(UserDataDto userData, long externalId);
}
