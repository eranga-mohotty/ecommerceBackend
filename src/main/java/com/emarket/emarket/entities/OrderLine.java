package com.emarket.emarket.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "order_line")
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_line_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_header_id")
    private OrderHeader orderHeader;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int item_amount;

    private int quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderHeader getOrderHeader() {
        return orderHeader;
    }

    public void setOrderHeader(OrderHeader orderHeader) {
        this.orderHeader = orderHeader;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getItem_amount() {
        return item_amount;
    }

    public void setItem_amount(int item_amount) {
        this.item_amount = item_amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
