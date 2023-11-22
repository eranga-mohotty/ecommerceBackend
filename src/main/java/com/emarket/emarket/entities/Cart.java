package com.emarket.emarket.entities;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    @Transient
    private Integer total;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
    public void addCartItem(CartItem cartItem) {
        if (! isCartItemInCart(cartItem)){
            cartItem.setCart(this);
            this.cartItems.add(cartItem);
        }
        // if item already in cart increase qty +1
        else {
            for (CartItem i : this.cartItems){
                if (cartItem.getProduct() == i.getProduct()){
                    i.setQuantity(i.getQuantity()+1);
                }

            }

        }

    }
    public void updateItemQuantity(CartItem cartItem, int quantity) {
        for (CartItem i : this.cartItems){
            if (cartItem.getProduct() == i.getProduct()){
                i.setQuantity(quantity);
            }

        }

    }

    public void removeCartItem(CartItem cartItem) {
        for (int i = 0; i < this.cartItems.size(); i++) {
            if (cartItems.get(i).getProduct()==cartItem.getProduct()){
                cartItems.remove(i);
            }
        }
    }


    private boolean isCartItemInCart(CartItem cartItem){
        for (CartItem i : this.cartItems){
            if (cartItem.getProduct() == i.getProduct()){
                return true;
            }

        }
        return false;
    }

    public void calculateTotal() {
        this.total = cartItems.stream()
                .mapToInt(cartItem -> cartItem.getProduct().getPrice() * cartItem.getQuantity())
                .sum();
    }
}
