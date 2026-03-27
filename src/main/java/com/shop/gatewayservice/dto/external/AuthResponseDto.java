package com.shop.gatewayservice.dto.external;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class AuthResponseDto {

  private long userId;
  private String tempAbortCode;

}
