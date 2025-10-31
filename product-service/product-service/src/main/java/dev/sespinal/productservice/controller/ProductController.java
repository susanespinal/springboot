package dev.sespinal.productservice.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final Map<Long, ProductResponse> inMemoryStore = new ConcurrentHashMap<>();
  private final AtomicLong sequence = new AtomicLong(1);


  @GetMapping("/{id}")
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
  }

  @GetMapping
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
  }

  @PostMapping
  public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
    long id = sequence.getAndIncrement();
    ProductResponse created = ProductResponse.fromRequest(id, request);
    inMemoryStore.put(id, created);
    return ResponseEntity.ok(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest request) {
    if (!inMemoryStore.containsKey(id)) {
      return ResponseEntity.notFound().build();
    }
    ProductResponse updated = ProductResponse.fromRequest(id, request);
    inMemoryStore.put(id, updated);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    if (!inMemoryStore.containsKey(id)) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body("El registro con ID " + id + " no existe.");
    }

    inMemoryStore.remove(id);
    return ResponseEntity.ok("El registro con ID " + id + " se eliminó correctamente.");
  }

  public record ProductRequest(String name, String description, Double price, Integer stock) { }

  public record ProductResponse(Long id, String name, String description, Double price, Integer stock, Instant updatedAt) {
    static ProductResponse fromRequest(Long id, ProductRequest request) {
      return new ProductResponse(
          id,
          request.name(),
          request.description(),
          request.price(),
          request.stock(),
          Instant.now()
      );
    }

    ProductResponse withStock(Integer newStock) {
      return new ProductResponse(id, name, description, price, newStock, updatedAt);
    }
  }
}
