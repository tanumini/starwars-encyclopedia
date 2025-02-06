
# Star Wars Information Retrieval System

## Overview

This is a full-stack microservice-based system for retrieving Star Wars information. The backend is built using Spring Boot, and the frontend uses React.js with Bootstrap for styling. The system allows users to search for details about Planets, Spaceships, Vehicles, People, Films, and Species by selecting the type and entering the name.

## Features

- **Search Functionality**: Allows users to search for different types of Star Wars entities such as Planets, Vehicles, etc., by changing the 'type' and entering a name.
- **Offline Mode**: The system supports offline mode, which is handled entirely by the backend. The frontend does not require any special implementation for offline functionality.
- **CI/CD Pipelines**: Continuous integration and continuous deployment pipelines are implemented for automated testing and deployment.
- **Docker Deployment**: The application is Dockerized to ensure easy and consistent deployment across environments.

## Design and Implementation

### Backend

- **Spring Boot**: The backend is built with Spring Boot to handle the business logic and API requests.
- **Microservice Architecture**: The backend is designed in a microservice manner to ensure scalability and maintainability.
- **H2 Database**: For simplicity and local storage, the H2 database is used to store vehicle data, which is populated via a scheduled task from the SWAPI API.
- **Scheduled Task**: The data for all entities is fetched every 24 hours from the SWAPI API using an asynchronous scheduled task. If offline mode is enabled, the data fetch is skipped.
- **Asynchronous Processing**: The `@Async` annotation is used to handle the fetching of data in an asynchronous manner, ensuring that it does not block the main thread of the application.
- **Exception Handling**: Proper exception handling is in place to ensure errors during the data fetching process are logged appropriately.
- **REST API**: The backend exposes REST endpoints that allow the frontend to query vehicle information by name.

### Frontend

- **React.js**: The frontend is built using React.js for rendering dynamic UI components.
- **Bootstrap**: Bootstrap is used for styling the frontend, providing a responsive and modern design.
- **Search UI**: The UI includes a type dropdown, a name input field, a search button, and a results display.

### Data Flow

1. **User Input**: The user selects a 'type' (e.g., Vehicle, Planet, Film, People, Spaceship, Species) and enters a name into the search field.
2. **Backend Request**: A request is sent to the backend, querying the vehicle or other entity based on the provided name and type.
3. **Data Retrieval**: The backend either fetches the data from the local H2 database or the SWAPI API (depending on the mode).
4. **Response**: The data is returned to the frontend and displayed to the user.

## Technologies Used

- **Backend**: Spring Boot, H2, RestTemplate , Java
- **Frontend**: React.js, Bootstrap
- **Asynchronous Processing**: @Async, @Scheduled
- **Database**: H2
- **Containerization**: Docker
- **CI/CD**: Jenkins
- **Version Control**: Git

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone <repository_url>
   cd <project_directory>
   ```

2. **Run the Backend**:
    - Navigate to the backend directory and run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Run the Frontend**:
    - Navigate to the frontend directory and install dependencies:
   ```bash
   npm install
   ```
    - Start the React app:
   ```bash
   npm start
   ```

4. **Docker Setup**:
    - Build and run the Docker container:
   ```bash
   docker-compose up --build
   ```
## Sequence diagram
![Sequence Diagram](SequenceDiagram.png)
