package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.StatusBook;
import ru.practicum.shareit.client.BaseClient;

import java.util.List;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<List<BookingRequestDTO>> getUserBookings(long userId, StatusBook state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        return get("?state={state}", userId, parameters);
    }

    public ResponseEntity<List<BookingRequestDTO>> getOwnerBookings(long ownerId, StatusBook state) {
        Map<String, Object> parameters = Map.of("state", state.name());
        return get("/owner?state={state}", ownerId, parameters);
    }

    public ResponseEntity<BookingRequestDTO> bookItem(long userId, BookingRequestDTO requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<BookingRequestDTO> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<BookingRequestDTO> changeBookingStatus(long ownerId, Long bookingId, boolean approved) {
        Map<String, Object> params = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", ownerId, params, null);
    }

}
