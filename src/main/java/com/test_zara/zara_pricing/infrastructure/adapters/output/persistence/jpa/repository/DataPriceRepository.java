package com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa.repository;

import com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa.entity.PriceEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Repositorio R2DBC para la entidad PriceEntity
 * 
 * MÉTODOS AUTOMÁTICOS DISPONIBLES (heredados de R2dbcRepository):
 * - save(PriceEntity entity) -> Mono<PriceEntity>
 * - saveAll(Iterable<PriceEntity>) -> Flux<PriceEntity>
 * - findById(Long id) -> Mono<PriceEntity>
 * - findAll() -> Flux<PriceEntity>
 * - findAllById(Iterable<Long>) -> Flux<PriceEntity>
 * - count() -> Mono<Long>
 * - existsById(Long id) -> Mono<Boolean>
 * - deleteById(Long id) -> Mono<Void>
 * - delete(PriceEntity entity) -> Mono<Void>
 * - deleteAll() -> Mono<Void>
 * - deleteAllById(Iterable<Long>) -> Mono<Void>
 * 
 * IMPORTANTE: R2DBC NO soporta métodos derivados como JPA
 * (ej: findByBrandIdAndProductId) - solo @Query con SQL nativo
 */
@Repository
public interface DataPriceRepository extends R2dbcRepository<PriceEntity, Long> {

    /**
     * Consulta optimizada: devuelve un solo precio con mayor prioridad
     * Usa LIMIT 1 para eficiencia
     */
    @Query("""
        SELECT * FROM prices 
        WHERE brand_id = :brandId 
        AND product_id = :productId 
        AND :date BETWEEN start_date AND end_date
        ORDER BY priority DESC
        LIMIT 1
        """)
    Mono<PriceEntity> findFinalPriceWithJpql(
            @Param("brandId") Integer brandId,
            @Param("productId") Long productId,
            @Param("date") LocalDateTime date
    );

    /**
     * Consulta completa: devuelve todos los precios para que la lógica de negocio
     * seleccione el adecuado según las reglas de prioridad
     */
    @Query("""
        SELECT * FROM prices 
        WHERE brand_id = :brandId 
        AND product_id = :productId 
        AND :date BETWEEN start_date AND end_date
        ORDER BY priority DESC
        """)
    Flux<PriceEntity> findFinalPriceWithConvention(
            @Param("brandId") Integer brandId,
            @Param("productId") Long productId,
            @Param("date") LocalDateTime date
    );
}
