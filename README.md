# IBM Task Comment-to-Ticket Triage (Backend)

The backend is built with **Java** and **Spring Boot**, exposing RESTful APIs to submit comments and manage generated tickets.

## Technologies Used
- Java 17
- Spring Boot 4
- Spring Web (REST API)
- H2 Database (in-memory)
- Hugging Face Inference API  
  - Model: `MiniMaxAI/MiniMax-M2.5:novita`

## Features
- Submit comments.
- Automatic AI analysis of comments to determine if they should become tickets.
- Generate structured ticket data for relevant comments:
  - **Title**
  - **Category** (bug / feature / billing / account / other)
  - **Priority** (low / medium / high)
  - **Short summary**
- Store comments and tickets in memory (H2 database).
- REST API endpoints for retrieving comments and tickets.

---

## Getting Started

### Prerequisites
- Java 17
- Maven

### Steps to Run Locally

1. Clone the repository:
   git clone https://github.com/sibluK/ibm-ticket-system-backend.git

2. Navigate to the project folder:
   cd ibm-ticket-system-backend

3. Build and run the application:

   mvn clean install

   mvn spring-boot:run

4. The API will be available at:
   http://localhost:8080/api

## Environment Variables

To run this project, you will need to add the following environment variables to your .env file

`FRONTEND_URL`
`HF_ACCESS_TOKEN`
`HF_TEXT_API_URL`
`HF_MODEL`

