package com.emarket.emarket.services;

import com.emarket.emarket.entities.*;
import com.emarket.emarket.repositories.CartRepository;
import com.emarket.emarket.repositories.OrderHeaderRepository;
import com.emarket.emarket.repositories.OrderLineRepository;
import com.emarket.emarket.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class OrderService {
    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderHeaderRepository orderHeaderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;

    public String saveCartToOrder(String email, String password) {
        String cartString = cartService.getCart(email, password);
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

}
