package com.emarket.emarket.controllers;

import com.emarket.emarket.services.AuthService;
import com.emarket.emarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    AuthService AuthService;

    @PostMapping("/login")
    public ResponseEntity<String> findData(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Boolean isAuthorized = AuthService.EmailPasswordMatch(email, password);
        HttpStatus httpStatus;
        String responseBody = "";

        if (isAuthorized){
            httpStatus = HttpStatus.OK;
        }
        else {
            httpStatus = HttpStatus.UNAUTHORIZED;
            responseBody = "Incorrect email or password provided";
        }

        return new ResponseEntity<>(responseBody, httpStatus);
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");

        String response = userService.registerUser(name, email, password);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<String> getUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Boolean isAuthorized = AuthService.EmailPasswordMatch(email, password);

        if (isAuthorized){
            String jsonSerializedUser = String.valueOf(userService.getJsonSerializedUser(email,password));
            return new ResponseEntity<>(jsonSerializedUser, HttpStatus.OK);
        }

        return new ResponseEntity<>("Incorrect email or password provided",HttpStatus.UNAUTHORIZED);
    }


}
