# image-object-detector

This is a simple REST endpoint that provides API for adding and retrieving images and their metadata.

1. Java 17+
1. Spring Boot 3.x

# Building

```
mvn clean install
```

# Running

# Developing

## Prerequisites

1. Docker
   On Mac, you can download [Docker desktop](https://docs.docker.com/desktop/release-notes)

### Database

This application leverages a database. I use [postgres](https://www.postgresql.org/docs/16/index.html) but technically
any RDBMS with lob support should work.

## Set up

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

