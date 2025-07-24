package com.test_zara.zara_pricing.service;

import com.test_zara.zara_pricing.application.services.impl.PriceServiceImpl;
import com.test_zara.zara_pricing.domain.exceptions.PriceNotFoundException;
import com.test_zara.zara_pricing.domain.model.Price;
import com.test_zara.zara_pricing.domain.ports.out.PriceOutputPort;
import com.test_zara.zara_pricing.domain.ports.out.PriceEventPublisher;
import com.test_zara.zara_pricing.domain.services.PriceDomainService;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceUnitTest {

    @Mock
    private PriceOutputPort priceOutputPort;

    @Mock
    private PriceDomainService priceDomainService;

    @Mock
    private PriceEventPublisher priceEventPublisher;

    @InjectMocks
    private PriceServiceImpl priceService;

    @Test
    void whenValidRequestThenReturnPrice() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
        Price price = new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR");

        when(priceOutputPort.findFinalPrice(any(), any(), any())).thenReturn(Mono.just(price));
        doNothing().when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Mono<Price> responseMono = priceService.getFinalPrice(startDate, 35455L, 1);

        StepVerifier.create(responseMono)
            .assertNext(response -> {
                assertThat(response.price()).isEqualTo(new BigDecimal("25.45"));
                assertThat(response.productId()).isEqualTo(35455L);
            })
            .verifyComplete();
        verify(priceOutputPort, times(1)).findFinalPrice(any(), any(), any());
        verify(priceEventPublisher, times(1)).publishPriceRetrieved(price);
    }

    @Test
    void whenValidRequestThenReturnPriceWithEventPublisherException() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
        Price price = new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR");

        when(priceOutputPort.findFinalPrice(any(), any(), any())).thenReturn(Mono.just(price));
        doThrow(new RuntimeException("Publisher error")).when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Mono<Price> responseMono = priceService.getFinalPrice(startDate, 35455L, 1);

        StepVerifier.create(responseMono)
            .assertNext(response -> {
                assertThat(response.price()).isEqualTo(new BigDecimal("25.45"));
                assertThat(response.productId()).isEqualTo(35455L);
            })
            .verifyComplete();
        verify(priceOutputPort, times(1)).findFinalPrice(any(), any(), any());
        verify(priceEventPublisher, times(1)).publishPriceRetrieved(price);
    }

    @Test
    void whenPriceNotFoundThenThrowException() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);

        when(priceOutputPort.findFinalPrice(any(), any(), any())).thenReturn(Mono.empty());

        Mono<Price> responseMono = priceService.getFinalPrice(startDate, 35455L, 1);

        StepVerifier.create(responseMono)
            .expectError(PriceNotFoundException.class)
            .verify();
        verify(priceOutputPort, times(1)).findFinalPrice(any(), any(), any());
        verify(priceEventPublisher, never()).publishPriceRetrieved(any(Price.class));
    }

    @Test
    void whenValidRequestThenReturnPriceWithConvention() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
        Price price = new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR");
        List<Price> prices = Collections.singletonList(price);

        when(priceOutputPort.findFinalPriceWithConvention(any(), any(), any())).thenReturn(Flux.fromIterable(prices));
        when(priceDomainService.selectHighestPriorityPrice(prices)).thenReturn(price);
        doNothing().when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Mono<Price> responseMono = priceService.getFinalPriceWithConvention(startDate, 35455L, 1);

        StepVerifier.create(responseMono)
            .assertNext(response -> {
                assertThat(response.price()).isEqualTo(new BigDecimal("25.45"));
                assertThat(response.productId()).isEqualTo(35455L);
            })
            .verifyComplete();
        verify(priceOutputPort, times(1)).findFinalPriceWithConvention(any(), any(), any());
        verify(priceDomainService, times(1)).selectHighestPriorityPrice(prices);
        verify(priceEventPublisher, times(1)).publishPriceRetrieved(price);
    }

    @Test
    void whenValidRequestThenReturnPriceWithConventionAndEventPublisherException() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
        Price price = new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR");
        List<Price> prices = Collections.singletonList(price);

        when(priceOutputPort.findFinalPriceWithConvention(any(), any(), any())).thenReturn(Flux.fromIterable(prices));
        when(priceDomainService.selectHighestPriorityPrice(prices)).thenReturn(price);
        doThrow(new RuntimeException("Publisher error")).when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Mono<Price> responseMono = priceService.getFinalPriceWithConvention(startDate, 35455L, 1);

        StepVerifier.create(responseMono)
            .assertNext(response -> {
                assertThat(response.price()).isEqualTo(new BigDecimal("25.45"));
                assertThat(response.productId()).isEqualTo(35455L);
            })
            .verifyComplete();
        verify(priceOutputPort, times(1)).findFinalPriceWithConvention(any(), any(), any());
        verify(priceDomainService, times(1)).selectHighestPriorityPrice(prices);
        verify(priceEventPublisher, times(1)).publishPriceRetrieved(price);
    }

    @Test
    void whenPriceWithConventionNotFoundThenThrowException() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        List<Price> emptyList = Collections.emptyList();

        when(priceOutputPort.findFinalPriceWithConvention(any(), any(), any())).thenReturn(Flux.fromIterable(emptyList));
        when(priceDomainService.selectHighestPriorityPrice(emptyList))
                .thenThrow(new PriceNotFoundException("No prices available for selection"));

        Mono<Price> responseMono = priceService.getFinalPriceWithConvention(startDate, 35455L, 1);

        StepVerifier.create(responseMono)
            .expectError(PriceNotFoundException.class)
            .verify();
        verify(priceOutputPort, times(1)).findFinalPriceWithConvention(any(), any(), any());
        verify(priceDomainService, times(1)).selectHighestPriorityPrice(emptyList);
        verify(priceEventPublisher, never()).publishPriceRetrieved(any(Price.class));
    }

    @Test
    void whenValidRequestThenReturnPriceList() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
        List<Price> prices = Collections.singletonList(
                new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR")
        );

        when(priceOutputPort.findAll()).thenReturn(Flux.fromIterable(prices));
        doNothing().when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Flux<Price> responseFlux = priceService.getPricesListAll();

        StepVerifier.create(responseFlux)
            .assertNext(response -> {
                assertThat(response.price()).isEqualTo(new BigDecimal("25.45"));
                assertThat(response.productId()).isEqualTo(35455L);
            })
            .verifyComplete();
        verify(priceOutputPort, times(1)).findAll();
        verify(priceEventPublisher, times(1)).publishPriceRetrieved(prices.get(0));
    }

    @Test
    void whenValidRequestThenReturnPriceListWithEventPublisherException() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
        List<Price> prices = Collections.singletonList(
                new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR")
        );

        when(priceOutputPort.findAll()).thenReturn(Flux.fromIterable(prices));
        doThrow(new RuntimeException("Publisher error")).when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Flux<Price> responseFlux = priceService.getPricesListAll();

        StepVerifier.create(responseFlux)
            .assertNext(response -> {
                assertThat(response.price()).isEqualTo(new BigDecimal("25.45"));
                assertThat(response.productId()).isEqualTo(35455L);
            })
            .verifyComplete();
        verify(priceOutputPort, times(1)).findAll();
        verify(priceEventPublisher, times(1)).publishPriceRetrieved(prices.get(0));
    }

    @Test
    void whenValidRequestThenReturnMultiplePricesList() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
        List<Price> prices = List.of(
                new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR"),
                new Price(2, startDate, endDate, 1, 35455L, 0, new BigDecimal("30.50"), "EUR")
        );

        when(priceOutputPort.findAll()).thenReturn(Flux.fromIterable(prices));
        doNothing().when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Flux<Price> responseFlux = priceService.getPricesListAll();

        StepVerifier.create(responseFlux)
            .expectNextMatches(p -> p.price().equals(new BigDecimal("25.45")))
            .expectNextMatches(p -> p.price().equals(new BigDecimal("30.50")))
            .verifyComplete();
        verify(priceOutputPort, times(1)).findAll();
        verify(priceEventPublisher, times(2)).publishPriceRetrieved(any(Price.class));
    }
}
