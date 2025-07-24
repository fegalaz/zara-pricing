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
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
        // Arrange
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

        when(priceInputPort.getFinalPrice(date, productId, brandId)).thenReturn(Mono.just(mockPrice));
        when(priceMapper.toDto(mockPrice)).thenReturn(mockResponse);

        // Act
        Mono<PriceResponse> responseMono = priceController.getFinalPrice(date, productId, brandId);

        // Assert
        StepVerifier.create(responseMono)
                .expectNext(mockResponse)
                .verifyComplete();

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

        when(priceInputPort.getFinalPriceWithConvention(date, productId, brandId)).thenReturn(Mono.just(mockPrice));
        when(priceMapper.toDto(mockPrice)).thenReturn(mockResponse);

        Mono<PriceResponse> responseMono = priceController.getFinalPriceWithConvention(date, productId, brandId);

        StepVerifier.create(responseMono)
                .expectNext(mockResponse)
                .verifyComplete();
        verify(priceInputPort).getFinalPriceWithConvention(date, productId, brandId);
        verify(priceMapper).toDto(mockPrice);
    }

    @Test
    void testGetPricesListAll() {
        List<Price> mockPrices = PriceFixtures.getPricesFixtures();
        PriceResponse mockResponse1 = PriceResponse.builder().finalPrice("19.99 EUR").build();
        PriceResponse mockResponse2 = PriceResponse.builder().finalPrice("29.99 EUR").build();

        when(priceInputPort.getPricesListAll()).thenReturn(Flux.fromIterable(mockPrices));
        when(priceMapper.toDto(mockPrices.get(0))).thenReturn(mockResponse1);
        when(priceMapper.toDto(mockPrices.get(1))).thenReturn(mockResponse2);

        Flux<PriceResponse> responseFlux = priceController.getPricesListAll();

        StepVerifier.create(responseFlux)
                .expectNext(mockResponse1)
                .expectNext(mockResponse2)
                .verifyComplete();
        verify(priceInputPort).getPricesListAll();
        verify(priceMapper).toDto(mockPrices.get(0));
        verify(priceMapper).toDto(mockPrices.get(1));
    }
}
