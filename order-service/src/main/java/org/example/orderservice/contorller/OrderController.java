package org.example.orderservice.contorller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.dto.OrderDto;
import org.example.orderservice.messagequeue.KafkaProducer;
import org.example.orderservice.messagequeue.OrderProducer;
import org.example.orderservice.repository.OrderEntity;
import org.example.orderservice.service.OrderService;
import org.example.orderservice.vo.RequestOrder;
import org.example.orderservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service")
public class OrderController {

	private final OrderService orderService;
	private final KafkaProducer kafkaProducer;
	private final OrderProducer orderProducer;
	private final ModelMapper modelMapper = new ModelMapper();


	@GetMapping("/health-check")
	public String status(HttpServletRequest request) {
		return "It's working in Order-service On Port " + request.getServerPort();
	}

	@GetMapping("/{userId}/orders")
	public List<ResponseOrder> getOrder(@PathVariable String userId) throws Exception {
		log.info("Before retrieve orders data");
		List<ResponseOrder> orders = orderService.getOrdersByUserId(userId).stream()
				.map(order -> modelMapper.map(order, ResponseOrder.class)).toList();
		log.info("After retrieve orders data");
		return orders;
	}

	@PostMapping("/{userId}/orders")
	public ResponseEntity<ResponseOrder> createOrder(@PathVariable String userId,
			@RequestBody RequestOrder orderDetails) {
		log.info("Before add orders data");
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		OrderDto orderDto = modelMapper.map(orderDetails, OrderDto.class);
		orderDto.setUserId(userId);

		/* jpa */
		orderService.createOrder(orderDto);

		/* kafka */
		orderDto.setOrderId(UUID.randomUUID().toString());
		orderDto.setTotalPrice(orderDetails.getQuantity() * orderDetails.getUnitPrice());

		/* send order to kafka */
		kafkaProducer.send("example-catalog-topic", orderDto);
//		orderProducer.send("orders", orderDto);

		ResponseOrder responseOrder = modelMapper.map(orderDto, ResponseOrder.class);
		log.info("After add orders data");
		return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
	}
}
