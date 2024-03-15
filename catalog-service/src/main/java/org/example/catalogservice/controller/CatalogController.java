package org.example.catalogservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.catalogservice.service.CatalogService;
import org.example.catalogservice.vo.ResponseCatalog;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/catalog-service")
public class CatalogController {

	private final CatalogService catalogService;


	@GetMapping("/health-check")
	public String status(HttpServletRequest request) {
		return "It's working in Catalog-service On Port " + request.getServerPort();
	}

	@GetMapping("/catalogs")
	public List<ResponseCatalog> getUsers() {
		ModelMapper modelMapper = new ModelMapper();

		return catalogService.getAllCatalogs().stream()
				.map(user -> modelMapper.map(user, ResponseCatalog.class)).toList();
	}
}
