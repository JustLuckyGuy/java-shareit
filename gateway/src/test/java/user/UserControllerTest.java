package user;

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
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDTO;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserClient client;
    UserDTO dto;

    @BeforeEach
    void before() {
        dto = UserDTO.builder()
                .name("Shrek")
                .email("shrekIsLive@gmail.com")
                .build();
    }

    @Test
    void testPost() throws Exception {
        Mockito.when(client.saveUser(Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("name", is(dto.getName())))
                .andExpect(jsonPath("email", is(dto.getEmail())));

        Mockito.verify(client, Mockito.times(1))
                .saveUser(dto);
    }

    @Test
    void testGetUser() throws Exception {
        Mockito.when(client.getUser(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(get("/users/3")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("name", is(dto.getName())))
                .andExpect(jsonPath("email", is(dto.getEmail())));

        Mockito.verify(client, Mockito.times(1))
                .getUser(3);
    }

    @Test
    void testUpdateUser() throws Exception {
        dto.setName("Donkey");
        Mockito.when(client.updateUser(Mockito.anyLong(), Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(patch("/users/33")
                        .content(mapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(jsonPath("name", is(dto.getName())))
                .andExpect(jsonPath("email", is(dto.getEmail())));

        Mockito.verify(client, Mockito.times(1))
                .updateUser(33, dto);
    }

    @Test
    void testDeleteUser() throws Exception {
        Mockito.when(client.deleteUser(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete("/users/333")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
        );

        Mockito.verify(client, Mockito.times(1))
                .deleteUser(333);
    }
}
