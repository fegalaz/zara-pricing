package com.test_zara.zara_pricing.infrastructure.adapters.input.api;

import com.test_zara.zara_pricing.infrastructure.dto.PriceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceControllerApi {

    @Operation(
            summary = "Obtener precio aplicable",
            description = "Devuelve el precio activo para un producto en una fecha específica",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Precio encontrado",
                            content = @Content(schema = @Schema(implementation = PriceResponse.class))
                    )})
    @GetMapping("/final-price")
    ResponseEntity<PriceResponse> getFinalPrice(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime date,
            @RequestParam @NotNull @Positive Long productId,
            @RequestParam @NotNull @Positive Integer brandId);


    @Operation(
            summary = "Obtener precio aplicable",
            description = "Devuelve el precio activo para un producto en una fecha específica",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Precio encontrado",
                            content = @Content(schema = @Schema(implementation = PriceResponse.class))
                    )})
    @GetMapping("/final-price-convention")
    ResponseEntity<PriceResponse> getFinalPriceWithConvention(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime date,
            @RequestParam @NotNull @Positive Long productId,
            @RequestParam @NotNull @Positive Integer brandId);

    @Operation(
            summary = "Obtener lista completa de precios",
            description = "Devuelve todos los precios disponibles",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de precios obtenida exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PriceResponse.class, type = "array")))})
    @GetMapping("/price-list-all")
    ResponseEntity<List<PriceResponse>> getPricesListAll();
}
