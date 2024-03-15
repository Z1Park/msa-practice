package org.example.orderservice.service;

import java.util.List;
import org.example.orderservice.dto.OrderDto;
import org.example.orderservice.repository.OrderEntity;

public interface OrderService {

	OrderDto createOrder(OrderDto orderDto);

	OrderDto getOrderByOrderId(String orderId);

	List<OrderEntity> getOrdersByUserId(String userId);
}
