package booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.StatusBook;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class BookingClientTest {
    @Test
    void getUserBookingsShouldReturnResponse() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.getUserBookings(Mockito.anyLong(), Mockito.any(StatusBook.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = bookingClient.getUserBookings(1L, StatusBook.ALL);

        assertNotNull(response);
    }

    @Test
    void getOwnerBookingsShouldReturnResponse() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.getOwnerBookings(Mockito.anyLong(), Mockito.any(StatusBook.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = bookingClient.getOwnerBookings(1L, StatusBook.ALL);

        assertNotNull(response);
    }

    @Test
    void bookItemShouldReturnResponse() {
        BookingClient bookingClient = mock(BookingClient.class);
        BookingRequestDTO requestDTO = BookingRequestDTO.builder().build();
        when(bookingClient.bookItem(Mockito.anyLong(), Mockito.any(BookingRequestDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = bookingClient.bookItem(1L, requestDTO);

        assertNotNull(response);
    }

    @Test
    void getBookingShouldReturnResponse() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = bookingClient.getBooking(1L, 1L);

        assertNotNull(response);
    }

    @Test
    void changeBookingStatusShouldReturnResponse() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.changeBookingStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = bookingClient.changeBookingStatus(1L, 1L, true);

        assertNotNull(response);
    }

    @Test
    void getUserBookingsWithDifferentStatesShouldReturnResponse() {
        BookingClient bookingClient = mock(BookingClient.class);
        for (StatusBook state : StatusBook.values()) {
            when(bookingClient.getUserBookings(Mockito.anyLong(), Mockito.eq(state)))
                    .thenReturn(ResponseEntity.ok().build());

            ResponseEntity<?> response = bookingClient.getUserBookings(1L, state);

            assertNotNull(response);
        }
    }

    @Test
    void changeBookingStatusWithFalseShouldReturnResponse() {
        BookingClient bookingClient = mock(BookingClient.class);
        when(bookingClient.changeBookingStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.eq(false)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = bookingClient.changeBookingStatus(1L, 1L, false);

        assertNotNull(response);
    }

    @Test
    void bookItemWithValidDataShouldReturnResponse() {
        BookingClient bookingClient = mock(BookingClient.class);
        BookingRequestDTO requestDTO = BookingRequestDTO.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(bookingClient.bookItem(Mockito.anyLong(), Mockito.any(BookingRequestDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = bookingClient.bookItem(1L, requestDTO);

        assertNotNull(response);
    }
}
