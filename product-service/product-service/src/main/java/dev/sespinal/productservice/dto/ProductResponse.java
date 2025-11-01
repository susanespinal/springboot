package dev.sespinal.productservice.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {

}