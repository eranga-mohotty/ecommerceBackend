package com.emarket.emarket.controllers;

import com.emarket.emarket.model.ServiceLayer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private ServiceLayer serviceLayer;

    @PostMapping("/login")
    public ResponseEntity<String> findData(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        String response = serviceLayer.EmailPasswordMatch(email, password);
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

        String response = serviceLayer.registerUser(name, email, password);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<String> getUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        String response = serviceLayer.EmailPasswordMatch(email, password);
        if (Objects.equals(response, "Incorrect email or password provided")){
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response = String.valueOf(serviceLayer.getUser(email,password));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
