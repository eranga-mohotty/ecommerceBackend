package com.emarket.emarket.repositories;

import com.emarket.emarket.entities.Cart;
import com.emarket.emarket.entities.OrderHeader;
import com.emarket.emarket.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Long> {
    Optional<List<OrderHeader>> findByUserOrderByDateTimeDesc(User user);
}