package org.example.orderservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

	OrderEntity findByOrderId(String orderId);

	List<OrderEntity> findByUserId(String userId);
}
