package com.emarket.emarket.controllers;

import com.emarket.emarket.services.AuthService;
import com.emarket.emarket.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

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

        String response = AuthService.EmailPasswordMatch(email, password);
        HttpStatus httpStatus = HttpStatus.OK;

        if (Objects.equals(response, "Incorrect email or password provided")){
            httpStatus = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(response, httpStatus);
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

        String response = AuthService.EmailPasswordMatch(email, password);
        if (Objects.equals(response, "Incorrect email or password provided")){
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response = String.valueOf(userService.getUser(email,password));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
