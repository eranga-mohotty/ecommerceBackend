package com.emarket.emarket.controllers;
import com.emarket.emarket.entities.Product;
import com.emarket.emarket.model.ServiceLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private ServiceLayer serviceLayer;

    @PostMapping("/order")
    public ResponseEntity<String> saveCartToOrder(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        String response = serviceLayer.EmailPasswordMatch(email, password);
        if (Objects.equals(response, "Incorrect email or password provided")){
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        serviceLayer.saveCartToOrder(email,password);
        return new ResponseEntity<>("Order saved", HttpStatus.OK);
    }

    @PostMapping("/attemptCharge")
    public ResponseEntity<String> attemptCharge(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String nonce = request.get("nonce");

        String response = serviceLayer.EmailPasswordMatch(email, password);
        if (Objects.equals(response, "Incorrect email or password provided")){
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String handleAttemptChargeResult = serviceLayer.handleAttemptCharge(email,password,nonce);
        if (handleAttemptChargeResult.equals("Payment was Successful.")){
            return new ResponseEntity<>(handleAttemptChargeResult, HttpStatus.OK);

        }
        else{

            return new ResponseEntity<>(handleAttemptChargeResult, HttpStatus.BAD_REQUEST);
        }
    }

}
