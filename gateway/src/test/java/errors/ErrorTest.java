package errors;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.exception.ErrorHandler;

import java.util.Map;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class ErrorTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidateShouldReturnMap() {
        ValidationException exception = new ValidationException("Validation failed");

        Map<String, String> response = errorHandler.handleValidate(exception);

        assertNotNull(response);
        assertEquals("Validation failed", response.get("ошибка"));
    }

    @Test
    void handleIllegalArgumentShouldReturnMap() {
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");

        Map<String, String> response = errorHandler.handleIllegalArgument(exception);

        assertNotNull(response);
        assertEquals("Illegal argument", response.get("ошибка"));
    }

    @Test
    void handleValidateWithEmptyMessageShouldReturnMap() {
        ValidationException exception = new ValidationException("");

        Map<String, String> response = errorHandler.handleValidate(exception);

        assertNotNull(response);
        assertEquals("", response.get("ошибка"));
    }

    @Test
    void handleIllegalArgumentWithEmptyMessageShouldReturnMap() {
        IllegalArgumentException exception = new IllegalArgumentException("");

        Map<String, String> response = errorHandler.handleIllegalArgument(exception);

        assertNotNull(response);
        assertEquals("", response.get("ошибка"));
    }

    @Test
    void handleValidateWithWhitespaceMessageShouldReturnMap() {
        ValidationException exception = new ValidationException("   ");

        Map<String, String> response = errorHandler.handleValidate(exception);

        assertNotNull(response);
        assertEquals("   ", response.get("ошибка"));
    }

    @Test
    void handleIllegalArgumentWithWhitespaceMessageShouldReturnMap() {
        IllegalArgumentException exception = new IllegalArgumentException("   ");

        Map<String, String> response = errorHandler.handleIllegalArgument(exception);

        assertNotNull(response);
        assertEquals("   ", response.get("ошибка"));
    }

    @Test
    void handleValidateWithSpecialCharactersShouldReturnMap() {
        ValidationException exception = new ValidationException("Error: !@#$%^&*()");

        Map<String, String> response = errorHandler.handleValidate(exception);

        assertNotNull(response);
        assertEquals("Error: !@#$%^&*()", response.get("ошибка"));
    }

    @Test
    void handleIllegalArgumentWithLongMessageShouldReturnMap() {
        String longMessage = "A".repeat(1000);
        IllegalArgumentException exception = new IllegalArgumentException(longMessage);

        Map<String, String> response = errorHandler.handleIllegalArgument(exception);

        assertNotNull(response);
        assertEquals(longMessage, response.get("ошибка"));
    }
}
