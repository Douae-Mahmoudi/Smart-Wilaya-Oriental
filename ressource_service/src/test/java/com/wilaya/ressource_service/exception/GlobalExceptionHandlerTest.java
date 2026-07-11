package com.wilaya.ressource_service.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, GlobalExceptionHandlerTest.TestController.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldHandleRessourceNonTrouveeException() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void shouldHandleIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/illegal-arg"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Invalid argument"));
    }

    @Test
    void shouldHandleIllegalStateException() throws Exception {
        mockMvc.perform(get("/test/illegal-state"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Invalid state"));
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() throws Exception {
        String invalidJson = "{}";
        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erreurs.name").value("must not be blank"));
    }

    @Test
    void shouldHandleConstraintViolationException() throws Exception {
        mockMvc.perform(get("/test/constraint-violation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Constraint violation"));
    }

    @Test
    void shouldHandleObjectOptimisticLockingFailureException() throws Exception {
        mockMvc.perform(get("/test/optimistic-lock"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Cette ressource a été modifiée entre-temps, veuillez réessayer"));
    }

    @Test
    void shouldHandleGenericException() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Une erreur inattendue est survenue"));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/not-found")
        public void notFound() {
            throw new RessourceNonTrouveeException("Resource not found");
        }

        @GetMapping("/illegal-arg")
        public void illegalArg() {
            throw new IllegalArgumentException("Invalid argument");
        }

        @GetMapping("/illegal-state")
        public void illegalState() {
            throw new IllegalStateException("Invalid state");
        }

        @PostMapping("/validate")
        public void validate(@Valid @RequestBody TestDto dto) {
        }

        @GetMapping("/constraint-violation")
        public void constraintViolation() {
            throw new jakarta.validation.ConstraintViolationException("Constraint violation", null);
        }

        @GetMapping("/optimistic-lock")
        public void optimisticLock() {
            throw new ObjectOptimisticLockingFailureException("Optimistic lock", new Exception());
        }

        @GetMapping("/generic")
        public void generic() {
            throw new RuntimeException("Unexpected");
        }

        static class TestDto {
            @NotBlank(message = "must not be blank")
            private String name;
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }
}