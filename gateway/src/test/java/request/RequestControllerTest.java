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

import java.util.Arrays;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class RequestControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    RequestClient client;
    ItemRequestDto dto;
    ItemRequestDto dto2;

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

}
