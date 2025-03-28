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
public class CartService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    public String getCart(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));
        cart.calculateTotal();

        List<CartItem> cartItems = cart.getCartItems();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(cartItems);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error converting cart items to JSON: " + e.getMessage();
        }
    }

    private Cart createNewCart(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(new ArrayList<>());
        return newCart;
    }
    public String clearCart(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));
        cart.getCartItems().clear();
        cartRepository.save(cart);
        return "Cart cleared successfully";
    }

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

            // Save the changes to the database
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

//            return "cart";
        }

        else{
            throw new RuntimeException("Failed to add product to cart.");
        }
    }


}