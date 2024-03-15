package org.example.catalogservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.catalogservice.repository.CatalogEntity;
import org.example.catalogservice.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

	private final CatalogRepository catalogRepository;
	private final ObjectMapper objectMapper;

	@KafkaListener(topics = "example-catalog-topic")
	public void updateQuantity(String kafkaMessage) {
		log.info(("Kafka Message = {}"), kafkaMessage);

		Map<String, Object> map = new HashMap<>();
		try {
			map = objectMapper.readValue(kafkaMessage, new TypeReference<Map<String, Object>>() {});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		CatalogEntity catalog = catalogRepository.findByProductId((String) map.get("productId"));
		if (catalog != null) {
			catalog.setStock(catalog.getStock() - (Integer) map.get("quantity"));
			catalogRepository.save(catalog);
		}
	}
}
