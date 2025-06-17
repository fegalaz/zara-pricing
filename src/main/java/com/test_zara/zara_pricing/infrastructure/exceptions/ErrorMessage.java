package com.test_zara.zara_pricing.infrastructure.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

	private int statusCode;
	private Date timestamp;
	private String message;
	private String description;
}
