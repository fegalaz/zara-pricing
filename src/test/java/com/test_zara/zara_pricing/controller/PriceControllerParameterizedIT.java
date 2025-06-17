package com.test_zara.zara_pricing.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerParameterizedIT {

@Autowired
private MockMvc mockMvc;

@ParameterizedTest
@MethodSource("priceTestCases")
@DisplayName("whenGetFinalPriceThenReturnCorrectPrice")
void whenGetFinalPriceThenReturnCorrectPrice(
        String testName,
        String applicationDate,
        int expectedPriceList,
        String expectedPrice,
        String expectedStartDate,
        String expectedEndDate
) throws Exception {
    mockMvc.perform(get("/api/v1/rest/prices/final-price")
                    .param("date", applicationDate)
                    .param("productId", "35455")
                    .param("brandId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId", is(35455)))
            .andExpect(jsonPath("$.brandId", is(1)))
            .andExpect(jsonPath("$.priceList", is(expectedPriceList)))
            .andExpect(jsonPath("$.finalPrice", is(expectedPrice)))
            .andExpect(jsonPath("$.startDate", is(expectedStartDate)))
            .andExpect(jsonPath("$.endDate", is(expectedEndDate)));
}

@ParameterizedTest
@MethodSource("priceTestCases")
@DisplayName("whenGetFinalPriceWithConventionThenReturnCorrectPrice")
void whenGetFinalPriceWithConventionThenReturnCorrectPrice(
        String testName,
        String applicationDate,
        int expectedPriceList,
        String expectedPrice,
        String expectedStartDate,
        String expectedEndDate
) throws Exception {
    mockMvc.perform(get("/api/v1/rest/prices/final-price-convention")
                    .param("date", applicationDate)
                    .param("productId", "35455")
                    .param("brandId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId", is(35455)))
            .andExpect(jsonPath("$.brandId", is(1)))
            .andExpect(jsonPath("$.priceList", is(expectedPriceList)))
            .andExpect(jsonPath("$.finalPrice", is(expectedPrice)))
            .andExpect(jsonPath("$.startDate", is(expectedStartDate)))
            .andExpect(jsonPath("$.endDate", is(expectedEndDate)));
}

private static Stream<Arguments> priceTestCases() {
    return Stream.of(
        Arguments.of("Test 1: 10:00 del día 14", "2020-06-14 10:00:00", 1, "35.50 EUR", "2020-06-14 00:00:00", "2020-12-31 23:59:59"),
        Arguments.of("Test 2: 16:00 del día 14", "2020-06-14 16:00:00", 2, "25.45 EUR", "2020-06-14 15:00:00", "2020-06-14 18:30:00"),
        Arguments.of("Test 3: 21:00 del día 14", "2020-06-14 21:00:00", 1, "35.50 EUR", "2020-06-14 00:00:00", "2020-12-31 23:59:59"),
        Arguments.of("Test 4: 10:00 del día 15", "2020-06-15 10:00:00", 3, "30.50 EUR", "2020-06-15 00:00:00", "2020-06-15 11:00:00"),
        Arguments.of("Test 5: 21:00 del día 16", "2020-06-16 21:00:00", 4, "38.95 EUR", "2020-06-15 16:00:00", "2020-12-31 23:59:59")
    );}

@Test
void whenInvalidDateThenBadRequest() throws Exception {
    mockMvc.perform(get("/api/v1/rest/prices/final-price")
                    .param("date", "invalid-date")
                    .param("productId", "35455")
                    .param("brandId", "1"))
            .andExpect(status().isBadRequest());
}
}
