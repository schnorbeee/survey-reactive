# Getting Started

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Docker Compose support

This project contains a Docker Compose file named `docker-compose.yml`.
In this file, the following services have been defined:

* db1: [`postgres:16, port: 5432`]
* pgadmin1: [`dpage/pgadmin4, port: 5050`]
* backend1: [`main app use Dockerfile, port: 8080`]

Please review the tags of the used images and set them to the same as you're running in production.

### Running and Testing application support

You can run application with a single command, into the root of project:

### `docker-compose up --build`

Application will running under this link:

http://localhost:8080/

You don't need `mvn clean install` or `mvn clean install -DskipTests` command to prebuild application, 
but if you want to run test cases, then you have 2 options:

1. You simple run `mvn clean install` into the root of project. In this case all tests will run after the build.
2. You can run separate run once by once test files with Intellj. You can find files here: 
        ../src/test/java/com/dynata/survayhw/controllers/*

In resources folder I put your sended .csv files, and the test cases uses this files as well.

    ../src/test/resources/testfiles/*.csv

I create into root postman folder, where you can find a postman collection, 
and you can test with postman application after you imported into Postman app,
and you change to correcting the POST requests file path with yours.

    ../survay-hw/postman/survey.postman_collection.json

!!! I had one big issues with the name of .csv files. 
Spring application in POST requests can't handle MultipartFile, if filenames had separator character like 'space'. 
So if you want to use your files into postman collection, then take care about this information and remove all separator character into the filenames.

If you want to check the database actual state while running app with docker-compose command, 
then you can use link bellow. You need enter just once root password "admin" and after this, also once database password "survey_password".

http://localhost:5050/

I hope it will be fine.