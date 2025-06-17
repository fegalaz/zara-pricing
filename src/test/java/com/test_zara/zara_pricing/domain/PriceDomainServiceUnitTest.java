package com.test_zara.zara_pricing.domain;

import com.test_zara.zara_pricing.domain.exceptions.PriceNotFoundException;
import com.test_zara.zara_pricing.domain.model.Price;
import com.test_zara.zara_pricing.domain.services.PriceDomainService;
import com.test_zara.zara_pricing.fixtures.PriceFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PriceDomainServiceUnitTest {

    private PriceDomainService priceDomainService;



    @BeforeEach
    void setUp() {
        priceDomainService = new PriceDomainService();
        LocalDateTime now = LocalDateTime.now();
    }

    @Test
    void selectHighestPriorityPriceShouldReturnHighestPriorityPrice() {
        LocalDateTime now = LocalDateTime.now();
        Price lowPriorityPrice = new Price(1, now, now, 1, 1L, 0, new BigDecimal("10.00"), "EUR");
        Price mediumPriorityPrice = new Price(1, now, now, 2, 1L, 1, new BigDecimal("15.00"), "EUR");
        Price highPriorityPrice = new Price(1, now, now, 3, 1L, 2, new BigDecimal("20.00"), "EUR");

        List<Price> prices = Arrays.asList(lowPriorityPrice, highPriorityPrice, mediumPriorityPrice);

        Price result = priceDomainService.selectHighestPriorityPrice(prices);

        assertEquals(highPriorityPrice, result);
        assertEquals(2, result.priority());
    }

    @Test
    void selectHighestPriorityPriceWithEmptyListShouldThrowException() {
        List<Price> emptyPrices = Collections.emptyList();

        PriceNotFoundException exception = assertThrows(
                PriceNotFoundException.class,
                () -> priceDomainService.selectHighestPriorityPrice(emptyPrices)
        );

        assertEquals("No prices available for selection", exception.getMessage());
    }

    @Test
    void selectHighestPriorityPriceWithNullListShouldThrowException() {
        PriceNotFoundException exception = assertThrows(
                PriceNotFoundException.class,
                () -> priceDomainService.selectHighestPriorityPrice(null)
        );

        assertEquals("No prices available for selection", exception.getMessage());
    }

    @Test
    void selectHighestPriorityPriceWithSinglePriceShouldReturnThatPrice() {
        LocalDateTime now = LocalDateTime.now();
        Price singlePrice = new Price(1, now, now, 1, 1L, 5, new BigDecimal("25.99"), "EUR");
        List<Price> prices = Collections.singletonList(singlePrice);

        Price result = priceDomainService.selectHighestPriorityPrice(prices);

        assertEquals(singlePrice, result);
    }

    @Test
    void isValidPriceWithValidPriceShouldReturnTrue() {
        LocalDateTime now = LocalDateTime.now();
        Price validPrice = new Price(1, now, now, 1, 1L, 0, new BigDecimal("10.00"), "EUR");

        boolean result = this.isValidPrice(validPrice);

        assertTrue(result);
    }

    @Test
    void isValidPriceWithNullPriceShouldReturnFalse() {
        boolean result = this.isValidPrice(null);

        assertFalse(result);
    }

    @Test
    void isValidPriceWithNullPriceAmountShouldReturnFalse() {
        LocalDateTime now = LocalDateTime.now();
        Price invalidPrice = new Price(1, now, now, 1, 1L, 0, null, "EUR");

        boolean result =this.isValidPrice(invalidPrice);

        assertFalse(result);
    }

    @Test
    void isValidPriceWithZeroPriceAmountShouldReturnFalse() {

        Price invalidPrice = new Price(1, PriceFixtures.now, PriceFixtures.now, 1, 1L, 0, BigDecimal.ZERO, "EUR");

        boolean result = this.isValidPrice(invalidPrice);

        assertFalse(result);
    }

    @Test
    void isValidPriceWithNegativePriceAmountShouldReturnFalse() {
        Price invalidPrice = new Price(1, PriceFixtures.now, PriceFixtures.now, 1, 1L, 0, new BigDecimal("-10.00"), "EUR");

        boolean result = this.isValidPrice(invalidPrice);

        assertFalse(result);
    }

    @Test
    void isValidPriceWithNullCurrencyShouldReturnFalse() {
        Price invalidPrice = new Price(1, PriceFixtures.now, PriceFixtures.now, 1, 1L, 0, new BigDecimal("10.00"), null);

        boolean result = this.isValidPrice(invalidPrice);

        assertFalse(result);
    }

    @Test
    void isValidPriceWithEmptyCurrencyShouldReturnFalse() {
        Price invalidPrice = new Price(1, PriceFixtures.now, PriceFixtures.now, 1, 1L, 0, new BigDecimal("10.00"), "   ");

        boolean result = this.isValidPrice(invalidPrice);

        assertFalse(result);
    }

    public boolean isValidPrice(Price price) {
        return price != null
                && price.price() != null
                && price.price().compareTo(BigDecimal.ZERO) > 0
                && price.currency() != null
                && !price.currency().trim().isEmpty();
    }
}