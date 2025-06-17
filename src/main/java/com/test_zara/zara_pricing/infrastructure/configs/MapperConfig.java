package com.test_zara.zara_pricing.infrastructure.configs;

import com.test_zara.zara_pricing.infrastructure.mappers.PriceMapper;
import com.test_zara.zara_pricing.infrastructure.mappers.PriceMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public PriceMapper priceMapper() {
        return new PriceMapperImpl();
    }
} 