# 🌦️ Event Weather Guard

A Spring Boot backend service that analyzes hourly weather forecasts and determines whether an outdoor event is **Safe**, **Risky**, or **Unsafe** based on deterministic, explainable rules.

This project was built as a **backend intern take‑home assignment** focusing on API design, external integration, validation, and clean business logic.

---

## 🚀 Features

* REST API to evaluate weather risk for outdoor events
* Integration with **Open‑Meteo** public weather API (hourly forecasts)
* Deterministic and explainable classification rules
* Input validation with clear error responses
* Clean layered architecture (Controller → Service → Client → Rule Engine)
* Swagger / OpenAPI documentation
* No database or authentication (as per requirements)

---

## 🛠️ Tech Stack

* **Java 17**
* **Spring Boot 3.x**
* Spring MVC
* WebClient (blocking usage)
* Bean Validation (Jakarta Validation)
* Lombok
* Springdoc OpenAPI (Swagger UI)

---

## 📦 API Endpoint

### `POST /api/v1/event-forecast`

Evaluates weather conditions during the event time window and returns a risk classification.

### Request Body

```json
{
  "name": "Football Match",
  "location": {
    "latitude": 19.0760,
    "longitude": 72.8777
  },
  "startTime": "2026-02-05T17:00:00",
  "endTime": "2026-02-05T19:00:00"
}
```

### Response Body

```json
{
  "classification": "SAFE",
  "summary": "Weather conditions are stable",
  "reason": ["Weather conditions are stable"],
  "eventWindowForecast": [
    {
      "time": "17:00",
      "rainProbability": 0,
      "windKmh": 3.1
    },
    {
      "time": "18:00",
      "rainProbability": 0,
      "windKmh": 2.6
    }
  ]
}
```

---

## ⚖️ Weather Classification Rules

The service classifies events using **deterministic rules**:

### ❌ Unsafe

* Rain probability > **80%** during any event hour, OR
* Wind speed > **40 km/h** during any event hour

### ⚠️ Risky

* Rain probability > **60%** during any event hour

### ✅ Safe

* None of the above conditions are met

Each response includes **human‑readable reasons** explaining which rule(s) were triggered.

---

## 🧠 Design Overview

* **Controller**: Handles HTTP requests and validation
* **Service Layer**: Orchestrates flow and validates business rules
* **WeatherApiClient**: Isolates external API integration (Open‑Meteo)
* **Rule Engine**: Applies deterministic weather classification logic
* **DTOs**: Clean request/response models with validation
* **Global Exception Handler**: Centralized error handling

This separation keeps the system **clean, testable, and easy to explain**.

---

## 📘 Swagger / OpenAPI

Interactive API documentation is available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## ▶️ Running the Application

### Prerequisites

* Java 17+
* Maven

### Steps

```bash
mvn clean install
mvn spring-boot:run
```

The service will start at:

```
http://localhost:8080
```

---

## ⚠️ Validation & Error Handling

* Request fields are validated using Bean Validation
* Invalid inputs return `400 Bad Request` with descriptive messages
* Logical validation ensures `startTime < endTime`

Example error response:

```json
{
  "startTime": "Start time is required",
  "location.latitude": "Latitude must be >= -90"
}
```

---

## 🔍 Key Assumptions & Trade‑offs

* Forecast data is evaluated only for the provided time window
* Classification is rule‑based (no ML / probabilistic modeling)
* No persistence layer is used
* WebClient is used in blocking mode for simplicity

---

## 🧪 Unit Testing

The project includes focused **unit tests only for the service layer and business logic**, avoiding controller-level tests to keep the test suite fast and deterministic.

### Test Structure

The test directory mirrors the main source structure:

```
src/test/java/com/apora/eventweatherguard
└── service
    ├── WeatherRuleEngineTest.java
    └── EventForecastServiceImplTest.java
```

### Testing Strategy

* **WeatherRuleEngineTest**

    * Verifies Safe / Risky / Unsafe classification logic
    * Covers edge cases such as empty forecasts

* **EventForecastServiceImplTest**

    * Validates request time constraints (`startTime < endTime`)
    * Mocks external dependencies (Weather API client, Rule Engine)
    * Ensures correct orchestration and delegation

Tests are written using **JUnit 5** and **Mockito** and are fully deterministic (no external API calls).

Run tests using:

```bash
mvn test
```

---

## 🌟 Possible Extensions

* Numeric severity score (0–100)
* Alternative safe time recommendation
* Caching weather API responses
* Unit & integration test coverage

---

## 👤 Author

Built as part of a backend intern take‑home assignment to demonstrate:

* Clean API design
* External service integration
* Business rule modeling
* Readable and maintainable code

---
