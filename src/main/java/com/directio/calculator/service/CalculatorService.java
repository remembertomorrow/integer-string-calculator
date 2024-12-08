package com.directio.calculator.service;

import com.directio.calculator.exception.InputStringInvalidException;
import com.directio.calculator.request.CalculationRequest;
import com.directio.calculator.response.CalculationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

@Service
@Scope(value="prototype", proxyMode= ScopedProxyMode.TARGET_CLASS)
public class CalculatorService {

    private static final String ADDITION_OPERATOR = "+";
    private static final String SUBTRACTION_OPERATOR = "-";
    private static final String MULTIPLICATION_OPERATOR = "*";
    private static final String DIVISION_OPERATOR = "/";

    private int currentIndex = 0;
    private String[] expressionElements;

    @Value("${calculator.input-length-limit}")
    private Integer inputLengthLimit;

    @Value("${calculator.result.scale}")
    private Integer resultScale;


    public CalculationResponse calculateWithResponse(CalculationRequest req) {
        return new CalculationResponse(calculate(req.getIntegerString()));
    }


    /**
     *The main method in the calculator class. The algorithm works as follows:
     *<br><br>
     * 1. Split expression using space as a separator, and produce String array containing all integers and operators
     *<br><br>
     * 2. Validate the input
     *<br><br>
     * 3. Try to perform first precedence operation (in our case: multiplication or division)
     *<br><br>
     * 4. While operations at hand are second precedence (addition or subtraction), just continue performing them, until you encounter a first precedence operation, in which case perform it for as long as needed, and go back to second precedence
     * @return BigDecimal
     */
    public BigDecimal calculate(String integerString) {
        expressionElements = integerString.split(" ");
        validateIntegerString(integerString);

        BigDecimal number = calculateFirstPrecedence();

        while (isCurrentIndexWithinBounds() &&
                (currentToken().equals(ADDITION_OPERATOR) || currentToken().equals(SUBTRACTION_OPERATOR))
        ) {
            String operator = currentToken();
            moveIndex();
            BigDecimal otherNumber = calculateFirstPrecedence();
            number = operator.equals(ADDITION_OPERATOR) ?
                    number.add(otherNumber) : number.subtract(otherNumber);
        }
        return number.stripTrailingZeros();
    }


    /**
     * Method to calculate first precedence operations (multiplication or division)
     *<br><br>
     * 1. Get the number at hand
     *<br><br>
     * 2. While operations at hand are first precedence, just continue performing them, until you encounter a second precedence operation, in which case return results
     * @return BigDecimal
     */
    private BigDecimal calculateFirstPrecedence() {

        BigDecimal number = processNumber();

        while (isCurrentIndexWithinBounds() &&
                (currentToken().equals(MULTIPLICATION_OPERATOR) || currentToken().equals(DIVISION_OPERATOR))
        ) {
            String operator = currentToken();
            moveIndex();
            BigDecimal otherNumber = processNumber();
            number = operator.equals(MULTIPLICATION_OPERATOR) ?
                multiply(number, otherNumber) :
                divide(number, otherNumber);
        }
        return number;
    }


    /**
     * Method to get the number at a given index in the array
     * @return BigDecimal
     */
    private BigDecimal processNumber() {
        BigDecimal number = new BigDecimal(currentToken()).setScale(resultScale, RoundingMode.HALF_EVEN);
        moveIndex();
        return number;
    }


    private BigDecimal multiply(BigDecimal number, BigDecimal otherNumber) {
        return number.multiply(otherNumber).setScale(resultScale, RoundingMode.HALF_EVEN);
    }

    private BigDecimal divide(BigDecimal number, BigDecimal otherNumber) {
        if (otherNumber.compareTo(BigDecimal.ZERO) == 0) {
            throw new InputStringInvalidException("Division by zero is not allowed");
        }
        return number.divide(otherNumber, resultScale, RoundingMode.HALF_EVEN);
    }

    private boolean isCurrentIndexWithinBounds() {
        return currentIndex < expressionElements.length;
    }

    private String currentToken() {
        return expressionElements[currentIndex];
    }

    private void moveIndex() {
        currentIndex++;
    }

    private void validateIntegerString(String inputString) {

        String regex = "(-?\\d+)(\\s+[+\\-*/]\\s+-?\\d+)*";

        if (inputString == null || inputString.isBlank()) {
            throw new InputStringInvalidException("Input string cannot be null or blank");
        }
        if (inputString.length() > inputLengthLimit) {
            throw new InputStringInvalidException(String.format("Input string maximum length is %d chars", inputLengthLimit));
        }
        if (!inputString.matches(regex)) {
            throw new InputStringInvalidException(
                    "Input string has to start and end with an integer, consist of only integers and four operators: +,-,*,/, and have all the integers and operators separated with a space"
            );
        }
        checkIfAllValuesIntegers();

    }

    private void checkIfAllValuesIntegers() {
        Arrays.stream(expressionElements).filter(e -> e.length() != 1).forEach(e -> {
            try {
                Integer.parseInt(e);
            } catch (NumberFormatException ex) {
                throw new InputStringInvalidException(
                        "Values in String need to be within Java integer limits, that is >= -2147483648 && <= 2147483647"
                );
            }
        });
    }

}

