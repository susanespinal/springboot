package dev.sespinal.productservice.service;

import dev.sespinal.productservice.dto.ProductRequest;
import dev.sespinal.productservice.dto.ProductResponse;
import dev.sespinal.productservice.exception.ResourceNotFoundException;
import dev.sespinal.productservice.mapper.ProductMapper;
import dev.sespinal.productservice.model.Product;
import dev.sespinal.productservice.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository repository;

  public ProductService(ProductRepository repository) {
    this.repository = repository;
  }

  public List<ProductResponse> findAll(String name) {
    List<Product> products = (name == null || name.isBlank())
        ? repository.findAll()
        : repository.findByNameContainingIgnoreCase(name);
    return products.stream()
        .map(ProductMapper::toResponse)
        .toList();
  }

  public ProductResponse findById(Long id) {
    Product product = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Producto " + id + " no encontrado"));
    return ProductMapper.toResponse(product);
  }

  @Transactional
  public ProductResponse create(ProductRequest request) {
    Product product = new Product();
    Product saved = repository.save(ProductMapper.toEntity(request, product));
    return ProductMapper.toResponse(saved);
  }

  @Transactional
  public ProductResponse update(Long id, ProductRequest request) {
    Product product = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Producto " + id + " no encontrado"));
    Product updated = repository.save(ProductMapper.toEntity(request, product));
    return ProductMapper.toResponse(ProductMapper.toEntity(request,updated));
  }

  @Transactional
  public void delete(Long id) {
    if(repository.existsById(id)){
      throw new ResourceNotFoundException("Producto " + id + " no encontrado");
    }
    repository.deleteById(id);
  }
}
