package dev.sespinal.productservice.dto;

import java.math.BigDecimal;

public record ProductSummaryResponse (
  Long id,
  String name,
  BigDecimal price
  ){

}
