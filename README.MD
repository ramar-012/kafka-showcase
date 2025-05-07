# Kafka Order Management POC

A simple **Spring Boot Proof of Concept (POC)** demonstrating asynchronous, event-driven architecture using **Apache Kafka**. This project simulates an **Order Management System (OMS)** where various services communicate via Kafka topics to handle orders, inventory, and payment processes independently.

---

### Features

- Publish and consume Kafka events
-  Category-based partitioning logic for specific topics
-  Asynchronous service communication
-  Kafka topic creation via Spring Boot config on app startup
-  MySQL integration for persisting order and inventory data
-  Clean and simple code structure for learning and demonstration

---

### Tech Stack

- Java 17
- Spring Boot
- Apache Kafka
- MySQL

---

### Kafka Setup (Local)

- Download Apache Kafka from the [official website](https://kafka.apache.org/downloads) and extract the folder.
- Move the extracted folder from `Downloads` to `C:\` (Windows C drive).
- Open the terminal, navigate to the Kafka folder, and run it using the following commands.


**i. Start Zookeeper**

```
.\zookeeper-server-start.bat ..\config\zookeeper.properties
```
**ii. Start Kafka broker**
```
.\kafka-server-start.bat ..\config\server.properties
```
**iii. Create a topic manually (if needed)**
```
.\kafka-topics.bat --create --topic order-created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```
---
### Kafka set up (Docker)
- Create a docker-compose.yml file to be used by Docker Compose to define, run zookeeper and kafka container Docker applications.
- Run the containers: `docker-compose up -d`
- Verify if the containers have started running: `docker ps`
- Verify the topics
```bash
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list
```
- Create a topic manually if needed
```bash
docker exec -it kafka kafka-topics --create --topic order-created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```
---
### 📘 API Endpoints

| Method | Endpoint             | Description                                               |
|--------|----------------------|-----------------------------------------------------------|
| POST   | `/kafka/send`     | Send a sample message to a specific topic for testing |
| POST   | `/kafka/orders/create`     | Create a new order and publish to a Kafka topic          |
| POST   | `/kafka/orders/specific-create` | Send an order to a specific partition based on category |

---
## Running the App
- Start Kafka via local terminal setup or Docker Compose.
- Run the Spring Boot application.
- Use Postman or curl to call the APIs.
- Observe Kafka logs for produced and consumed messages.

## License
This project is free to use for learning and demonstration purposes.




