package com.directio.calculator.controller;

import com.directio.calculator.request.CalculationRequest;
import com.directio.calculator.response.CalculationResponse;
import com.directio.calculator.service.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calculator")
public class CalculatorController {

    private final CalculatorService calculatorService;

    @PostMapping
    public CalculationResponse calculate(@RequestBody CalculationRequest request) {
        return calculatorService.calculateWithResponse(request);
    }

}
