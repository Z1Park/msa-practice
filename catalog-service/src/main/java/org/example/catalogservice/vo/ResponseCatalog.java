package org.example.catalogservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ResponseCatalog {

	private String productId;
	private String productName;
	private Integer unitPrice;
	private Integer stock;
	private LocalDateTime createdAt;
}
