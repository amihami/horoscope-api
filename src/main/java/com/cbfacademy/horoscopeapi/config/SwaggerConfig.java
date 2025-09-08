package com.cbfacademy.horoscopeapi.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Horoscope API")
                        .description(
                                "A horoscope API that allows you to find your star/moon/rising sign and get a daily, weekly and monthly reading.")
                        .version("v1")
                        .license(new License().name("MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project README")
                        .url("https://github.com/amihami/horoscope-api"));
    }

    @Bean
    public GroupedOpenApi usersGroup() {
        return GroupedOpenApi.builder()
                .group("users")
                .pathsToMatch("/api/users/**")
                .build();
    }
}