package com.shop.gatewayservice.dto.external;

public record CreateUserDto(String name, String surname, long externalId,
                            String birthDate, String email) {

}
