package com.emarket.emarket.controllers;
import com.emarket.emarket.services.AuthService;
import com.emarket.emarket.services.OrderService;
import com.emarket.emarket.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    AuthService authService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<String> saveCartToOrder(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Boolean isAuthorized = authService.EmailPasswordMatch(email, password);
        if (isAuthorized){
            orderService.saveCartToOrder(email,password);
            return new ResponseEntity<>("Order saved", HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Incorrect email or password provided", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/attemptCharge")
    public ResponseEntity<String> attemptCharge(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String nonce = request.get("nonce");

        Boolean isAuthorized = authService.EmailPasswordMatch(email, password);
        if (isAuthorized){
            String handleAttemptChargeResult = paymentService.handleAttemptCharge(email,password,nonce);
            if (handleAttemptChargeResult.equals("Payment was Successful.")){
                return new ResponseEntity<>(handleAttemptChargeResult, HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(handleAttemptChargeResult, HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity<>("Incorrect email or password provided", HttpStatus.UNAUTHORIZED);

        }

    }

}
