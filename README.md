# Bookmark It (vanilla Spring version)

Here I am building the backend for a bookmarks keeping service. This 
service users will be able to save and manage links to anything they 
like on the internet: sites, images, video. The backend is implemented 
using vanilla Spring Framework, therefore, developers may compare it to 
services built on Spring Boot. Based on my student project at Yandex 
Practicum.

## Table of Contents

1. [About service](#about-service)
2. [API Specification](#api-specification)
3. [System Requirements](#system-requirements) 
4. [Building Project](#building-project)
5. [Testing with Postman](#testing-with-postman)
6. [Running with docker-compose](#running-with-docker-compose)
7. [What's Next](#whats-next)
8. [Contacts](#contacts)
9. [License](#license)

## About Service

If it sometimes happens that you need to save a some idea, a link to 
a post, a picture while browsing the internet, this service will help 
you to do so. Registered users may save links to different media, they 
meet: articles, posts, images, video. Also, users may add their own 
tags to these links and then filter links by tags.

Now the backend supports managing of users: registering, listing, 
deactivating, deleting. Support for operations with links will follow.  

Stack used:
- Java 21
- Spring Framework
- Hibernate
- PostgreSQL
- REST API
- Docker
- Postman
- Maven

## API Specification

OpenAPI specification for the backend is available in 
[bookmark-it-api-spec.json](bookmark-it-api-spec.json).

## System Requirements

To build and run the project you need:
- JDK (version 21 or later)
- Docker (28.0.4 or later)

## Building project

To build the project:
```bash
./mvnw clean package
```

To run tests:
```bash
./mvnw verify -P check,coverage
```

To build a Docker image for the backend:
```bash
./mvnw clean package && docker build . -t bookmark-it
```

## Testing with Postman

Project also contains API tests for [Postman](https://www.postman.com/) 
in [postman/bookmark-it-backend.json](postman/bookmark-it-backend.json).

## Running with docker-compose

To run the [Docker image](#building-project) using `docker-compose`:
```bash
docker-compose up -d 
```
The backend will be available at port `8080`.

To stop:
```bash
docker-compose down
```

## What's Next

The next step is to add support for operations with links and tags.

## Contacts

Have you any ideas or comments to share, contact me via email
[akuniutka@gmail.com](mailto:akuniutka@gmail.com), Telegram
[@akuniutka](https://t.me/akuniutka), or just leave an 
[issue](https://github.com/akuniutka/bookmark-it-vanilla-spring/issues).

## License

This project is released under version 2.0 of 
[Apache License](https://www.apache.org/licenses/LICENSE-2.0)
