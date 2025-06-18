package com.test_zara.zara_pricing.infrastructure.adapters.output.kafka;

import com.test_zara.zara_pricing.domain.ports.out.PriceEventPublisher;
import com.test_zara.zara_pricing.domain.model.Price;
import com.test_zara.zara_pricing.infrastructure.avro.PriceChangeType;
import com.test_zara.zara_pricing.infrastructure.avro.PricingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPriceEventPublisher implements PriceEventPublisher {

    private final KafkaTemplate<String, PricingEvent> kafkaTemplate;

    @Value("${kafka.topics.pricing-events}")
    private String pricingEventsTopic;

    /**
     * Publica un evento de precio recuperado usando Avro
     */
    @Override
    public void publishPriceRetrieved(Price price) {
        try {
            PricingEvent event = buildPricingEvent(price, PriceChangeType.NEW_PRODUCT);
            String key = price.productId() + "-" + price.brandId();

            CompletableFuture<SendResult<String, PricingEvent>> future = 
                kafkaTemplate.send(pricingEventsTopic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Evento de precio publicado exitosamente: topic={}, partition={}, offset={}, key={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            key);
                } else {
                    log.error("Error al publicar evento de precio: key={}, error={}", key, ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error al crear evento de precio: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica un evento de precio creado
     */
    public void publishPriceCreated(Price price) {
        publishPriceEvent(price, PriceChangeType.NEW_PRODUCT);
    }

    /**
     * Publica un evento de precio actualizado
     */
    public void publishPriceUpdated(Price price) {
        publishPriceEvent(price, PriceChangeType.INCREASE);
    }

    /**
     * Método privado para publicar eventos con tipo específico
     */
    private void publishPriceEvent(Price price, PriceChangeType changeType) {
        try {
            PricingEvent event = buildPricingEvent(price, changeType);
            String key = price.productId() + "-" + price.brandId();

            CompletableFuture<SendResult<String, PricingEvent>> future = 
                kafkaTemplate.send(pricingEventsTopic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Evento de precio publicado exitosamente: topic={}, partition={}, offset={}, key={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            key);
                } else {
                    log.error("Error al publicar evento de precio: key={}, error={}", key, ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error al crear evento de precio: {}", e.getMessage(), e);
        }
    }

    /**
     * Construye un evento de pricing desde un objeto Price del dominio
     */
    private PricingEvent buildPricingEvent(Price price, PriceChangeType changeType) {
        return PricingEvent.newBuilder()
                .setProductId(price.productId().toString())
                .setProductName("Product-" + price.productId()) // Nombre genérico basado en ID
                .setOldPrice(null) // No tenemos precio anterior en este contexto
                .setNewPrice(price.price().doubleValue())
                .setCurrency(price.currency())
                .setChangeType(changeType)
                .setTimestamp(Instant.now().toEpochMilli())
                .setStoreId(null) // No tenemos storeId en el modelo actual
                .setCategory("ROPA") // Categoría por defecto
                .build();
    }
} 