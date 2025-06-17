package com.test_zara.zara_pricing.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PriceResponse {

    @Schema(description = "ID del producto", example = "35455")
    private Long productId;

    @Schema(description = "ID de la marca", example = "1")
    private Integer brandId;

    @Schema(description = "ID de la tarifa", example = "1")
    private Integer priceList;

    @Schema(description = "Fecha de inicio de aplicación",
            type = "string",
            format = "date-time",
            example = "2020-06-14 00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "Fecha de fin de aplicación",
            type = "string",
            format = "date-time",
            example = "2020-12-31 23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "Precio final", example = "35.50 EUR")
    private String finalPrice;
}