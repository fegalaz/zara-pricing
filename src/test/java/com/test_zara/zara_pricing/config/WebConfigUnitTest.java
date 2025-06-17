package com.test_zara.zara_pricing.config;

import com.test_zara.zara_pricing.infrastructure.configs.WebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebConfigUnitTest {

    private WebConfig.StringToLocalDateTimeConverter converter;

    @BeforeEach
    void setUp() {
        converter = new WebConfig.StringToLocalDateTimeConverter();
    }

    @Test
    void whenTestConvertStandardFormat() {
        String dateString = "2020-06-14 15:00:00";
        LocalDateTime expected = LocalDateTime.of(2020, 6, 14, 15, 0, 0);

        LocalDateTime result = converter.convert(dateString);

        assertEquals(expected, result);
    }

    @Test
    void testConvertAlternativeFormat() {
        String dateString = "2020-06-14-15.00.00";
        LocalDateTime expected = LocalDateTime.of(2020, 6, 14, 15, 0, 0);

        LocalDateTime result = converter.convert(dateString);

        assertEquals(expected, result);
    }

    @Test
    void testConvertISOFormat() {
        String dateString = "2020-06-14T15:00:00";
        LocalDateTime expected = LocalDateTime.of(2020, 6, 14, 15, 0, 0);

        LocalDateTime result = converter.convert(dateString);

        assertEquals(expected, result);
    }

    @Test
    void testConvertInvalidFormat() {

        String dateString = "invalid-date-format";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(dateString)
        );

        assertTrue(exception.getMessage().contains("Formato de fecha inválido"));
        assertTrue(exception.getMessage().contains("Use alguno de estos formatos"));
    }

    @Test
    void testConvertEmptyString() {
        String dateString = "";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(dateString)
        );

        assertTrue(exception.getMessage().contains("Formato de fecha inválido"));
    }

    @Test
    void testConvertNullString() {
        assertThrows(
                NullPointerException.class,
                () -> converter.convert(null)
        );
    }

    @Test
    void testConvertUnsupportedFormat() {
        String dateString = "2020/06/14 15:00:00";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(dateString)
        );

        assertTrue(exception.getMessage().contains("Formato de fecha inválido"));
    }
}