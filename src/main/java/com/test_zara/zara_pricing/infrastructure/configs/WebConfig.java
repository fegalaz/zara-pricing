package com.test_zara.zara_pricing.infrastructure.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLocalDateTimeConverter());
    }

    public static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

        private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),      // Formato principal
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss"),      // Formato alternativo
            DateTimeFormatter.ISO_LOCAL_DATE_TIME                    // Formato ISO
        };

        @Override
        public LocalDateTime convert(@NonNull String source) {
            DateTimeParseException lastException = null;

            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDateTime.parse(source, formatter);
                } catch (DateTimeParseException e) {
                    lastException = e;
                }
            }

            throw new IllegalArgumentException(
                String.format("Formato de fecha inválido: '%s'. Use alguno de estos formatos: yyyy-MM-dd HH:mm:ss, yyyy-MM-dd-HH.mm.ss, o ISO", source),
                lastException
            );
        }
    }
}