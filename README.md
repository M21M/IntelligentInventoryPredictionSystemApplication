# Inventory Service

The **Inventory Service** is a core microservice in the **ML‑Enhanced Intelligent Inventory Prediction System**.  
It manages product stock data, integrates with PostgreSQL for persistence, and emits inventory change events to Kafka for downstream analytics and ML retraining pipelines.

> **Note:** This service does not perform any machine learning tasks. Predictions are handled by the Dashboard Service via the ML Prediction Service.

---

## 📌 Responsibilities
- Maintain inventory data for SKUs (CRUD operations).
- Persist inventory records in a PostgreSQL database.
- Publish inventory change events to Apache Kafka.
- Expose RESTful endpoints for inventory management.

---

## 🛠 Tech Stack
- **Java 17+**
- **Spring Boot** (Web, Data JPA, Actuator)
- **PostgreSQL** (Inventory schema)
- **Apache Kafka** (Event streaming)
- **Docker / Docker Compose** (for local environment)
- **Maven** (build & dependency management)

---

## 📂 Project Structure
inventory-service/

├── src/main/java/com/example/inventory

│ ├── controller/ # REST controllers

│ ├── model/ # Entity classes

│ ├── repository/ # JPA repositories

│ ├── service/ # Business logic

│ └── config/ # Kafka, DB configs

├── src/main/resources/

│ ├── application.yml # Configuration

│ └── db/migration/ # Flyway migrations

└── README.md

## 🚀 Getting Started

### Prerequisites
- Java 17 or newer
- Maven 3.8+
- Docker & Docker Compose

### Run Locally
```bash
# Clone repository
git clone https://github.com/yourusername/inventoryService.git
cd inventory-service

# Start PostgreSQL & Kafka using Docker Compose
docker-compose up -d

# Build and run the service
mvn clean install
mvn spring-boot:run