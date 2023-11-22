//package com.emarket.emarket.model;
//import com.emarket.emarket.entities.Cart;
//import com.emarket.emarket.entities.User;
//import com.emarket.emarket.repositories.UserRepository;
//import com.emarket.emarket.repositories.CartRepository;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.util.Optional;
//
//@Service
//public class CartService {
//
//    @Autowired
//    private final UserRepository userRepository;
//    @Autowired
//    private final CartRepository cartRepository;
//
//    public CartService(UserRepository userRepository, CartRepository cartRepository) {
//        this.userRepository = userRepository;
//        this.cartRepository = cartRepository;
//    }
//
//    public Optional<Cart> getCartForUser(String email, String password) {
//        // Step 1: Retrieve user
//        Optional<User> userOptional = userRepository.findByEmailAndPassword(email, password);
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//
//            // Step 2: Retrieve cart for the user
//            return cartRepository.findByUser(user);
//        } else {
//            // Handle case where user is not found (authentication failure)
////            throw new AuthenticationException("Incorrect email or password provided");
//            return Optional.empty();
//        }
//    }
//}
