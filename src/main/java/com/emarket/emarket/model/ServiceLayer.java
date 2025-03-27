package com.emarket.emarket.model;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.emarket.emarket.entities.*;
import com.emarket.emarket.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.DataInput;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.SubmissionPublisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


@Service
public class ServiceLayer {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderHeaderRepository orderHeaderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;

    @Value("${env.SQUARE_API_KEY}")
    private String SQUARE_ACCESS_TOKEN;

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

    public String getUser(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Print the exception details for debugging
            return "Error converting User to JSON: " + e.getMessage();
        }
    }

    public String saveCartToOrder(String email, String password) {
        String cartString = getCart(email, password);
        if (Objects.equals(cartString, "Cart not present")){
            return cartString;

        }
//        if cart isn't empty
        // copy items in cart table to order tables (orderHeader and order Line)
        // clear cart to avoid reentering same cart again and again
        else{
            User user = userRepository.findByEmailAndPassword(email, password);
            Cart cart = cartRepository.findByUser(user).get();
            OrderHeader orderHeader = new OrderHeader();
            orderHeader.setUser(user);
            orderHeader.setPaid(Boolean.FALSE);
            cart.calculateTotal();
            orderHeader.setTotal(cart.getTotal());
            orderHeader.setDateTime(LocalDateTime.now());
            orderHeaderRepository.save(orderHeader);

            for (CartItem cartItem: cart.getCartItems()){
                OrderLine orderLine = new OrderLine();
                orderLine.setOrderHeader(orderHeader);
                orderLine.setProduct(cartItem.getProduct());
                orderLine.setItem_amount(cartItem.getProduct().getPrice());
                orderLine.setQuantity(cartItem.getQuantity());
                orderLineRepository.save(orderLine);

            }
            cart.getCartItems().clear();
            cartRepository.save(cart);
            return "successfully copied to order header and emptied cart";



        }


    }

    public String handleAttemptCharge(String email, String password, String nonce){

        final String SQUARE_API_URL = "https://connect.squareupsandbox.com/v2/payments";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + SQUARE_ACCESS_TOKEN);

        // Create the request body
        String idempotencyKey = UUID.randomUUID().toString(); //replace with orderHeaderID
        String requestJson = String.format("{\"idempotency_key\": \"%s\", \"source_id\": \"%s\", \"amount_money\": {\"amount\": %s, \"currency\": \"AUD\"}}",
                idempotencyKey, nonce, 1); //Replace price with actual price from order Header
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        // Make the POST request
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(SQUARE_API_URL, requestEntity, String.class);


        System.out.println("RESPONSE BODY = \n\n\n"+responseEntity.getBody());

        // Handle the response
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            // Payment successful, you can process the responseEntity.getBody()

            User user = userRepository.findByEmailAndPassword(email, password);
            Optional<List<OrderHeader>> orderHeaders = orderHeaderRepository.findByUserOrderByDateTimeDesc(user);
            OrderHeader orderHeader = orderHeaders.get().get(0);
            orderHeader.setPaid(true);
            orderHeaderRepository.save(orderHeader);

            return "Payment was Successful.";


        } else {
            // Payment failed, handle the error
            return "Payment failed.";
        }

    }

}