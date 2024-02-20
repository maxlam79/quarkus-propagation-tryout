# Quarkus Reactive Keycloak Admin Client Issue Demo

This is a small repository demonstrating a puzzling issue with Keycloak Reactive Admin Client.

** Note: the keycloak settings are to an external keycloak demo server, configuring the keycloak dev services is a pain.

## How to run
Take a look at the application.properties file. The DB will run on Quarkus's dev service.

To run, just type:
```
./mvnw clean quarkus:dev
``` 
It should be running on 8080.

## End Points:
End Points to demonstrate the issues are:
- GET - http://localhost:8080/resource/p02
- GET - http://localhost:8080/resource/p03
- POST - http://localhost:8080/resource/p04
- POST - http://localhost:8080/resource/p05