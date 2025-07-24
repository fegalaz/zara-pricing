package com.test_zara.zara_pricing.infrastructure.adapters.output.persistence.jpa.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceEntity {
    @Id
    private Long id;

    private Integer brandId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer priceList;
    private Long productId;
    private Integer priority;
    private BigDecimal price;
    private String curr;
}