package com.test_zara.zara_pricing.infrastructure.exceptions;

import com.test_zara.zara_pricing.domain.exceptions.PriceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(PriceNotFoundException.class)
	public ResponseEntity<ErrorMessage> handlePriceNotFoundException(PriceNotFoundException ex, WebRequest request) {
		ErrorMessage message = new ErrorMessage(
				HttpStatus.NOT_FOUND.value(), new Date(), ex.getMessage(), request.getDescription(false)
		);
		return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
	}
}
