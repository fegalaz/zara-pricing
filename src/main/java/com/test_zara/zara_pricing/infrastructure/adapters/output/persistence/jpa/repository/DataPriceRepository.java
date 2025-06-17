package com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa.repository;

import com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DataPriceRepository extends JpaRepository<PriceEntity, Long> {

    @Query("""
        SELECT p FROM PriceEntity p 
        WHERE p.brandId = :brandId 
        AND p.productId = :productId 
        AND :date BETWEEN p.startDate AND p.endDate
        ORDER BY p.priority DESC
        LIMIT 1
        """)
    Optional<PriceEntity> findFinalPriceWithJpql(
            @Param("brandId") Integer brandId,
            @Param("productId") Long productId,
            @Param("date") LocalDateTime date
    );


    List<PriceEntity> findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Integer brandId,
            Long productId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
