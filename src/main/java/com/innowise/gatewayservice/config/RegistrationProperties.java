package com.innowise.gatewayservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "signup.external")
public class RegistrationProperties {
  private int maxAttempts;
  private int backoffDelay;
  private PathProperties uri;
  private ParamProperties param;

  public RegistrationProperties() {
    uri = new PathProperties();
    param = new ParamProperties();
  }

  @Getter
  @Setter
  public class PathProperties{
    private String authServerCreate;
    private String authServerDelete;
    private String userServiceCreate;
  }

  @Getter
  @Setter
  public class ParamProperties{
    private String authServerDelete;
  }
}
