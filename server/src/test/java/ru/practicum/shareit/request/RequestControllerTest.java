package ru.practicum.shareit.request;

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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private BookingService bookingService;

    private ItemRequestDto itemRequestDto;
    private ItemRequestDto responseItemRequestDto;
    private final Long userId = 1L;
    private final Long requestId = 1L;

    @BeforeEach
    void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .description("Need a drill for home renovation")
                .build();

        responseItemRequestDto = ItemRequestDto.builder()
                .id(requestId)
                .description("Need a drill for home renovation")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void postRequestShouldCreateRequest() throws Exception {
        Mockito.when(itemRequestService.createRequest(Mockito.any(ItemRequestDto.class), eq(userId)))
                .thenReturn(responseItemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestId.intValue())))
                .andExpect(jsonPath("$.description", is("Need a drill for home renovation")));

        Mockito.verify(itemRequestService).createRequest(Mockito.any(ItemRequestDto.class), eq(userId));
    }

    @Test
    void postRequestWithMissingUserIdShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getUserRequestsShouldReturnUserRequests() throws Exception {
        List<ItemRequestDto> requests = List.of(responseItemRequestDto);

        Mockito.when(itemRequestService.getUserRequests(userId))
                .thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestId.intValue())))
                .andExpect(jsonPath("$[0].description", is("Need a drill for home renovation")));

        Mockito.verify(itemRequestService).getUserRequests(userId);
    }

    @Test
    void getUserRequestsWithMissingUserIdShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRequestsShouldReturnAllRequests() throws Exception {
        List<ItemRequestDto> requests = List.of(responseItemRequestDto);

        Mockito.when(itemRequestService.getAllRequests(userId))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestId.intValue())));

        Mockito.verify(itemRequestService).getAllRequests(userId);
    }

    @Test
    void getAllRequestsWithMissingUserIdShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRequestsWithPaginationShouldWork() throws Exception {
        List<ItemRequestDto> requests = List.of(responseItemRequestDto);

        Mockito.when(itemRequestService.getAllRequests(userId))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        Mockito.verify(itemRequestService).getAllRequests(userId);
    }

    @Test
    void getRequestByIdShouldReturnRequest() throws Exception {
        Mockito.when(itemRequestService.getRequestById(requestId))
                .thenReturn(responseItemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestId.intValue())))
                .andExpect(jsonPath("$.description", is("Need a drill for home renovation")));

        Mockito.verify(itemRequestService).getRequestById(requestId);
    }


    @Test
    void getUserRequestsWithNoRequestsShouldReturnEmptyList() throws Exception {
        Mockito.when(itemRequestService.getUserRequests(userId))
                .thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        Mockito.verify(itemRequestService).getUserRequests(userId);
    }

    @Test
    void getAllRequestsWithNoRequestsShouldReturnEmptyList() throws Exception {
        Mockito.when(itemRequestService.getAllRequests(userId))
                .thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        Mockito.verify(itemRequestService).getAllRequests(userId);
    }


    @Test
    void getRequestByIdWithInvalidIdShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/invalid"))
                .andExpect(status().isBadRequest());
    }
}
