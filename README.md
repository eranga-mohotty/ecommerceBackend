### Ecommerce backend is a rest API built as a template that follows common e-commerce application features suitable for customer facing applications

## Features/Endpoints
- Auth (register and login)
- Product listing and product details
- User details
- Cart features
- Place Order
- Square payment integration

### Tech stack
- Language: Java [20.0.1](https://www.oracle.com/java/technologies/javase/jdk20-archive-downloads.html)
- Framework: Spring Boot [3.1.4](https://docs.spring.io/spring-boot/docs/3.1.4/reference/html/)
- Build Tools: [Apache Maven 3.9.4](https://maven.apache.org/docs/3.9.4/release-notes.html)
- Database: MySql
- Refer pom.xml for dependancies

### Prerequisites
- JDK 20 or later
- Maven 3.6.3 or later
⚠️Ensure JDK/Maven/SpringBoot versions are compatible if different versions are used and update pom.xml

### How to Build and Run App
0. Clone repo or download as zip and extract
1. Integrate Square payment gateway
    - Follow instructins at [Square Developer docs](https://developer.squareup.com/docs/get-started/create-account-and-application) to signup and create new application
    - Change API version to 2023-12-13
    - Create a .env file at root of project and add below line with you Access token from your Square App
    - SQUARE_API_KEY="enter your Access token here"
2. Build and Run
    -     mvn clean install && mvn spring-boot:run  
  
TODO:
1. Code cleanup
2. More cleanup...
3. Switch to RESTful 
4. Upgrade psw hashing method to (SHA256 + Salt or Pepper)
5. Use JWT for auth
6. Add functionality needed for an Admin Panel
