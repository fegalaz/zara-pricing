package com.test_zara.zara_pricing.application.services.impl;


import com.test_zara.zara_pricing.application.services.PriceService;
import com.test_zara.zara_pricing.domain.exceptions.PriceNotFoundException;
import com.test_zara.zara_pricing.domain.model.Price;
import com.test_zara.zara_pricing.domain.ports.in.PriceInputPort;
import com.test_zara.zara_pricing.domain.ports.out.PriceOutputPort;
import com.test_zara.zara_pricing.domain.services.PriceDomainService;
import com.test_zara.zara_pricing.domain.ports.out.PriceEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceServiceImpl implements PriceService, PriceInputPort {

    private final PriceOutputPort priceOutputPort;
    private final PriceDomainService priceDomainService;
    private final PriceEventPublisher priceEventPublisher;

    @Override
    public Price getFinalPrice(LocalDateTime date, Long productId, Integer brandId) {
        Price price = priceOutputPort.findFinalPrice(brandId, productId, date)
                .orElseThrow(() -> new PriceNotFoundException("Price not found for the given criteria"));

        try {
            priceEventPublisher.publishPriceRetrieved(price);
            log.info("Evento de precio recuperado publicado para productId: {}, brandId: {}", productId, brandId);
        } catch (Exception e) {
            log.error("Error al publicar evento de precio recuperado: {}", e.getMessage(), e);
        }
        return price;
    }

    @Override
    public Price getFinalPriceWithConvention(LocalDateTime date, Long productId, Integer brandId) {
        List<Price> applicablePrices = priceOutputPort.findFinalPriceWithConvention(brandId, productId, date);
        Price selectedPrice = priceDomainService.selectHighestPriorityPrice(applicablePrices);

        try {
            priceEventPublisher.publishPriceRetrieved(selectedPrice);
            log.info("Evento de precio con convención publicado para productId: {}, brandId: {}", productId, brandId);
        } catch (Exception e) {
            log.error("Error al publicar evento de precio con convención: {}", e.getMessage(), e);
        }
        return selectedPrice;
    }

    @Override
    public List<Price> getPricesListAll() {
        List<Price> prices = priceOutputPort.findAll();

        prices.forEach(price -> {
            try {
                priceEventPublisher.publishPriceRetrieved(price);
            } catch (Exception e) {
                log.error("Error al publicar evento de precio en lista: {}", e.getMessage(), e);
            }
        });
        return prices;
    }
}
