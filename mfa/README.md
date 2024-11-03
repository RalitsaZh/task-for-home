# Multi-Factor Authentication (MFA) API Documentation

This project provides a Java-based API for handling Multi-Factor Authentication (MFA) by sending verification codes to users via email. It utilizes Kafka for message queuing and SendGrid for email delivery. The API includes two main endpoints, focusing on sending and verifying MFA codes.

---

## Endpoints

### 1. `/send` - Send MFA Code

- **HTTP Method:** POST
- **Description:** This endpoint accepts an email address and triggers the process to generate and send an MFA code to the specified email.
- **Request Parameters:**
    - `email` (String): The email address to which the MFA code should be sent.
- **Response:** Returns a success message upon successful code generation and dispatch, or an error message if the process fails.

### 2. `/verify` - Verify MFA Code

- **HTTP Method:** GET
- **Description:** This endpoint accepts an email address and MFA code, verifying the code for this email.
- **Request Parameters:**
    - `email` (String): The email address for which the MFA code is being verified.
    - `code` (String): The MFA code to be validated for the specified email.
- **Response:**
    - **200 OK:** "MFA code verified successfully" if the code is valid.
    - **400 Bad Request:** "Invalid MFA code." if the code does not match or has expired.
    - **500 Internal Server Error:** An error message for unexpected issues like database or server failures.

---

## Code Flow and Components

### Controller Layer

- **`sendMfaEmail` Method:** Handles `/send` requests, capturing the email parameter and calling the `send` method in the Service layer. Includes error handling to manage exceptions during the MFA sending process.
- **`verifyMfaCode` Method:** Handles `/verify` requests, capturing the email and code parameters, and calls `verifyMfaCode` in the Service layer.
  Both methods employ Redis to monitor and enforce rate limits, maintaining control over the number of requests that can be made by a single user within a defined timeframe.

### Service Layer

#### `MFAService` (sendMfaEmail method)
- Accepts the email parameter and calls `VerificationCodeService` to generate a unique MFA code, then saves it to the database.
- Calls `EmailPublisherService` to publish the message (containing the email and verification code) to a Kafka topic.

#### `VerificationCodeService`
- **`createAndSaveVerificationCode:`** Checks for an active verification code associated with the email within a valid period, reusing it if available or creating a new one otherwise.
- **`generateUniqueVerificationCode:`** Calls `generateVerificationCode` repeatedly to ensure uniqueness, throwing an exception after a set limit.
- **`generateVerificationCode:`** Generates a random verification code by creating a UUID substring.

#### `EmailPublisherService`
- **`sendMail:`** Publishes a message to Kafka, containing the email and verification code, enabling asynchronous processing.

#### `EmailConsumerService`
- Listens for messages from `EmailPublisherService` on Kafka. Upon receiving a message, sends the email using SendGrid.

#### `RateLimiterService`
- Manages rate limits on user actions using Redis, checking if the action for a given email exceeds the allowed limit.

### Repository

- **`VerificationCodeRepository:`** Manages verification codes in the database, including CRUD operations and specialized verification queries.

---

## Project Configuration

### `application.properties`
- **Database Connection:** PostgreSQL at `jdbc:postgresql://localhost:5432/nexo_db` with specified credentials.
- **Email Sending:** SendGrid integration using `spring.sendgrid.api-key` and `spring.mail.from-address`.
- **Messaging:** Kafka configurations for real-time data processing. Topic: `mfa_notification`, Group: `email-group`.
- **Redis:** Configured for caching on `localhost` at port `6379`.

### DockerFile
- Multi-stage build: Maven image for compilation and a lightweight runtime image for execution. Port `8080` is exposed for network access.

---

## Docker Setup

- To manage and run services, this project uses Docker and Docker Compose.

1. **Build Docker Image**
   - docker build -t mfa:5.0 .

- This command builds the Docker image for the application and tags it as mfa:5.0.
2. **To start all services, navigate to the root directory containing the docker-compose.yml file and run:**
    - docker compose up

3. **To stop the services, use:**
   - docker compose down


## Quick Start Guide
### Step 1: Update application.properties
In the application.properties file, locate `***REMOVED***` and replace it with the KEY that I sent to you via email.

### Step 2: Build the Docker Image
Run the following command to build the Docker image for the application:

 - docker build -t mfa:5.0 .
	
### Step 3: Start the Application with Docker Compose
- docker compose up

### Step 4: Send MFA Code 
(Note: The email may arrive in your spam folder, and there could be some delay.) 
Using Postman, send a POST request to trigger the sending of an MFA code to your email. Replace YOUR_EMAIL with the email address where you want to receive the MFA code:
- POST http://localhost:8080/api/mfa/send?email=YOUR_EMAIL

### Step 5: Verify the MFA Code 
Once you receive the code in your email, verify it by sending GET request. Replace YOUR_EMAIL with the email address you used in the previous step, and replace CODE with the actual code you received:
- GET http://localhost:8080/api/mfa/verify?email=YOUR_EMAIL&code=CODE



## Services that are used

###  Main Application 
The app service runs the core application with 3 replicas for load balancing, connecting to PostgreSQL, Kafka, and Redis for data, messaging, and caching, and is dependent on the db, cache, and kafka services.
###  nginx 
The nginx service functions as a reverse proxy, forwarding external requests to the app service and exposing port 8080 on the host.
###  PostgreSQL Database 
The db service provides PostgreSQL for application data storage, with data persisted on the host and configured with a specific username, password, and database name.
###  Redis Cache 
The cache service supplies Redis as a caching layer for fast data access and is set to automatically restart for high availability.
###  Zookeeper 
The zookeeper service coordinates with Kafka to support the distributed messaging infrastructure.
###  Kafka 
The kafka service serves as a message broker for inter-service communication, connecting to ZooKeeper and configured to create a topic on startup.
