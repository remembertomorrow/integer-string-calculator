package com.directio.calculator.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CalculationResponse {

    private BigDecimal response;

}
