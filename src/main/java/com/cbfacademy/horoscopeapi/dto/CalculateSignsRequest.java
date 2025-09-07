package com.cbfacademy.horoscopeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CalculateSignsRequest", description = "Strict payload for calculating signs.")
public class CalculateSignsRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    public Subject subject;

    @Schema(name = "CalculateSignsRequest.Subject")
    public static class Subject {
        @Schema(example = "1990")
        public Integer year;
        @Schema(example = "1")
        public Integer month;
        @Schema(example = "1")
        public Integer day;
        @Schema(example = "8")
        public Integer hour;
        @Schema(example = "30")
        public Integer minute;
        @Schema(example = "London")
        public String city;
        @Schema(example = "Shannon")
        public String name;
        @Schema(example = "51.5072")
        public Double latitude;
        @Schema(example = "-0.1276")
        public Double longitude;
        @Schema(example = "Europe/London")
        public String timezone;
    }
}
