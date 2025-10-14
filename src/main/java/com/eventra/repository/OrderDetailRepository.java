package com.eventra.repository;

import com.eventra.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import com.eventra.model.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {
    List<OrderDetail> findByOrder(Order order);
}
