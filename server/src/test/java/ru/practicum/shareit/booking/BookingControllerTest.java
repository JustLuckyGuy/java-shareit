package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private ResponseBookingDto responseBookingDto;
    private final Long userId = 1L;
    private final Long bookingId = 1L;
    private final Long ownerId = 2L;

    @BeforeEach
    void setUp() {
        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        responseBookingDto = ResponseBookingDto.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(StatusBook.WAITING)
                .build();
    }

    @Test
    void bookItemShouldCreateBooking() throws Exception {
        Mockito.when(bookingService.bookItem(Mockito.eq(userId), Mockito.any(BookingDto.class)))
                .thenReturn(responseBookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId.intValue())))
                .andExpect(jsonPath("$.status", is("WAITING")));

        Mockito.verify(bookingService).bookItem(Mockito.eq(userId), Mockito.any(BookingDto.class));
    }

    @Test
    void changeBookStatusShouldUpdateBookingStatus() throws Exception {
        ResponseBookingDto updatedBooking = ResponseBookingDto.builder()
                .id(bookingId)
                .status(StatusBook.APPROVED)
                .build();

        Mockito.when(bookingService.changeBookStatus(ownerId, bookingId, true))
                .thenReturn(updatedBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId.intValue())))
                .andExpect(jsonPath("$.status", is("APPROVED")));

        Mockito.verify(bookingService).changeBookStatus(ownerId, bookingId, true);
    }

    @Test
    void getBookingShouldReturnBooking() throws Exception {
        Mockito.when(bookingService.getBooking(bookingId))
                .thenReturn(responseBookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId.intValue())))
                .andExpect(jsonPath("$.status", is("WAITING")));

        Mockito.verify(bookingService).getBooking(bookingId);
    }

    @Test
    void getUserBookingsShouldReturnUserBookings() throws Exception {
        List<ResponseBookingDto> bookings = List.of(responseBookingDto);

        Mockito.when(bookingService.getUserBookings(userId, "ALL"))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingId.intValue())));

        Mockito.verify(bookingService).getUserBookings(userId, "ALL");
    }

    @Test
    void getUserBookingsWithDefaultStateShouldReturnAllBookings() throws Exception {
        List<ResponseBookingDto> bookings = List.of(responseBookingDto);

        Mockito.when(bookingService.getUserBookings(userId, "all"))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        Mockito.verify(bookingService).getUserBookings(userId, "all");
    }

    @Test
    void getOwnerBookingsShouldReturnOwnerBookings() throws Exception {
        List<ResponseBookingDto> bookings = List.of(responseBookingDto);

        Mockito.when(bookingService.getOwnerBookings(ownerId, "FUTURE"))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingId.intValue())));

        Mockito.verify(bookingService).getOwnerBookings(ownerId, "FUTURE");
    }

    @Test
    void getOwnerBookingsWithDefaultStateShouldReturnAllBookings() throws Exception {
        List<ResponseBookingDto> bookings = List.of(responseBookingDto);

        Mockito.when(bookingService.getOwnerBookings(ownerId, "all"))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        Mockito.verify(bookingService).getOwnerBookings(ownerId, "all");
    }

    @Test
    void changeBookStatusWithDefaultApprovedShouldUseFalse() throws Exception {
        ResponseBookingDto rejectedBooking = ResponseBookingDto.builder()
                .id(bookingId)
                .status(StatusBook.REJECTED)
                .build();

        Mockito.when(bookingService.changeBookStatus(ownerId, bookingId, false))
                .thenReturn(rejectedBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")));

        Mockito.verify(bookingService).changeBookStatus(ownerId, bookingId, false);
    }

}
