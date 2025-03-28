package com.emarket.emarket.controllers;
import com.emarket.emarket.services.AuthService;
import com.emarket.emarket.services.CartItemService;
import com.emarket.emarket.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

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
    public ResponseEntity<String> findData(@RequestBody Map<String, String> request) {
        //check if uname psw matches
        String email = request.get("email");
        String password = request.get("password");
        String response = authService.EmailPasswordMatch(email, password);
        if (Objects.equals(response, "Incorrect email or password provided")){
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        // , cart exists, and cart not empty
        response = String.valueOf(cartService.getCart(email,password));
        return new ResponseEntity<>(response, HttpStatus.OK);
//        return new ResponseEntity<>("cart", HttpStatus.OK);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(@RequestBody Map<String, String> request) {
        //check if uname psw matches
        String email = request.get("email");
        String password = request.get("password");

        String response = authService.EmailPasswordMatch(email, password);
        if (Objects.equals(response, "Incorrect email or password provided")){
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        long product_id = Long.parseLong(request.get("product_id"));
        // add product to cart of current user
        response = String.valueOf(cartItemService.addProductToCart(email,password,product_id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cart/update")
    public ResponseEntity<String> updateItemQuantity(@RequestBody Map<String, String> request) {
        //check if uname psw matches
        String email = request.get("email");
        String password = request.get("password");
        int quantity = Integer.parseInt(request.get("quantity"));

        String response = authService.EmailPasswordMatch(email, password);
        if (Objects.equals(response, "Incorrect email or password provided")){
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        long product_id = Long.parseLong(request.get("product_id"));
        // add product to cart of current user
        response = String.valueOf(cartItemService.updateProductQuantity(email,password,product_id,quantity));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cart/remove")
    public ResponseEntity<String> removeProduct(@RequestBody Map<String, String> request) {
        //check if uname psw matches
        String email = request.get("email");
        String password = request.get("password");

        String response = authService.EmailPasswordMatch(email, password);
        if (Objects.equals(response, "Incorrect email or password provided")){
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        long product_id = Long.parseLong(request.get("product_id"));
        // add product to cart of current user
        response = String.valueOf(cartItemService.updateProductQuantity(email,password,product_id,0));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cart/clear")
    public ResponseEntity<String> clearCart(@RequestBody Map<String, String> request) {
        //check if uname psw matches
        String email = request.get("email");
        String password = request.get("password");

        String response = authService.EmailPasswordMatch(email, password);
        if (Objects.equals(response, "Incorrect email or password provided")){
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response = String.valueOf(cartService.clearCart(email,password));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}