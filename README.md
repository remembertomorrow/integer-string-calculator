# Take home task

This Spring Boot app provides a single endpoint, which can be used to calculate the value of a String represented expression consisting of space separated integers and 4 possible operators: +, -, *, and /.

In short it allows to perform those four simple arithmetic operations between integers.


## Technology

- Java 21
- Spring Boot 3.3.6
- Maven

## Input String requirements

1. It needs to adhere to the maximum input length, which is configurable via environment variable (INPUT_LENGTH_LIMIT).
   <br/><br/>
2. It has to **start** and **end** with an integer.
   <br/><br/>
3. Only integers and 4 operators +, -, *, / are allowed in the string.
   <br/><br/>
4. All integers and operators need to be separated by space.
   <br/><br/>
5. Integers have to adhere to minimum and maximum Integer constraints in Java, with the limits being -2147483648 and 2147483647 respectively.

Example of correct input:
<br/>
`"97 + 256 - 3 * 13 / 21 + 46 / 29 * -98 - 145 + 2147483000"`


## API

There is only one endpoint:

### POST `/calculator`

**request:**
```
{
    "integerString": String
}
```

**response:**
```
{
    "result": BigDecimal
}
```

## Running the Application locally

The app will run on port 8080, you can start it either:
- via InteliJ run configuration
- using `mvn spring-boot:run` command
- using spring-boot-mvn-plugin -> spring-boot:run 


<br/>

### Environment variables


| Name                 | Default Value | Description                                                                                                                      |
|:---------------------|:--------------|:---------------------------------------------------------------------------------------------------------------------------------|
| `INPUT_LENGTH_LIMIT` | 1000          | Customisable maximum length of the input String                                                                                  |
| `RESULT_SCALE`       | 10            | Customisable scale of the calculations and the result. With scale of 10, the result will be calculated to the 10th decimal place |



## Authors

- [@Wojtek Malek](https://www.github.com/remembertomorrow)

