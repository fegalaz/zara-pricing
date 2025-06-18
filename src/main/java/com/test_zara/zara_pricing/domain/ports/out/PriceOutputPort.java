package com.test_zara.zara_pricing.domain.ports.out;

import com.test_zara.zara_pricing.domain.model.Price;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PriceOutputPort {

    Optional<Price> findFinalPrice(
            Integer brandId,
            Long productId,
            LocalDateTime date
    );

    List<Price> findFinalPriceWithConvention(
            Integer brandId,
            Long productId,
            LocalDateTime date
    );

    List<Price> findAll();
}
