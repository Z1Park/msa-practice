package org.example.orderservice.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.OrderDto;
import org.example.orderservice.repository.OrderEntity;
import org.example.orderservice.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final ModelMapper modelMapper = new ModelMapper();


	@Override
	public OrderDto createOrder(OrderDto orderDto) {
		orderDto.setOrderId(UUID.randomUUID().toString());
		orderDto.setTotalPrice(orderDto.getUnitPrice() * orderDto.getQuantity());

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		OrderEntity orderEntity = modelMapper.map(orderDto, OrderEntity.class);
		System.out.println("orderEntity = " + orderEntity);

		orderRepository.save(orderEntity);
		return modelMapper.map(orderEntity, OrderDto.class);
	}

	@Override
	public OrderDto getOrderByOrderId(String orderId) {
		OrderEntity orderEntity = orderRepository.findByOrderId(orderId);
		return modelMapper.map(orderEntity, OrderDto.class);
	}

	@Override
	public List<OrderEntity> getOrdersByUserId(String userId) {
		return orderRepository.findByUserId(userId);
	}
}
