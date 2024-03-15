package org.example.catalogservice.service;

import java.util.List;
import org.example.catalogservice.repository.CatalogEntity;

public interface CatalogService {

	List<CatalogEntity> getAllCatalogs();
}
