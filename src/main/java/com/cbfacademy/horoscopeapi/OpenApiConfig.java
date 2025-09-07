package com.cbfacademy.horoscopeapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Horoscope API", version = "1.0.0", description = "Find your star/moon/rising sign and get a daily, weekly and monthly reading.", license = @License(name = "MIT")), servers = {
        @Server(url = "http://localhost:8080", description = "Local")
})
public class OpenApiConfig {
}