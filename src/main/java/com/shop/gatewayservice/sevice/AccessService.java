package com.shop.gatewayservice.sevice;

import com.shop.gatewayservice.dto.AccessDataDto;
import com.shop.gatewayservice.dto.UserDataDto;
import com.shop.gatewayservice.dto.external.AuthResponseDto;
import com.shop.gatewayservice.dto.external.UserDto;
import reactor.core.publisher.Mono;

public interface AccessService {

  Mono<AccessDataDto> signup(UserDataDto userData);

  void rollBackUserRegistration(String tempDeletionCode);

  Mono<AuthResponseDto> registerOnAuthServer(UserDataDto userData);

  Mono<UserDto> registerOnUserService(UserDataDto userData, long externalId);
}
