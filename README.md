<div align="center">
  <img src="docs/images/HoroscopeImage.jpg" alt="logo" width="auto" height="auto" />
  <h1>Horoscope API</h1>
  <p>Calculate your Sun/Moon/Rising signs and find your daily/weekly/monthly forecasts.</p>

  <a href="https://img.shields.io/badge/LANGUAGE-JAVA-brightgreen">
    <img src="https://img.shields.io/badge/LANGUAGE-JAVA-brightgreen" alt="Static Badge" />
  </a>

---

</div>

_Please read all the way through before starting_

### ✨ Features

- User management (create, read, update, delete)
- Find **Sun sign** from date of birth
- Calculate **Sun/Moon/Rising** via external service
- Find **daily/weekly/monthly** forecasts for a user’s Sun sign
- Filter and find users by **Sun/Moon/Rising** sign

---

### ✨ Tech Stack

- Java 21
- Maven
- Spring Boot
- Spring Data JPA
- MySQL
- Swagger OpenAPI
- JUnit 5
- Jackson (ObjectMapper)
- RestTemplate/HTTP

---

### ✨ API Documentation

- _Swagger UI_ (make sure API is running)

```
http://localhost:8080/swagger-ui/index.html
```

---

### ✨ Swagger

<div align="center">
  <img src="docs/images/SwaggerUI.png" alt="logo" width="auto" height="auto" />
</div>

---

### ✨ Testing
For **macOS/Linux/Git Bash**

- To run all tests:
    ```bash
    ./mvnw clean test
    ```

- To run the `SunSignCalculatorTest` alone:
    ```bash
    ./mvnw -Dtest=SunSignCalculatorTest test
    ```

- To run `UserServiceTest` alone:
    ```bash
    ./mvnw -Dtest=UserServiceTest test
    ```

For **Windows PowerShell/CMD**
- To run all tests:
    ```powershell
    .\mvnw.cmd clean test
    ```

- To run the `SunSignCalculatorTest` alone:
    ```powershell
    .\mvnw.cmd -Dtest=SunSignCalculatorTest test
    ```

- To run `UserServiceTest` alone:
    ```powershell
    .\mvnw.cmd -Dtest=UserServiceTest test
    ```
---

### ✨ Setting Up

#### 1. Clone the repository

```bash
git clone https://github.com/amihami/horoscope-api
cd horoscope-api
```

#### 2. Build

**macOS / Linux / Git Bash on Windows**

```bash
./mvnw clean install
```

**Windows (PowerShell)**

```powershell
.\mvnw.cmd clean install
```

**Windows (CMD)**

```cmd
mvnw.cmd clean install
```

#### 3. Run the API (available at http://localhost:8080)

**macOS / Linux / Git Bash on Windows**

```bash
./mvnw spring-boot:run
```

**Windows (PowerShell)**

```powershell
.\mvnw.cmd spring-boot:run
```

**Windows (CMD)**

```cmd
mvnw.cmd spring-boot:run
```

---

### ✨ API Endpoints

<sub>(_all requests JSON_)</sub>

**1. User Management**

- `POST /api/users` — Create user
- `GET /api/users` — List users
- `GET /api/users/{id}` — Get user by id
- `PUT /api/users/{id}` — Update user (partial)
- `DELETE /api/users/{id}` — Delete user

**2. Forecasts (by user’s Sun sign)**

- `GET /api/users/{id}/horoscope/daily`
- `GET /api/users/{id}/horoscope/weekly`
- `GET /api/users/{id}/horoscope/monthly`

**3. Calculate Signs (STRICT payload)**

- `POST /api/users/{id}/calculate-signs`

**4. Find Users by Sign (JPA filters)**

- `GET /api/users/by-sun?sign=Aries`
- `GET /api/users/by-moon?sign=Cancer`
- `GET /api/users/by-rising?sign=Libra`

---

### ✨ Important to Note

- The endpoint /calculate-signs has a strict payload enforced to ensure the accuracy of sign calculations.
  - All fields under subject are _required_
    ```json
    {
      "subject": {
        "year": 1990,
        "month": 1,
        "day": 1,
        "hour": 8,
        "minute": 30,
        "city": "London",
        "name": "Shannon",
        "latitude": 51.5072,
        "longitude": -0.1276,
        "timezone": "Europe/London"
      }
    }
    ```
  - For the fields _hour_ and _minute_, only integers values are excepted. Make sure there are **no leading zeros**.
  - E.g. If born 03:07 AM → "hour": 3, "minute": 7 ✅ not "hour": 03, "minute": 07 ❌
  - Be sure to use a valid IANA timezone, e.g. Europe/London, Africa/Nairobi.
    - Reference: https://en.wikipedia.org/wiki/List_of_tz_database_time_zones
- Forecast responses are cleaned for punctuation/ligatures to improve readability.

Use **Swagger UI** (link above) to try requests interactively and see request/response schemas.

### ✨ Happy Forcasting ✨
