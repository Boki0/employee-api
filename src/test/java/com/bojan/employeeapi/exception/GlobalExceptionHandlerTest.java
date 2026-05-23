package com.bojan.employeeapi.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return 404 when resource is not found")
    void shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Employee was not found"))
                .andExpect(jsonPath("$.path").value("/test/not-found"));
    }

    @Test
    @DisplayName("Should return 409 when conflict happens")
    void shouldReturnConflict() throws Exception {
        mockMvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Employee already exists"))
                .andExpect(jsonPath("$.path").value("/test/conflict"));
    }

    @Test
    @DisplayName("Should return 400 when bad request happens")
    void shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid request"))
                .andExpect(jsonPath("$.path").value("/test/bad-request"));
    }

    @Test
    @DisplayName("Should return 400 when validation fails")
    void shouldReturnBadRequestForValidationError() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("name")))
                .andExpect(jsonPath("$.path").value("/test/validation"));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/not-found")
        void throwNotFound() {
            throw new ResourceNotFoundException("Employee was not found");
        }

        @GetMapping("/test/conflict")
        void throwConflict() {
            throw new ConflictException("Employee already exists");
        }

        @GetMapping("/test/bad-request")
        void throwBadRequest() {
            throw new BadRequestException("Invalid request");
        }

        @PostMapping("/test/validation")
        void validateRequest(@Valid @RequestBody TestRequest request) {
        }
    }

    record TestRequest(
            @NotBlank(message = "Name is required")
            String name
    ) {
    }
}
