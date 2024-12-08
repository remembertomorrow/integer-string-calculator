package com.directio.calculator.service;

import com.directio.calculator.exception.InputStringInvalidException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceTest {


    private static final String LONG_INPUT_PATH = "src/test/resources/long-test-string.txt";
    private static final String TOO_LONG_INPUT_PATH = "src/test/resources/too-long-string.txt";

    @InjectMocks
    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() throws Exception {
        Field inputLengthLimitField = CalculatorService.class.getDeclaredField("inputLengthLimit");
        inputLengthLimitField.setAccessible(true);
        inputLengthLimitField.set(calculatorService, 1000);
        Field scaleField = CalculatorService.class.getDeclaredField("resultScale");
        scaleField.setAccessible(true);
        scaleField.set(calculatorService, 10);
    }

    @Test
    @DisplayName("Basic addition and subtraction - success")
    void calculateCase1() {
        String testString = "4 + 2 - 1 + -7";
        String expectedResult = "-2";
        calculateAndAssertThatResultEquals(testString, expectedResult);
    }

    @Test
    @DisplayName("Basic multiplication and division")
    void calculateCase2() {
        String testString = "4 * 2 / -2 * -4";
        String expectedResult = "16";
        calculateAndAssertThatResultEquals(testString, expectedResult);
    }

    @Test
    @DisplayName("Division by ZERO - fail")
    void calculateCase3() {
        String testString = "4 / 0";
        assertThrows(InputStringInvalidException.class, () -> calculatorService.calculate(testString));
    }

    @Test
    @DisplayName("Fractional results - success")
    void calculateCase4() {
        String testString = "4 * 3 / 7";
        String expectedResult = "1.7142857143";
        calculateAndAssertThatResultEquals(testString, expectedResult);
    }

    @Test
    @DisplayName("Blank string - fail")
    void calculateCase5() {
        String testString = " ";
        assertThrows(InputStringInvalidException.class, () -> calculatorService.calculate(testString));
    }

    @Test
    @DisplayName("String over length limit - fail")
    void calculateCase6() throws IOException {
        String testString = loadInputString(TOO_LONG_INPUT_PATH);
        assertThrows(InputStringInvalidException.class, () -> calculatorService.calculate(testString));
    }

    @Test
    @DisplayName("Doesn't match expression regex - [int][space][+-*/][space][int]... - fail")
    void calculateCase7() {
        String testString = "2147483648 - a +  1 ";
        assertThrows(InputStringInvalidException.class, () -> calculatorService.calculate(testString));
    }

    @Test
    @DisplayName("Values in String larger than integer size - fail")
    void calculateCase8() {
        String testString = "2147483648 + 1";
        assertThrows(InputStringInvalidException.class, () -> calculatorService.calculate(testString));
    }

    @Test
    @DisplayName("Long and complex input - success")
    void calculateCase9() throws IOException {
        String testString = loadInputString(LONG_INPUT_PATH);
        String expectedResult = "-26536799212.8184580569";
        calculateAndAssertThatResultEquals(testString, expectedResult);
    }

    private void calculateAndAssertThatResultEquals(String testString, String expectedResult) {
        BigDecimal result = calculatorService.calculate(testString);
        assertThat(result,  Matchers.comparesEqualTo(new BigDecimal(expectedResult)));
    }

    private String loadInputString(String path) throws IOException {
        Path sqlFilePath = Path.of(path);
        return Files.readString(sqlFilePath);
    }

}