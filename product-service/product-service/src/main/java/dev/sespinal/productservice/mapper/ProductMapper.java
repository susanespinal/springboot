package dev.sespinal.productservice.mapper;


import dev.sespinal.productservice.dto.ProductRequest;
import dev.sespinal.productservice.dto.ProductResponse;
import dev.sespinal.productservice.model.Product;

public final class ProductMapper {

  private ProductMapper() {
    throw new AssertionError("Utility class, no debe instanciarse");
  }

  public static ProductResponse toResponse(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getStock(),
        product.getCreatedAt(),
        product.getUpdatedAt()
    );
  }

  public static Product toEntity(ProductRequest request, Product entity) {
    entity.setName(request.name());
    entity.setDescription(request.description());
    entity.setPrice(request.price());
    entity.setStock(request.stock());
    return entity;
  }
}
