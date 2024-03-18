# image-object-detector

This is a simple REST endpoint that provides API for adding and retrieving images and their metadata.

1. Java 17+
1. Spring Boot 3.x

# Building

```
mvn clean install
```

# Running

## Prerequisites

This application leverages a database. I use [postgres](https://www.postgresql.org/docs/16/index.html) but technically
any RDBMS should work. I also opt for using a docker container to run the database. If you have an external database
to connect to, simply edit the `spring.datasource.url` in the `application.properties` file under `src/main/resources`

The following are the steps to set up running postgres docker container.

### Docker

1. Download Docker desktop.
   On Mac, you can download [Docker desktop](https://docs.docker.com/desktop/release-notes)

### Database

1. Download postgres docker image
   ```
   docker pull postgres
   ```

1. Run it
   ```
   docker run -itd --name postgresdb -e POSTGRES_PASSWORD=<enterAdminPasswordHere> \
          -p 5432:5432 -v /opt/local/data/postgres:/var/lib/postgres/data postgres
   ```
   The `<enterAdminPasswordHere>` can be any string you choose for the postgres admin password.
   The `/opt/local/data/postgres` should be a directory on the host machine that can store postgres data.

1. Go inside the container
   ```
   docker ps
   docker exec -it <containerIdFromAbove> bash
   ```

1. Initialize the database and user
   ```
   psql -U postgres
   CREATE USER appuser WITH PASSWORD 't0pS3cr3t'
   CREATE DATABASE imagedb OWNER appuser
   ```
   You can replace the password with any string, as long as it matches with what is in the `application.properties`
   file under `src/main/resources` directory.

### Imagga API

This application uses [Imagga API](https://imagga.com) for object detection. In order to make this application works,
you will need a working API key and secret. Get a free API key at  [Imagga API](https://imagga.com).

Then add the api key and secret to the file `application-dev.properties` under `src/main/resources`

You can then run the application with profile `dev`

## Running from command line
To run the application, you can run from IDE or command line:
```
mvn spring-boot:run
```

To run the application with a specific profile, for example: profile `dev`, run:
```
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Swagger UI
To access the Swagger API UI, open browser and type in this address:
```
https://localhost:8080/swagger-ui.html
```

