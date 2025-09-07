package com.cbfacademy.horoscopeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreateUserRequest", description = "Payload to create a user.")
public class CreateUserRequest {
    @Schema(example = "Shannon", requiredMode = Schema.RequiredMode.REQUIRED)
    public String name;

    @Schema(example = "1990-01-01", description = "ISO-8601 date", requiredMode = Schema.RequiredMode.REQUIRED)
    public String dateOfBirth;

    @Schema(example = "08:30", description = "ISO local time (optional, HH:mm)")
    public String timeOfBirth;

    @Schema(example = "London", description = "Optional birth place")
    public String placeOfBirth;
}