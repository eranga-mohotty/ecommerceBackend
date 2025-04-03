package com.emarket.emarket.controllers;

import com.emarket.emarket.services.AuthService;
import com.emarket.emarket.services.CartItemService;
import com.emarket.emarket.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")

public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private CartItemService cartItemService;

    @Autowired
    AuthService authService;

    @PostMapping("/cart")
    public ResponseEntity<String> serializedCartResponse(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");
        Boolean isAuthorized = authService.EmailPasswordMatch(email, password);
        if (isAuthorized){
            String response = String.valueOf(cartService.getCart(email,password));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Incorrect email or password provided", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");

        Boolean isAuthorized = authService.EmailPasswordMatch(email, password);
        if (isAuthorized){
            long product_id = Long.parseLong(request.get("product_id"));
            String response = cartItemService.addProductToCart(email,password,product_id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Incorrect email or password provided", HttpStatus.UNAUTHORIZED);
        }

    }

    @PostMapping("/cart/update")
    public ResponseEntity<String> updateItemQuantity(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        int quantity = Integer.parseInt(request.get("quantity"));

        Boolean isAuthorized = authService.EmailPasswordMatch(email, password);
        if (isAuthorized){
            long product_id = Long.parseLong(request.get("product_id"));
            String response = cartItemService.updateProductQuantity(email,password,product_id,quantity);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Incorrect email or password provided", HttpStatus.UNAUTHORIZED);

        }

    }

    @PostMapping("/cart/remove")
    public ResponseEntity<String> removeProduct(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Boolean isAuthorized = authService.EmailPasswordMatch(email, password);
        if (! isAuthorized){
            return new ResponseEntity<>("Incorrect email or password provided", HttpStatus.UNAUTHORIZED);
        }

        long product_id = Long.parseLong(request.get("product_id"));

        String response = cartItemService.updateProductQuantity(email,password,product_id,0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cart/clear")
    public ResponseEntity<String> clearCart(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Boolean isAuthorized = authService.EmailPasswordMatch(email, password);
        if (isAuthorized){
            String response = String.valueOf(cartService.clearCart(email,password));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>("Incorrect email or password provided", HttpStatus.UNAUTHORIZED);
    }
}