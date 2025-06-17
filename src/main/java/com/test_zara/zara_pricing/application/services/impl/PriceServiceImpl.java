package com.test_zara.zara_pricing.application.services.impl;


import com.test_zara.zara_pricing.application.services.PriceService;
import com.test_zara.zara_pricing.domain.exceptions.PriceNotFoundException;
import com.test_zara.zara_pricing.domain.model.Price;
import com.test_zara.zara_pricing.domain.ports.in.PriceInputPort;
import com.test_zara.zara_pricing.domain.ports.out.PriceOutputPort;
import com.test_zara.zara_pricing.domain.services.PriceDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService, PriceInputPort {

    private final PriceOutputPort priceOutputPort;
    private final PriceDomainService priceDomainService;

    @Override
    public Price getFinalPrice(LocalDateTime date, Long productId, Integer brandId) {
        return priceOutputPort.findFinalPrice(brandId, productId, date)
                .orElseThrow(() -> new PriceNotFoundException("Price not found for the given criteria"));
    }

    @Override
    public Price getFinalPriceWithConvention(LocalDateTime date, Long productId, Integer brandId) {
        List<Price> applicablePrices = priceOutputPort.findFinalPriceWithConvention(brandId, productId, date);

        return priceDomainService.selectHighestPriorityPrice(applicablePrices);
    }

    @Override
    public List<Price> getPricesListAll() {
        return priceOutputPort.findAll();
    }
}
