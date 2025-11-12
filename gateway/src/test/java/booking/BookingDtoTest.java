package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.StatusBook;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
class BookingDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeBookingRequestDTO() throws Exception {
        BookingRequestDTO bookingRequest = BookingRequestDTO.builder()
                .itemId(1L)
                .status(StatusBook.WAITING)
                .start(LocalDateTime.of(2023, 10, 1, 10, 0))
                .end(LocalDateTime.of(2023, 10, 2, 10, 0))
                .build();

        String json = objectMapper.writeValueAsString(bookingRequest);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"status\":\"WAITING\"");
        assertThat(json).contains("\"start\":\"2023-10-01T10:00:00\"");
        assertThat(json).contains("\"end\":\"2023-10-02T10:00:00\"");
    }

    @Test
    void shouldDeserializeBookingRequestDTO() throws Exception {
        String json = """
                {
                    "itemId": 1,
                    "status": "APPROVED",
                    "start": "2023-10-01T10:00:00",
                    "end": "2023-10-02T10:00:00"
                }
                """;

        BookingRequestDTO bookingRequest = objectMapper.readValue(json, BookingRequestDTO.class);

        assertThat(bookingRequest.getItemId()).isEqualTo(1L);
        assertThat(bookingRequest.getStatus()).isEqualTo(StatusBook.APPROVED);
        assertThat(bookingRequest.getStart()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(bookingRequest.getEnd()).isEqualTo(LocalDateTime.of(2023, 10, 2, 10, 0));
    }

    @Test
    void shouldDeserializeWithoutStatus() throws Exception {
        String json = """
                {
                    "itemId": 1,
                    "start": "2023-10-01T10:00:00",
                    "end": "2023-10-02T10:00:00"
                }
                """;

        BookingRequestDTO bookingRequest = objectMapper.readValue(json, BookingRequestDTO.class);

        assertThat(bookingRequest.getItemId()).isEqualTo(1L);
        assertThat(bookingRequest.getStatus()).isNull();
        assertThat(bookingRequest.getStart()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(bookingRequest.getEnd()).isEqualTo(LocalDateTime.of(2023, 10, 2, 10, 0));
    }

    @Test
    void shouldHandleNullFields() throws Exception {
        String json = """
                {
                    "itemId": 1
                }
                """;

        BookingRequestDTO bookingRequest = objectMapper.readValue(json, BookingRequestDTO.class);

        assertThat(bookingRequest.getItemId()).isEqualTo(1L);
        assertThat(bookingRequest.getStatus()).isNull();
        assertThat(bookingRequest.getStart()).isNull();
        assertThat(bookingRequest.getEnd()).isNull();
    }

    @Test
    void shouldSerializeWithAllStatusValues() throws Exception {
        for (StatusBook status : StatusBook.values()) {
            BookingRequestDTO bookingRequest = BookingRequestDTO.builder()
                    .itemId(1L)
                    .status(status)
                    .start(LocalDateTime.of(2023, 10, 1, 10, 0))
                    .end(LocalDateTime.of(2023, 10, 2, 10, 0))
                    .build();

            String json = objectMapper.writeValueAsString(bookingRequest);
            BookingRequestDTO deserialized = objectMapper.readValue(json, BookingRequestDTO.class);

            assertThat(deserialized.getStatus()).isEqualTo(status);
        }
    }

    @Test
    void shouldHandleFutureDates() throws Exception {
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(2);

        BookingRequestDTO bookingRequest = BookingRequestDTO.builder()
                .itemId(1L)
                .start(futureStart)
                .end(futureEnd)
                .build();

        String json = objectMapper.writeValueAsString(bookingRequest);
        BookingRequestDTO deserialized = objectMapper.readValue(json, BookingRequestDTO.class);

        assertThat(deserialized.getStart()).isEqualTo(futureStart);
        assertThat(deserialized.getEnd()).isEqualTo(futureEnd);
    }
}
