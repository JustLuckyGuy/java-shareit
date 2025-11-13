package request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.RequestClient;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class RequestControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private RequestClient client;
    private ItemRequestDto dto;
    private ItemRequestDto dto2;

    @BeforeEach
    void before() {
        dto = ItemRequestDto.builder()
                .description("Wanna shrexy Shrek")
                .build();

        dto2 = ItemRequestDto.builder()
                .description("So am I")
                .build();
    }

    @Test
    void testPost() throws Exception {
        Mockito.when(client.postRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(post("/requests")
                .header("X-Sharer-User-Id", 41)
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
        ).andExpect(jsonPath("description", is(dto.getDescription())));

        Mockito.verify(client, Mockito.times(1))
                .postRequest(41, dto);
    }

    @Test
    void testGetUserRequests() throws Exception {
        Mockito.when(client.getUserRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(Arrays.asList(dto, dto2)));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 14)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("[0]description", is(dto.getDescription())))
                .andExpect(jsonPath("[1]description", is(dto2.getDescription())));

        Mockito.verify(client, Mockito.times(1))
                .getUserRequests(14);
    }

    @Test
    void testGetAllRequests() throws Exception {
        dto.setDescription("adadadadadadadadadadad");
        Mockito.when(client.getAllRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(Arrays.asList(dto, dto2)));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 4)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("[0]description", is(dto.getDescription())))
                .andExpect(jsonPath("[1]description", is(dto2.getDescription())));

        Mockito.verify(client, Mockito.times(1))
                .getAllRequests(4);
    }

    @Test
    void testGetRequestById() throws Exception {
        dto2.setDescription("Vingardium leviossssAAAAAA");
        Mockito.when(client.getRequestById(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(get("/requests/379")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
        ).andExpect(jsonPath("description", is(dto.getDescription())));

        Mockito.verify(client, Mockito.times(1))
                .getRequestById(379);
    }

    @Test
    void testPostWithoutUserIdHeader() throws Exception {
        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserRequestsWithoutUserIdHeader() throws Exception {
        mvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllRequestsWithoutUserIdHeader() throws Exception {
        mvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithEmptyDescription() throws Exception {
        ItemRequestDto invalidDto = ItemRequestDto.builder()
                .description("")
                .build();

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithBlankDescription() throws Exception {
        ItemRequestDto invalidDto = ItemRequestDto.builder()
                .description("   ")
                .build();

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithNullDescription() throws Exception {
        String jsonWithoutDescription = "{\"someField\":\"value\"}";

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithoutDescription))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRequestByIdWithZeroId() throws Exception {
        Mockito.when(client.getRequestById(0L))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(get("/requests/0"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserRequestsWithEmptyList() throws Exception {
        Mockito.when(client.getUserRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void testGetAllRequestsWithEmptyList() throws Exception {
        Mockito.when(client.getAllRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void testPostWithVeryLongDescription() throws Exception {
        String longDescription = "A".repeat(510);
        ItemRequestDto longDto = ItemRequestDto.builder()
                .description(longDescription)
                .build();

        Mockito.when(client.postRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(longDto));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(longDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description", is(longDescription)));

        Mockito.verify(client, Mockito.times(1))
                .postRequest(1, longDto);
    }

    @Test
    void testPostWithSpecialCharacters() throws Exception {
        ItemRequestDto specialDto = ItemRequestDto.builder()
                .description("Special chars: !@#$%^&*()_+{}[]|:;<>,.?/~`")
                .build();

        Mockito.when(client.postRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(specialDto));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(specialDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description", is(specialDto.getDescription())));

        Mockito.verify(client, Mockito.times(1))
                .postRequest(1, specialDto);
    }

    @Test
    void testPostWithFullDto() throws Exception {
        ItemRequestDto fullDto = ItemRequestDto.builder()
                .id(123L)
                .description("Full DTO test")
                .created(java.time.LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        Mockito.when(client.postRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(fullDto));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(fullDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("description", is(fullDto.getDescription())));

        Mockito.verify(client, Mockito.times(1))
                .postRequest(1, fullDto);
    }

    @Test
    void testPostWithNullBody() throws Exception {
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
