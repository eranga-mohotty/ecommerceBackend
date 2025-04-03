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


    private User user;

    public User getUser(String email,String password) {
        try {
            user = userRepository.findByEmailAndPassword(email, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return user;
    }


//TODO: Separate Command and query
    public String addProductToCart(String email, String password, long productId) {
        User user = userRepository.findByEmailAndPassword(email, password);
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));

        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (user != null && optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            int quantity = 1;

            CartItem cartItem = createCartItemFromProduct(product, quantity);

            cart.addCartItem(cartItem);

            List<CartItem> cartItems = getUpdatedCartItems(cart);

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(cartItems);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
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

        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (user != null && optionalProduct.isPresent()) {

            CartItem cartItem = createCartItemFromProduct(optionalProduct.get(), quantity);

            if (quantity>0){
                cart.updateItemQuantity(cartItem,quantity);
            }
            else{
                cart.removeCartItem(cartItem);
            }


            List<CartItem> cartItems = getUpdatedCartItems(cart);

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(cartItems);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "Error converting cart items to JSON: " + e.getMessage();
            }
        }

        else{
            throw new RuntimeException("Failed to add product to cart.");
        }
    }
    private static CartItem createCartItemFromProduct(Product product, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        return cartItem;
    }

    private List<CartItem> getUpdatedCartItems(Cart cart) {
        cartRepository.save(cart);
        cart.calculateTotal();
        return cart.getCartItems();
    }

    private Cart createNewCart(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(new ArrayList<>());
        return newCart;
    }


}
