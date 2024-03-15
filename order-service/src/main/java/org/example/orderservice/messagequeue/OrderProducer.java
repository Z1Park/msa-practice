package org.example.orderservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.dto.Field;
import org.example.orderservice.dto.KafkaOrderDto;
import org.example.orderservice.dto.OrderDto;
import org.example.orderservice.dto.Payload;
import org.example.orderservice.dto.Schema;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final List<Field> fields = List.of(
			new Field("string", true, "order_id"),
			new Field("string", true, "user_id"),
			new Field("string", true, "product_id"),
			new Field("int32", true, "quantity"),
			new Field("int32", true, "unit_price"),
			new Field("int32", true, "total_price"));
	private final Schema schema = Schema.builder()
			.type("struct")
			.fields(fields)
			.optional(false)
			.name("orders")
			.build();


	public OrderDto send(String topic, OrderDto orderDto) {
		Payload payload = Payload.builder()
				.order_id(orderDto.getOrderId())
				.user_id(orderDto.getUserId())
				.product_id(orderDto.getProductId())
				.quantity(orderDto.getQuantity())
				.unit_price(orderDto.getUnitPrice())
				.total_price(orderDto.getTotalPrice())
				.build();
		KafkaOrderDto kafkaOrderDto = new KafkaOrderDto(schema, payload);

		String jsonInString = "";
		try {
			jsonInString = objectMapper.writeValueAsString(kafkaOrderDto);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		kafkaTemplate.send(topic, jsonInString);
		log.info("Order Producer sent data from the Order microservice: {}", kafkaOrderDto);

		return orderDto;
	}
}
