package com.innowise.gatewayservice.dto.external;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class UserDto {

  private long id;
  private String name;
  private String surname;
  private String birthDate;
  private String email;

}
