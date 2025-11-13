package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.StatusBook;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class BookingClientTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    private BookingClient bookingClient;
    private static final String BASE_URL = "http://test-server";
    private static final String API_PREFIX = "/bookings";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(BASE_URL + API_PREFIX);
        when(builder.uriTemplateHandler(Mockito.any(UriTemplateHandler.class)))
                .thenReturn(builder);
        when(builder.requestFactory(Mockito.any(Supplier.class)))
                .thenReturn(builder);
        when(builder.build())
                .thenReturn(restTemplate);
        builder.uriTemplateHandler(uriBuilderFactory);

        bookingClient = new BookingClient(BASE_URL, builder);

        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body("mock-response");
        Mockito.lenient().when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.any(HttpMethod.class),
                Mockito.any(),
                Mockito.eq(Object.class),
                Mockito.any(Map.class)
        )).thenReturn(mockResponse);

        Mockito.lenient().when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.any(HttpMethod.class),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(mockResponse);
    }

    @Test
    void getUserBookingsShouldCallGetWithStateParameterAndUserIdHeader() {
        long userId = 1L;
        StatusBook state = StatusBook.ALL;

        bookingClient.getUserBookings(userId, state);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("?state={state}"),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) ->
                        params.containsKey("state") && params.get("state").equals("ALL"))
        );
    }

    @Test
    void getUserBookingsWithDifferentStatesShouldCallGetWithCorrectStateParameters() {
        long userId = 1L;

        for (StatusBook state : StatusBook.values()) {
            bookingClient.getUserBookings(userId, state);

            Mockito.verify(restTemplate).exchange(
                    Mockito.eq("?state={state}"),
                    Mockito.eq(HttpMethod.GET),
                    Mockito.argThat((HttpEntity<?> entity) ->
                            Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                    Mockito.eq(Object.class),
                    Mockito.argThat((Map<String, Object> params) ->
                            params.containsKey("state") && params.get("state").equals(state.name()))
            );
        }
    }

    @Test
    void getOwnerBookingsShouldCallGetWithOwnerPathAndStateParameter() {
        long ownerId = 1L;
        StatusBook state = StatusBook.CURRENT;

        bookingClient.getOwnerBookings(ownerId, state);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/owner?state={state}"),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) ->
                        params.containsKey("state") && params.get("state").equals("CURRENT"))
        );
    }

    @Test
    void bookItemShouldCallPostWithBookingDataAndUserIdHeader() {
        long userId = 1L;
        BookingRequestDTO requestDTO = BookingRequestDTO.builder()
                .itemId(123L)
                .start(LocalDateTime.of(2023, 10, 1, 10, 0))
                .end(LocalDateTime.of(2023, 10, 2, 10, 0))
                .build();

        bookingClient.bookItem(userId, requestDTO);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == requestDTO &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class));
    }

    @Test
    void bookItemWithValidDataShouldCallPostWithCompleteBookingData() {
        long userId = 1L;
        BookingRequestDTO requestDTO = BookingRequestDTO.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(StatusBook.WAITING)
                .build();

        bookingClient.bookItem(userId, requestDTO);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == requestDTO &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class));
    }

    @Test
    void getBookingShouldCallGetWithBookingIdPathAndUserIdHeader() {
        long userId = 1L;
        Long bookingId = 123L;

        bookingClient.getBooking(userId, bookingId);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123"),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class));
    }

    @Test
    void getBookingWithDifferentIdsShouldCallGetWithCorrectPaths() {
        long userId = 1L;


        bookingClient.getBooking(userId, 1L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/1"),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class));

        bookingClient.getBooking(userId, 999L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/999"),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class));
    }

    @Test
    void changeBookingStatusShouldCallPatchWithApprovedParameterAndOwnerIdHeader() {
        long ownerId = 1L;
        long bookingId = 123L;
        boolean approved = true;

        bookingClient.changeBookingStatus(ownerId, bookingId, approved);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123?approved={approved}"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) ->
                        params.containsKey("approved") && params.get("approved").equals(true))
        );
    }

    @Test
    void changeBookingStatusWithFalseShouldCallPatchWithFalseParameter() {
        long ownerId = 1L;
        Long bookingId = 123L;
        boolean approved = false;

        bookingClient.changeBookingStatus(ownerId, bookingId, approved);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123?approved={approved}"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) ->
                        params.containsKey("approved") && params.get("approved").equals(false))
        );
    }

    @Test
    void changeBookingStatusWithDifferentBookingIdsShouldCallPatchWithCorrectPaths() {
        long ownerId = 1L;

        bookingClient.changeBookingStatus(ownerId, 1L, true);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/1?approved={approved}"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) -> params.get("approved").equals(true))
        );

        bookingClient.changeBookingStatus(ownerId, 456L, false);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/456?approved={approved}"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) -> params.get("approved").equals(false))
        );
    }

    @Test
    void changeBookingStatusWithDifferentOwnersShouldCallPatchWithCorrectUserHeaders() {
        Long bookingId = 123L;

        bookingClient.changeBookingStatus(1L, bookingId, true);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123?approved={approved}"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) -> params.get("approved").equals(true))
        );

        bookingClient.changeBookingStatus(2L, bookingId, false);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123?approved={approved}"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "2")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) -> params.get("approved").equals(false))
        );
    }

    @Test
    void getUserBookingsWithDifferentUsersShouldCallGetWithCorrectUserHeaders() {
        StatusBook state = StatusBook.ALL;

        bookingClient.getUserBookings(1L, state);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("?state={state}"),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) -> params.get("state").equals("ALL"))
        );

        bookingClient.getUserBookings(2L, state);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("?state={state}"),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) -> Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "2")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) -> params.get("state").equals("ALL"))
        );
    }

    @Test
    void bookItemWithStatusShouldCallPostWithStatusInBody() {
        long userId = 1L;
        BookingRequestDTO requestDTO = BookingRequestDTO.builder()
                .itemId(123L)
                .start(LocalDateTime.of(2023, 10, 1, 10, 0))
                .end(LocalDateTime.of(2023, 10, 2, 10, 0))
                .build();

        bookingClient.bookItem(userId, requestDTO);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == requestDTO &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getOwnerBookingsWithApprovedStateShouldCallGetWithApprovedParameter() {
        long ownerId = 1L;
        StatusBook state = StatusBook.APPROVED;

        bookingClient.getOwnerBookings(ownerId, state);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/owner?state={state}"),
                Mockito.eq(HttpMethod.GET),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class),
                Mockito.argThat((Map<String, Object> params) ->
                        params.containsKey("state") && params.get("state").equals("APPROVED"))
        );
    }

    @Test
    void bookItemWithMinimalDataShouldCallPostWithRequiredFields() {
        long userId = 1L;
        BookingRequestDTO minimalRequest = BookingRequestDTO.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 10, 1, 10, 0))
                .end(LocalDateTime.of(2023, 10, 2, 10, 0))
                .build();

        bookingClient.bookItem(userId, minimalRequest);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == minimalRequest &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class));
    }
}
