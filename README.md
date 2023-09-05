# AWS IP Range Filter

This Spring Boot application allows you to retrieve and filter AWS IP ranges based on selected regions. You can use this application to create IP filters for security groups in your AWS environment.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Usage](#usage)
- [Building and Running with Docker](#building-and-running-with-docker)
- [License](#license)
- [Contributing](#contributing)

## Prerequisites

Before running the application, make sure you have the following prerequisites installed:

- [Java 11 or higher](https://adoptopenjdk.net/)
- [Apache Maven](https://maven.apache.org/) (for building the project)
- [Docker](https://www.docker.com/get-started) (optional, for Dockerization)

## Getting Started

1. Clone the repository:

   https://github.com/akhtarnadra/aws-ip-range-service.git
   
## Build the project:
cd aws-ip-range-service
mvn clean package

## Run the application:
java -jar target/aws-ip-range-service-0.0.1-SNAPSHOT.jar

## Configuration

### `application.properties`

- `server.port` (default is 8080): The server port for the application.
- `app.cache.expire-minutes` (default is 60 minutes): Cache expiration time in minutes.

## Usage

### Retrieve AWS IP Ranges

To retrieve AWS IP ranges, make a GET request to the `/aws-region-ip-ranges` endpoint. You can filter the ranges by region using the `region` query parameter. Valid region values are `EU`, `US`, `AP`, `CN`, `SA`, `AF`, `CA`, or `ALL` (to retrieve all regions).
Example request:

# curl -X GET http://localhost:8080/aws-ip-range-service-service/api/aws-ip-range/v1/aws-region-ip-ranges?region=EU
Response
# The application will respond with a plain text list of IP prefixes:
3.2.34.0/26
3.5.140.0/22
...
If no data is available for the specified region, the response will be empty.

## Building and Running with Docker
You can also run the application inside a Docker container. Make sure you have Docker installed and follow these steps:

# Build a Docker image:

# docker build -t aws-ip-range-filter .
Run the Docker container:

# docker run -p 8080:8080 aws-ip-range-filter
The application will be accessible at http://localhost:8080 inside the Docker container.