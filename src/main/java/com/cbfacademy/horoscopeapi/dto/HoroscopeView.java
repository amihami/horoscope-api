package com.cbfacademy.horoscopeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HoroscopeView", description = "Simplified horoscope payload returned by this API.")
public class HoroscopeView {

    @Schema(description = "User's Sun sign", example = "Aries")
    private String sign;

    @Schema(description = "Forecast period", example = "daily",
            allowableValues = {"daily", "weekly", "monthly"})
    private String period;

    @Schema(description = "Day label for daily forecasts (null for weekly/monthly)",
            example = "today", nullable = true)
    private String day;

    @Schema(description = "Plain text horoscope", example = "Momentum builds as you take initiative today...")
    private String text;

    public String getSign() { return sign; }
    public void setSign(String sign) { this.sign = sign; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}