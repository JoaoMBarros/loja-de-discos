# joao-pedro-monteiro-backend
Projeto Backend - João Pedro Monteiro da Silva Barros

# Vinyl Record E-Commerce Loyalty Program Challenge

## Challenge Description

An e-commerce platform specializing in vinyl records has decided to implement a loyalty program based on points to increase sales volume and attract new customers. After several meetings, the sales team has defined a points table based on days of the week:

| Day       | Sunday | Monday | Tuesday | Wednesday | Thursday | Friday | Saturday |
|-----------|--------|--------|---------|-----------|----------|--------|----------|
| Points    | 25     | 7      | 6       | 2         | 10       | 15     | 20       |


## How to Run

1. Clone this repository:

    ```bash
    git clone https://github.com/bc-fullstack-04/joao-pedro-monteiro-backend
    ```

2. Install dependencies through Maven.

3. Build JAR files.

4. Run the project with Docker Compose:

    ```bash
    docker-compose -f ./docker-compose.yml up
    ```

### Ports

- `8080`: User API
- `8082`: Integration API
- `5432`: PostgreSQL
- `15672`: RabbitMQ Management
- `5672` : RabbitMQ

## Notes

Ensure that you have Docker and Docker Compose installed on your system before running the project.

## API Documentation

For more information about the API, please refer to the Swagger documentation after running the project.:

### User API

`http://localhost:8080/api/swagger-ui/index.html#`

### Integration API

`http://localhost:8082/api/swagger-ui/index.html#/`