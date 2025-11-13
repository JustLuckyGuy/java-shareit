package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.dto.StatusBook;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingClient client;
    private BookingRequestDTO dto;
    private BookingRequestDTO dto2;
    private BookingRequestDTO validBookingDto;

    @BeforeEach
    void before() {
        dto = BookingRequestDTO.builder()
                .status(StatusBook.REJECTED)
                .itemId(333)
                .build();

        dto2 = BookingRequestDTO.builder()
                .status(StatusBook.WAITING)
                .itemId(31213)
                .build();

        validBookingDto = BookingRequestDTO.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void testPost() throws Exception {
        Mockito.when(client.bookItem(Mockito.anyLong(), Mockito.any(BookingRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("status", is(dto.getStatus().toString())))
                .andExpect(jsonPath("itemId", is((int) dto.getItemId())));

        Mockito.verify(client, Mockito.times(1))
                .bookItem(1, dto);
    }

    @Test
    void testGetBookingById() throws Exception {
        Mockito.when(client.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(dto2));

        mvc.perform(get("/bookings/343")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 123)
                ).andExpect(jsonPath("status", is(dto2.getStatus().toString())))
                .andExpect(jsonPath("itemId", is((int) dto2.getItemId())));

        Mockito.verify(client, Mockito.times(1))
                .getBooking(123, (long) 343);
    }

    @Test
    void testGetUserBookings() throws Exception {
        Mockito.when(client.getUserBookings(Mockito.anyLong(), Mockito.any(StatusBook.class)))
                .thenReturn(ResponseEntity.ok(Arrays.asList(dto, dto2)));

        mvc.perform(get("/bookings?state=future")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 123)
                ).andExpect(jsonPath("[0].status", is(dto.getStatus().toString())))
                .andExpect(jsonPath("[1]itemId", is((int) dto2.getItemId())));

        Mockito.verify(client, Mockito.times(1))
                .getUserBookings(123, StatusBook.FUTURE);
    }

    @Test
    void testGetOwnerBookings() throws Exception {
        Mockito.when(client.getOwnerBookings(Mockito.anyLong(), Mockito.any(StatusBook.class)))
                .thenReturn(ResponseEntity.ok(Arrays.asList(dto, dto2)));

        mvc.perform(get("/bookings/owner?state=past")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 776)
                ).andExpect(jsonPath("[1].status", is(dto2.getStatus().toString())))
                .andExpect(jsonPath("[0]itemId", is((int) dto.getItemId())));

        Mockito.verify(client, Mockito.times(1))
                .getOwnerBookings(776, StatusBook.PAST);
    }

    @Test
    void patchBooking() throws Exception {
        dto.setStatus(StatusBook.APPROVED);
        Mockito.when(client.changeBookingStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(patch("/bookings/43?approved=true")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 98)
        ).andExpect(jsonPath("status", is(dto.getStatus().toString())));

        Mockito.verify(client, Mockito.times(1))
                .changeBookingStatus(98, (long) 43, true);
    }

    @Test
    void testGetUserBookingsWithEmptyList() throws Exception {
        Mockito.when(client.getUserBookings(Mockito.anyLong(), Mockito.any(StatusBook.class)))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/bookings?state=all")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void testGetUserBookingsWithDefaultState() throws Exception {
        Mockito.when(client.getUserBookings(Mockito.anyLong(), Mockito.eq(StatusBook.ALL)))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(dto)));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk());

        Mockito.verify(client, Mockito.times(1))
                .getUserBookings(123, StatusBook.ALL);
    }

    @Test
    void testChangeBookingStatusWithFalse() throws Exception {
        Mockito.when(client.changeBookingStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(patch("/bookings/43?approved=false")
                        .header("X-Sharer-User-Id", 98))
                .andExpect(status().isOk());

        Mockito.verify(client, Mockito.times(1))
                .changeBookingStatus(98, 43L, false);
    }

    @Test
    void testGetBookingWithZeroId() throws Exception {
        Mockito.when(client.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(get("/bookings/0")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk());
    }

    @Test
    void testPostWithEndInPast() throws Exception {
        BookingRequestDTO invalidDto = BookingRequestDTO.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithStartInPast() throws Exception {
        BookingRequestDTO invalidDto = BookingRequestDTO.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithNegativeItemId() throws Exception {
        BookingRequestDTO invalidDto = BookingRequestDTO.builder()
                .itemId(-1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithInvalidBookingDto() throws Exception {
        BookingRequestDTO invalidDto = BookingRequestDTO.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithoutUserIdHeader() throws Exception {
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validBookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserBookingsWithInvalidState() throws Exception {
        mvc.perform(get("/bookings?state=invalid")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetOwnerBookingsWithInvalidState() throws Exception {
        mvc.perform(get("/bookings/owner?state=invalid")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetUserBookings_WithDifferentStates() throws Exception {
        for (StatusBook state : StatusBook.values()) {
            Mockito.when(client.getUserBookings(Mockito.anyLong(), Mockito.eq(state)))
                    .thenReturn(ResponseEntity.ok(Collections.emptyList()));

            mvc.perform(get("/bookings?state=" + state.name().toLowerCase())
                            .header("X-Sharer-User-Id", 123))
                    .andExpect(status().isOk());

            Mockito.verify(client, Mockito.times(1))
                    .getUserBookings(123, state);
            Mockito.reset(client);
        }
    }

    @Test
    void testChangeBookingStatus_WithDifferentParameters() throws Exception {
        for (boolean approved : new boolean[]{true, false}) {
            Mockito.when(client.changeBookingStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.eq(approved)))
                    .thenReturn(ResponseEntity.ok(dto));

            mvc.perform(patch("/bookings/1?approved=" + approved)
                            .header("X-Sharer-User-Id", 1))
                    .andExpect(status().isOk());

            Mockito.verify(client, Mockito.times(1))
                    .changeBookingStatus(1, 1L, approved);
            Mockito.reset(client);
        }
    }
}
