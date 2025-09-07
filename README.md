Horoscope API

Build user profiles, calculate astrological signs (sun, moon, rising), and fetch daily/weekly/monthly forecasts — all in one clean Spring Boot service.

🚀 Features

User Management (CRUD)

Create, list, get-by-id, update (partial), delete

Filtering via JPA

Find users by Sun, Moon, or Rising sign

Forecasting

Daily / Weekly / Monthly forecasts (cleaned text)

Strict Sign Calculation

Compute Sun/Moon/Rising from DOB, TOB, place, lat/lon, timezone

Robust Errors

Consistent HTTP status codes with ProblemDetail

OpenAPI / Swagger

Fully documented endpoints with examples

👾 Tech Stack

Java 21

Maven

Spring Boot

Spring Data JPA

MySQL

springdoc / Swagger OpenAPI

JUnit 5

Mockito

Jackson (ObjectMapper)

RestTemplate / HTTP

📖 API Documentation

Swagger UI
http://localhost:8080/swagger-ui/index.html

OpenAPI JSON
http://localhost:8080/v3/api-docs

Make sure the app is running before opening Swagger.

🧪 Running Tests
# all tests
./mvnw clean test

# single test (example)
./mvnw -Dtest=SunSignCalculatorTest test

🏃 Getting Started
# clone the project
git clone <YOUR_REPO_URL>
cd horoscope-api

# build
./mvnw clean install

# run
./mvnw spring-boot:run


The API will be available at http://localhost:8080.

🔬 Example Endpoints
1) Create User

POST /api/users

Request (JSON)

{
  "name": "Shannon",
  "dateOfBirth": "1990-01-01",
  "timeOfBirth": "08:30",
  "placeOfBirth": "London"
}


Response 201 Created
Location: /api/users/{id}
Body: UserProfile (includes derived Sun sign)

2) Calculate Signs (STRICT payload)

POST /api/users/{id}/calculate-signs

Important rules (read carefully):

All fields under subject are required.

Integers only for hour and minute. No leading zeros.

If born 03:07 AM → "hour": 3, "minute": 7 (✅) not 03 / 07 (❌)

Use a valid IANA timezone, e.g. Europe/London, America/New_York.
Reference: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones

Request (JSON)

{
  "subject": {
    "year": 1990,
    "month": 1,
    "day": 1,
    "hour": 3,
    "minute": 7,
    "city": "London",
    "name": "Shannon",
    "latitude": 51.5072,
    "longitude": -0.1276,
    "timezone": "Europe/London"
  }
}


Response 200 OK
Updated UserProfile with sunSign, moonSign, risingSign.

3) Forecasts (by user’s Sun sign)

GET /api/users/{id}/horoscope/daily

GET /api/users/{id}/horoscope/weekly

GET /api/users/{id}/horoscope/monthly

Response 200 OK — Simplified object:

{
  "sign": "Aries",
  "period": "weekly",
  "day": null,
  "text": "This week favors decisive moves and collaboration..."
}

4) Find Users by Sign (JPA filters)

GET /api/users/by-sun?sign=Aries

GET /api/users/by-moon?sign=Cancer

GET /api/users/by-rising?sign=Libra

Response 200 OK — UserProfile[]

⚠️ Error Handling

You’ll receive structured errors using ProblemDetail.

Example: Missing required fields in strict payload

{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Missing required field(s): subject.hour, subject.minute"
}


Example: User not found

{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "User not found with id 3fa85f64-5717-4562-b3fc-2c963f66afa6"
}

📝 Notes

Strict payload is enforced for /calculate-signs to ensure accurate astro calculations.

Forecast responses are cleaned for punctuation/ligatures to improve readability.

Use Swagger UI to try requests interactively and see request/response schemas.