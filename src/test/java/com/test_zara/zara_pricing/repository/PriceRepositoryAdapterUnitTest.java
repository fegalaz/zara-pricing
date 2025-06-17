package com.test_zara.zara_pricing.repository;


import com.test_zara.zara_pricing.domain.model.Price;
import com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa.PriceRepositoryAdapter;
import com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa.entity.PriceEntity;
import com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa.repository.DataPriceRepository;
import com.test_zara.zara_pricing.infrastructure.mappers.PriceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceRepositoryAdapterUnitTest {

    @Mock
    private DataPriceRepository dataPriceRepository;

    @Spy
    private PriceMapper priceMapper;

    private PriceRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PriceRepositoryAdapter(dataPriceRepository, priceMapper);
    }

    @Test
    void testFindFinalPriceReturnsMappedPrice() {
        Integer brandId = 1;
        Long productId = 2L;
        LocalDateTime date = LocalDateTime.now();
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);

        Optional<PriceEntity> entity = Optional.of(new PriceEntity());
        Price price = new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR");

        when(dataPriceRepository.findFinalPriceWithJpql(brandId, productId, date)).thenReturn(entity);
        when(priceMapper.toDomain(any(PriceEntity.class))).thenReturn(price);

        Optional<Price> result = adapter.findFinalPrice(brandId, productId, date);

        assertTrue(result.isPresent());
        assertEquals(price, result.get());
        verify(dataPriceRepository).findFinalPriceWithJpql(brandId, productId, date);
    }

    @Test
    void testFindFinalPriceWithConventionReturnsMappedPrice() {
        Integer brandId = 1;
        Long productId = 2L;
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 18, 30, 0);

        PriceEntity entity = new PriceEntity();
        List<PriceEntity> entities = Collections.singletonList(entity);

        Price expectedPrice = new Price(1, date, date, 2, 35455L, 0, new BigDecimal("25.45"), "EUR");

        when(dataPriceRepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(brandId, productId, date,date)).thenReturn(entities);
        when(priceMapper.toDomain(entity)).thenReturn(expectedPrice);

        List<Price> result = adapter.findFinalPriceWithConvention(brandId, productId, date);

        assertEquals(1, result.size());
        assertEquals(expectedPrice, result.getFirst());
        verify(dataPriceRepository).findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(brandId, productId, date,date);
    }

    @Test
    void testFindAllReturnsMappedPrice() {
        PriceEntity entity = new PriceEntity();
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
        Price price = new Price(1, startDate, endDate, 2, 35455L, 0, new BigDecimal("25.45"), "EUR");

        when(dataPriceRepository.findAll()).thenReturn(List.of(entity));
        when(priceMapper.toDomain(entity)).thenReturn(price);

        List<Price> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals(price, result.getFirst());
        verify(dataPriceRepository).findAll();
    }
}
