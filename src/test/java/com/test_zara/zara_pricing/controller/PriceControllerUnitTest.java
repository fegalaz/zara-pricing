package com.test_zara.zara_pricing.controller;

import com.test_zara.zara_pricing.domain.model.Price;
import com.test_zara.zara_pricing.domain.ports.in.PriceInputPort;
import com.test_zara.zara_pricing.fixtures.PriceFixtures;
import com.test_zara.zara_pricing.infrastructure.adapters.input.rest.PriceController;
import com.test_zara.zara_pricing.infrastructure.dto.PriceResponse;
import com.test_zara.zara_pricing.infrastructure.mappers.PriceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceControllerUnitTest {

    @Mock
    private PriceInputPort priceInputPort;

    @Mock
    private PriceMapper priceMapper;

    @InjectMocks
    private PriceController priceController;

    @Test
    void testGetFinalPrice() {
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        Long productId = 35455L;
        Integer brandId = 1;

        Price mockPrice = new Price(
            brandId,
            LocalDateTime.of(2020, 6, 14, 0, 0),
            LocalDateTime.of(2020, 12, 31, 23, 59),
            2,
            productId,
            0,
            new BigDecimal("25.45"),
            "EUR"
        );

        PriceResponse mockResponse = PriceResponse.builder()
                .productId(productId)
                .brandId(brandId)
                .finalPrice("25.45 EUR")
                .build();

        when(priceInputPort.getFinalPrice(date, productId, brandId)).thenReturn(mockPrice);
        when(priceMapper.toDto(mockPrice)).thenReturn(mockResponse);

        ResponseEntity<PriceResponse> response = priceController.getFinalPrice(date, productId, brandId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(priceInputPort).getFinalPrice(date, productId, brandId);
        verify(priceMapper).toDto(mockPrice);
    }

    @Test
    void testGetFinalPriceWithConvention() {
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        Long productId = 35455L;
        Integer brandId = 1;

        Price mockPrice = new Price(
            brandId,
            LocalDateTime.of(2020, 6, 14, 0, 0),
            LocalDateTime.of(2020, 12, 31, 23, 59),
            2,
            productId,
            0,
            new BigDecimal("25.45"),
            "EUR"
        );

        PriceResponse mockResponse = PriceResponse.builder()
                .productId(productId)
                .brandId(brandId)
                .finalPrice("25.45 EUR")
                .build();

        when(priceInputPort.getFinalPriceWithConvention(date, productId, brandId)).thenReturn(mockPrice);
        when(priceMapper.toDto(mockPrice)).thenReturn(mockResponse);

        ResponseEntity<PriceResponse> response = priceController.getFinalPriceWithConvention(date, productId, brandId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(priceInputPort).getFinalPriceWithConvention(date, productId, brandId);
        verify(priceMapper).toDto(mockPrice);
    }

    @Test
    void testGetPricesListAll() {
        List<Price> mockPrices = PriceFixtures.getPricesFixtures();
        List<PriceResponse> mockResponses = List.of(
                PriceResponse.builder().finalPrice("19.99 EUR").build(),
                PriceResponse.builder().finalPrice("29.99 EUR").build()
        );

        when(priceInputPort.getPricesListAll()).thenReturn(mockPrices);
        when(priceMapper.toDto(mockPrices)).thenReturn(mockResponses);

        ResponseEntity<List<PriceResponse>> response = priceController.getPricesListAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponses, response.getBody());
        verify(priceInputPort).getPricesListAll();
        verify(priceMapper).toDto(mockPrices);
    }
}
