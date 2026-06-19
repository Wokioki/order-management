package com.portfolio.ordermanagement.repository;

import com.portfolio.ordermanagement.entity.Order;
import com.portfolio.ordermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);
}