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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

        when(priceOutputPort.findFinalPrice(any(), any(), any())).thenReturn(Optional.of(price));
        doNothing().when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Price response = priceService.getFinalPrice(startDate, 35455L, 1);

        assertThat(response.price()).isEqualTo(new BigDecimal("25.45"));
        assertThat(response.productId()).isEqualTo(35455L);
        verify(priceOutputPort, times(1)).findFinalPrice(any(), any(), any());
        verify(priceEventPublisher, times(1)).publishPriceRetrieved(price);
    }

    @Test
    void whenValidRequestThenReturnPriceWithEventPublisherException() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
        Price price = new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR");

        when(priceOutputPort.findFinalPrice(any(), any(), any())).thenReturn(Optional.of(price));
        doThrow(new RuntimeException("Publisher error")).when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Price response = priceService.getFinalPrice(startDate, 35455L, 1);

        assertThat(response.price()).isEqualTo(new BigDecimal("25.45"));
        assertThat(response.productId()).isEqualTo(35455L);
        verify(priceOutputPort, times(1)).findFinalPrice(any(), any(), any());
        verify(priceEventPublisher, times(1)).publishPriceRetrieved(price);
    }

    @Test
    void whenPriceNotFoundThenThrowException() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);

        when(priceOutputPort.findFinalPrice(any(), any(), any())).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                PriceNotFoundException.class,
                () -> priceService.getFinalPrice(startDate, 35455L, 1)
        );
        verify(priceOutputPort, times(1)).findFinalPrice(any(), any(), any());
        verify(priceEventPublisher, never()).publishPriceRetrieved(any(Price.class));
    }

    @Test
    void whenValidRequestThenReturnPriceWithConvention() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
        Price price = new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR");
        List<Price> prices = Collections.singletonList(price);

        when(priceOutputPort.findFinalPriceWithConvention(any(), any(), any())).thenReturn(prices);
        when(priceDomainService.selectHighestPriorityPrice(prices)).thenReturn(price);
        doNothing().when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Price response = priceService.getFinalPriceWithConvention(startDate, 35455L, 1);

        assertThat(response.price()).isEqualTo(new BigDecimal("25.45"));
        assertThat(response.productId()).isEqualTo(35455L);
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

        when(priceOutputPort.findFinalPriceWithConvention(any(), any(), any())).thenReturn(prices);
        when(priceDomainService.selectHighestPriorityPrice(prices)).thenReturn(price);
        doThrow(new RuntimeException("Publisher error")).when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        Price response = priceService.getFinalPriceWithConvention(startDate, 35455L, 1);

        assertThat(response.price()).isEqualTo(new BigDecimal("25.45"));
        assertThat(response.productId()).isEqualTo(35455L);
        verify(priceOutputPort, times(1)).findFinalPriceWithConvention(any(), any(), any());
        verify(priceDomainService, times(1)).selectHighestPriorityPrice(prices);
        verify(priceEventPublisher, times(1)).publishPriceRetrieved(price);
    }

    @Test
    void whenPriceWithConventionNotFoundThenThrowException() {
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        List<Price> emptyList = Collections.emptyList();

        when(priceOutputPort.findFinalPriceWithConvention(any(), any(), any())).thenReturn(emptyList);
        when(priceDomainService.selectHighestPriorityPrice(emptyList))
                .thenThrow(new PriceNotFoundException("No prices available for selection"));

        org.junit.jupiter.api.Assertions.assertThrows(
                PriceNotFoundException.class,
                () -> priceService.getFinalPriceWithConvention(startDate, 35455L, 1)
        );
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

        when(priceOutputPort.findAll()).thenReturn(prices);
        doNothing().when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        List<Price> response = priceService.getPricesListAll();

        assertThat(response.getFirst().price()).isEqualTo(new BigDecimal("25.45"));
        assertThat(response.getFirst().productId()).isEqualTo(35455L);
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

        when(priceOutputPort.findAll()).thenReturn(prices);
        doThrow(new RuntimeException("Publisher error")).when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        List<Price> response = priceService.getPricesListAll();

        assertThat(response.getFirst().price()).isEqualTo(new BigDecimal("25.45"));
        assertThat(response.getFirst().productId()).isEqualTo(35455L);
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

        when(priceOutputPort.findAll()).thenReturn(prices);
        doNothing().when(priceEventPublisher).publishPriceRetrieved(any(Price.class));

        List<Price> response = priceService.getPricesListAll();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).price()).isEqualTo(new BigDecimal("25.45"));
        assertThat(response.get(1).price()).isEqualTo(new BigDecimal("30.50"));
        verify(priceOutputPort, times(1)).findAll();
        verify(priceEventPublisher, times(2)).publishPriceRetrieved(any(Price.class));
    }
}
