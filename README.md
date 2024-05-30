# Example SpringBoot AI


## Requirements.
- Java 17

## Technical principles
- Spring Boot 3.3.0


## How to start the project
Sorry but an API KEY is needed!

1. Build project ```mvn clean package -DwithHistory test-compile org.pitest:pitest-maven:mutationCoverage```
2. Execute locally. ``` mvn clean spring-boot:run -Dspring-boot.run.profiles=local ```.

There is a postman collection ir order to test queries and mutation which could be imported (is under the folder postman).

The api manage products with reviews
