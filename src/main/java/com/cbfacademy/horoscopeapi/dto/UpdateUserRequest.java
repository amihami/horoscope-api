package com.cbfacademy.horoscopeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdateUserRequest", description = "Any subset of fields may be provided.")
public class UpdateUserRequest {
    public String name;
    @Schema(example = "1995-02-12")
    public String dateOfBirth;
    @Schema(example = "09:45")
    public String timeOfBirth;
    @Schema(example = "Manchester")
    public String placeOfBirth;
    @Schema(example = "51.5072")
    public String latitude;
    @Schema(example = "-0.1276")
    public String longitude;
    @Schema(example = "Europe/London")
    public String timezone;
}