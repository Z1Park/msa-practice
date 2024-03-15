package org.example.catalogservice.service;

import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.catalogservice.repository.CatalogEntity;
import org.example.catalogservice.repository.CatalogRepository;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

	private final CatalogRepository catalogRepository;


	@Override
	public List<CatalogEntity> getAllCatalogs() {
		return catalogRepository.findAll();
	}
}
