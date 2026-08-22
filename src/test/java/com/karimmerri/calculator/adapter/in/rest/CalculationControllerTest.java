package com.karimmerri.calculator.adapter.in.rest;

import com.karimmerri.calculator.config.CalculatorConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculationController.class)
@Import({
        CalculatorConfiguration.class,
        RestExceptionHandler.class
})
class CalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCalculateExpression() throws Exception {
        mockMvc.perform(
                        post("/api/v1/calculations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "expression": "2 + 2 * 5 + 5"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.expression")
                        .value("2 + 2 * 5 + 5"))
                .andExpect(jsonPath("$.result").value("17"));
    }

    @Test
    void shouldReturnBadRequestForDivisionByZero() throws Exception {
        mockMvc.perform(
                        post("/api/v1/calculations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "expression": "1 / 0"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CALCULATION_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("Division by zero is not allowed"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/calculations"));
    }

    @Test
    void shouldReturnBadRequestForInvalidSyntax() throws Exception {
        mockMvc.perform(
                        post("/api/v1/calculations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "expression": "1 +"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SYNTAX_ERROR"));
    }

    @Test
    void shouldReturnBadRequestForInvalidCharacter() throws Exception {
        mockMvc.perform(
                        post("/api/v1/calculations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "expression": "2 @ 3"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("LEXICAL_ERROR"));
    }

    @Test
    void shouldValidateBlankExpression() throws Exception {
        mockMvc.perform(
                        post("/api/v1/calculations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "expression": "   "
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message")
                        .value("expression must not be blank"));
    }

    @Test
    void shouldValidateMissingExpression() throws Exception {
        mockMvc.perform(
                        post("/api/v1/calculations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldCalculateAdvancedExpression() throws Exception {
        mockMvc.perform(
                        post("/api/v1/calculations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "expression": "sqrt(4) + 2^3"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("10"));
    }
}
