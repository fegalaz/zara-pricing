package com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa;

import com.test_zara.zara_pricing.domain.model.Price;
import com.test_zara.zara_pricing.domain.ports.out.PriceOutputPort;
import com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa.repository.DataPriceRepository;
import com.test_zara.zara_pricing.infrastructure.mappers.PriceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PriceRepositoryAdapter implements PriceOutputPort {

    private final DataPriceRepository jpaRepository;

    private final PriceMapper priceMapper;

    @Override
    public Optional<Price> findFinalPrice(
            Integer brandId,
            Long productId,
            LocalDateTime date
    ) {
        return jpaRepository
                .findFinalPriceWithJpql(brandId, productId, date)
                .map(priceMapper::toDomain);
    }

    @Override
    public List<Price> findFinalPriceWithConvention(
            Integer brandId,
            Long productId,
            LocalDateTime date
    ) {
        return jpaRepository
                .findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        brandId, productId, date, date)
                .stream()
                .map(priceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Price> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(priceMapper::toDomain)
                .toList();
    }
}
