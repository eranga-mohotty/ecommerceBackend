package com.emarket.emarket.services;

import com.emarket.emarket.entities.Cart;
import com.emarket.emarket.entities.CartItem;
import com.emarket.emarket.entities.Product;
import com.emarket.emarket.entities.User;
import com.emarket.emarket.repositories.CartRepository;
import com.emarket.emarket.repositories.ProductRepository;
import com.emarket.emarket.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;


    public String addProductToCart(String email, String password, long productId) {
        User user = userRepository.findByEmailAndPassword(email, password);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));

        Optional<Product> optProduct = productRepository.findById(productId);

        if (user != null && optProduct.isPresent()) {
            Product product = optProduct.get();
            int quantity = 1;

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);

            cart.addCartItem(cartItem);

            cartRepository.save(cart);
            cart.calculateTotal();
            List<CartItem> cartItems = cart.getCartItems();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(cartItems);
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // Print the exception details for debugging
                return "Error converting cart items to JSON: " + e.getMessage();
            }

        }

        else{
            throw new RuntimeException("Failed to add product to cart.");
        }
    }

    public String updateProductQuantity(String email, String password, long productId, int quantity) {
        User user = userRepository.findByEmailAndPassword(email, password);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));

        Optional<Product> optProduct = productRepository.findById(productId);

        if (user != null && optProduct.isPresent()) {
            Product product = optProduct.get();

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            // remove item from cart if quantity reaches 0
            if (quantity>0){
                cart.updateItemQuantity(cartItem,quantity);
            }
            else{
                cart.removeCartItem(cartItem);
            }


            // Save the changes to the database
            // REPETITIVE CODE
            cartRepository.save(cart);
            cart.calculateTotal();
            List<CartItem> cartItems = cart.getCartItems();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(cartItems);
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // Print the exception details for debugging
                return "Error converting cart items to JSON: " + e.getMessage();
            }
        }

        else{
            throw new RuntimeException("Failed to add product to cart.");
        }
    }


    private Cart createNewCart(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(new ArrayList<>());
        return newCart;
    }


}
