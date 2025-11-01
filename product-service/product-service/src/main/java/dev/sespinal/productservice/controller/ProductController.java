package dev.sespinal.productservice.controller;

import dev.sespinal.productservice.model.Product;
import dev.sespinal.productservice.repository.ProductRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductRepository repository;

  public ProductController(ProductRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public List<Product> list(@RequestParam(required = false) String name) {
    return (name == null || name.isBlank())
        ? repository.findAll()
        : repository.findByNameContainingIgnoreCase(name);
  }

  /*@GetMapping("/{id}")
  public ResponseEntity<ProductResponse> findById(@PathVariable Long id,
      @RequestParam(defaultValue = "false") boolean includeStock) {
    ProductResponse product = inMemoryStore.get(id);
    if (product == null) {
      return ResponseEntity.notFound().build();
    }
    if (!includeStock) {
      product = product.withStock(null);
    }
    return ResponseEntity.ok(product);
  }*/

  @GetMapping("/{id}")
  public Product getById(@PathVariable Long id) {
    return repository.findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
  }

 /* @GetMapping
  public ResponseEntity<Map<String, Object>> search(@RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Map<String, Object> filters = new HashMap<>();
    filters.put("page", page);
    filters.put("size", size);
    if (name != null && !name.isBlank()) {
      filters.put("name", name);
    }

    Map<String, Object> body = new HashMap<>();
    body.put("filters", filters);
    body.put("results", inMemoryStore.values());

    return ResponseEntity.ok(body);
  }*/

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Product create(@RequestBody Product request) {
    return repository.save(request);
  }

  @PutMapping("/{id}")
  public Product update(@PathVariable Long id, @RequestBody Product request) {
    Product current = repository.findById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    current.setName(request.getName());
    current.setPrice(request.getPrice());
    current.setDescription(request.getDescription());
    current.setStock(request.getStock());
    return repository.save(current);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
    }

    repository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
