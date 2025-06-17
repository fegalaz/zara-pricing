package com.test_zara.zara_pricing.infrastructure.adapters.input.rest;

import com.test_zara.zara_pricing.domain.ports.in.PriceInputPort;
import com.test_zara.zara_pricing.infrastructure.adapters.input.api.PriceControllerApi;
import com.test_zara.zara_pricing.infrastructure.mappers.PriceMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.test_zara.zara_pricing.infrastructure.dto.PriceResponse;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rest/prices")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Prices", description = "API para gestión de precios")
public class PriceController implements PriceControllerApi {

    private final PriceInputPort priceInputPort;

    private final PriceMapper priceMapper;

    @Override
    public ResponseEntity<PriceResponse> getFinalPrice(LocalDateTime date, Long productId, Integer brandId) {
        log.info("Requesting final price for productId: {}, brandId: {}, date: {}", productId, brandId, date);

        return ResponseEntity.ok(
                priceMapper.toDto(priceInputPort.getFinalPrice(date, productId, brandId))
        );
    }

    @Override
    public ResponseEntity<PriceResponse> getFinalPriceWithConvention(LocalDateTime date, Long productId, Integer brandId) {
        log.info("Requesting final price with convention for productId: {}, brandId: {}, date: {}", productId, brandId, date);

        return ResponseEntity.ok(
                priceMapper.toDto(priceInputPort.getFinalPriceWithConvention(date, productId, brandId))
        );
    }

    @Override
    public ResponseEntity<List<PriceResponse>> getPricesListAll() {
        log.info("Requesting all prices list");

        return ResponseEntity.ok(
                priceMapper.toDto(priceInputPort.getPricesListAll())
        );
    }
}
