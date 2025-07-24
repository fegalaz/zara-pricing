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
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceServiceImpl implements PriceService, PriceInputPort {

    private final PriceOutputPort priceOutputPort;
    private final PriceDomainService priceDomainService;
    private final PriceEventPublisher priceEventPublisher;

    @Override
    public Mono<Price> getFinalPrice(LocalDateTime date, Long productId, Integer brandId) {
        return priceOutputPort.findFinalPrice(brandId, productId, date)
                .switchIfEmpty(Mono.error(new PriceNotFoundException("Price not found for the given criteria")))
                .doOnNext(price -> {
                    try {
                        priceEventPublisher.publishPriceRetrieved(price);
                        log.info("Evento de precio recuperado publicado para productId: {}, brandId: {}", productId, brandId);
                    } catch (Exception e) {
                        log.error("Error al publicar evento de precio recuperado: {}", e.getMessage(), e);
                    }
                });
    }

    @Override
    public Mono<Price> getFinalPriceWithConvention(LocalDateTime date, Long productId, Integer brandId) {
        return priceOutputPort.findFinalPriceWithConvention(brandId, productId, date)
                .collectList() // Junta todos los precios en una lista
                .map(prices -> priceDomainService.selectHighestPriorityPrice(prices)) // Aplica la lógica de dominio
                .switchIfEmpty(Mono.error(new PriceNotFoundException("Price not found for the given criteria")))
                .doOnNext(selectedPrice -> {
                    try {
                        priceEventPublisher.publishPriceRetrieved(selectedPrice);
                        log.info("Evento de precio con convención publicado para productId: {}, brandId: {}", productId, brandId);
                    } catch (Exception e) {
                        log.error("Error al publicar evento de precio con convención: {}", e.getMessage(), e);
                    }
                });
    }

    @Override
    public Flux<Price> getPricesListAll() {
        return priceOutputPort.findAll()
                .doOnNext(price -> {
                    try {
                        priceEventPublisher.publishPriceRetrieved(price);
                    } catch (Exception e) {
                        log.error("Error al publicar evento de precio en lista: {}", e.getMessage(), e);
                    }
                });
    }
}
