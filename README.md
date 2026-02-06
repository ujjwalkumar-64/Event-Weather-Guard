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

---

## 📤 API Usage Examples

### Example 1: Safe Event

**Request**

```json
{
  "name": "Morning Yoga",
  "location": {
    "latitude": 19.0760,
    "longitude": 72.8777
  },
  "startTime": "2026-02-05T06:00:00",
  "endTime": "2026-02-05T08:00:00"
}
```

**Response**

```json
{
  "classification": "SAFE",
  "severityScore": 6,
  "summary": "Weather conditions are stable",
  "reason": ["Weather conditions are stable"],
  "eventWindowForecast": [
    {
      "time": "2026-02-05T06:00:00",
      "rainProbability": 0,
      "windKmh": 4.5
    },
    {
      "time": "2026-02-05T07:00:00",
      "rainProbability": 0,
      "windKmh": 1.5
    },
    {
      "time": "2026-02-05T08:00:00",
      "rainProbability": 0,
      "windKmh": 8
    }
  ],
  "recommendedWindow": null
}
```

---

### Example 2: Risky Event with Recommendation

**Request**

```json
{
  "name": "Football Match",
  "location": {
    "latitude": 52.52,
    "longitude": 13.41
  },
  "startTime": "2026-02-05T17:00:00",
  "endTime": "2026-02-05T19:00:00"
}
```

**Response**

```json
{
  "classification": "RISKY",
  "severityScore": 46,
  "summary": "High chance of rain during event window",
  "reason": ["High chance of rain during event window"],
  "eventWindowForecast": [
    { "time": "2026-02-05T17:00:00",
      "rainProbability": 53, 
      "windKmh": 10.8
    },
    { "time": "2026-02-05T18:00:00",
      "rainProbability": 38, 
      "windKmh": 10.4
    }, 
    { "time": "2026-02-05T19:00:00", 
      "rainProbability": 63, 
      "windKmh": 10.8
    }
  ],
  "recommendedWindow": {
    "startTime": "2026-02-06T13:00:00",
    "endTime": "2026-02-06T15:00:00",
    "severityScore": 4,
    "reason": "Lower rain probability and calmer winds"
  }
}
```

---

## ⚖️ Weather Classification Rules

The system applies **deterministic, explainable rules** based on hourly weather data.

### ❌ Unsafe

* Rain probability **> 80%** during any event hour, OR
* Wind speed **> 40 km/h** during any event hour

### ⚠️ Risky

* Rain probability **> 60%** during any event hour

### ✅ Safe

* None of the above conditions are met

### 🎯 Severity Score (0–100)

In addition to classification, a numeric severity score is computed:

* Rain severity = rain probability (0–100)
* Wind severity = normalized to 0–100 (50 km/h treated as extreme)
* Hourly severity = `0.6 × rain + 0.4 × wind`
* Event severity = **maximum hourly severity** (worst‑case driven)

Severity score is **additive** and does not override classification rules.

---

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

## 🧪 Testing Strategy

The project includes **unit tests focused on the service layer and core business logic**, in line with the scope of the assignment.

### Unit Tests Included

* **WeatherRuleEngineTest**

    * Verifies SAFE / RISKY / UNSAFE classification logic
    * Covers rain and wind threshold edge cases
    * Ensures deterministic, explainable behavior

* **EventForecastServiceImplTest**

    * Validates business rule: `startTime < endTime`
    * Mocks external dependencies (Weather API client, Rule Engine)
    * Ensures correct orchestration and delegation

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

## ⚙️ Setup Instructions

### Prerequisites

* Java **17+**
* Maven **3.8+**
* Internet access (for Open‑Meteo API)

### Setup & Run

```bash
mvn clean install
mvn spring-boot:run
```

The application will start at:

```
http://localhost:8080
```

Swagger UI:

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

* Weather forecasts are **hourly**, and all evaluations align strictly to hourly boundaries
* Classification logic is **rule‑based and deterministic** for transparency and explainability
* The **severity score** is additive and does not override classification rules
* Alternate time recommendations are generated **only for RISKY or UNSAFE events**
* Recommended windows preserve the **original event duration** but shift the time window to reduce weather risk
* External weather API failures result in a fast‑fail error response
* No persistence layer or authentication is included (out of scope)
* Controller tests are intentionally omitted; unit tests focus on **service‑layer business logic**
