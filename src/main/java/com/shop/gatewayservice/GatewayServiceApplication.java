package com.shop.gatewayservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {@Server(url = "${swagger-server}")},
    info = @Info(title = "Gateway service API",
        version = "1.0",
        description = "Gateway service")
)
@SecurityScheme(name = "gateway_security",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(authorizationCode =
    @OAuthFlow(authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}",
        tokenUrl = "${springdoc.oAuthFlow.tokenUrl}"
    ))
)
@SpringBootApplication
public class GatewayServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayServiceApplication.class, args);
  }
}
