package com.emarket.emarket.model;

import com.emarket.emarket.entities.Cart;
import com.emarket.emarket.entities.CartItem;
import com.emarket.emarket.entities.Product;
import com.emarket.emarket.repositories.CartRepository;
import com.emarket.emarket.repositories.ProductRepository;
import com.emarket.emarket.repositories.UserRepository;
import com.emarket.emarket.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class ServiceLayer {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;

    public String EmailPasswordMatch(String email, String password){
        Optional<User> result = Optional.ofNullable(userRepository.findByEmailAndPassword(email, password));

        if (result.isPresent()){
            return "email psw found";
        } else {
            return "Incorrect email or password provided";
        }
    }

    public String registerUser(String name, String email, String password) {
        // You can add validation or other registration logic here
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        userRepository.save(user);

        return "Registration successful";
    }


    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProduct(long id) {
        Optional<Product> product =  productRepository.findById(id);
        return product;
    }

    // CART related methods start here
    public String getCart(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));
        cart.calculateTotal();

        if (cart != null) {
            List<CartItem> cartItems = cart.getCartItems();

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(cartItems);
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // Print the exception details for debugging
                return "Error converting cart items to JSON: " + e.getMessage();
            }
        } else {
            return "Cart not present"; // Handle the case when the cart is not present
        }
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
        newCart.setCartItems(new ArrayList<>()); // initialize the cart items list
        return newCart;
    }
    public String clearCart(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));
        // Clear the cart
        cart.getCartItems().clear();
        cartRepository.save(cart);
        return "Cart cleared successfully";
    }

}