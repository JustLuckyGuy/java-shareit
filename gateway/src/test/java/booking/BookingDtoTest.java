package booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.StatusBook;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingRequestDTO> json;

    @Test
    void shouldSerializeBookingRequestDTO() throws Exception {
        BookingRequestDTO bookingRequest = BookingRequestDTO.builder()
                .itemId(1L)
                .status(StatusBook.WAITING)
                .start(LocalDateTime.of(2023, 10, 1, 10, 0))
                .end(LocalDateTime.of(2023, 10, 2, 10, 0))
                .build();

        JsonContent<BookingRequestDTO> result = json.write(bookingRequest);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-10-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-10-02T10:00:00");
    }

    @Test
    void shouldDeserializeBookingRequestDTO() throws Exception {
        String content = "{\"itemId\":1,\"status\":\"APPROVED\",\"start\":\"2023-10-01T10:00:00\",\"end\":\"2023-10-02T10:00:00\"}";

        BookingRequestDTO bookingRequest = json.parseObject(content);

        assertThat(bookingRequest.getItemId()).isEqualTo(1L);
        assertThat(bookingRequest.getStatus()).isEqualTo(StatusBook.APPROVED);
        assertThat(bookingRequest.getStart()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(bookingRequest.getEnd()).isEqualTo(LocalDateTime.of(2023, 10, 2, 10, 0));
    }

    @Test
    void shouldDeserializeWithoutStatus() throws Exception {
        String content = "{\"itemId\":1,\"start\":\"2023-10-01T10:00:00\",\"end\":\"2023-10-02T10:00:00\"}";

        BookingRequestDTO bookingRequest = json.parseObject(content);

        assertThat(bookingRequest.getItemId()).isEqualTo(1L);
        assertThat(bookingRequest.getStatus()).isNull();
        assertThat(bookingRequest.getStart()).isEqualTo(LocalDateTime.of(2023, 10, 1, 10, 0));
        assertThat(bookingRequest.getEnd()).isEqualTo(LocalDateTime.of(2023, 10, 2, 10, 0));
    }

    @Test
    void shouldHandleNullFields() throws Exception {
        String content = "{\"itemId\":1}";

        BookingRequestDTO bookingRequest = json.parseObject(content);

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

            JsonContent<BookingRequestDTO> result = json.write(bookingRequest);
            assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(status.toString());
        }
    }

    @Test
    void shouldHandleFutureDates() throws Exception {
        LocalDateTime futureStart = LocalDateTime.of(2023, 12, 1, 10, 0);
        LocalDateTime futureEnd = LocalDateTime.of(2023, 12, 2, 10, 0);

        BookingRequestDTO bookingRequest = BookingRequestDTO.builder()
                .itemId(1L)
                .start(futureStart)
                .end(futureEnd)
                .build();

        JsonContent<BookingRequestDTO> result = json.write(bookingRequest);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-12-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-02T10:00:00");
    }
}
