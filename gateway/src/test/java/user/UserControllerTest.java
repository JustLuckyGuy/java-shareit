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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserClient client;
    private UserDTO dto;

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

    @Test
    void testPostWithEmptyName() throws Exception {
        UserDTO invalidDto = UserDTO.builder()
                .name("")
                .email("valid@email.com")
                .build();

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithBlankName() throws Exception {
        UserDTO invalidDto = UserDTO.builder()
                .name("   ")
                .email("valid@email.com")
                .build();

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithInvalidEmail() throws Exception {
        UserDTO invalidDto = UserDTO.builder()
                .name("Valid Name")
                .email("invalid-email")
                .build();

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithEmptyEmail() throws Exception {
        UserDTO invalidDto = UserDTO.builder()
                .name("Valid Name")
                .email("")
                .build();

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testGetUserWithZeroId() throws Exception {
        mvc.perform(get("/users/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserWithNegativeId() throws Exception {
        mvc.perform(get("/users/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUserWithZeroId() throws Exception {
        mvc.perform(delete("/users/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUserWithNegativeId() throws Exception {
        mvc.perform(delete("/users/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserOnlyName() throws Exception {
        UserDTO updateDto = UserDTO.builder()
                .name("Updated Name")
                .build();

        UserDTO expectedDto = UserDTO.builder()
                .name("Updated Name")
                .email(dto.getEmail())
                .build();

        Mockito.when(client.updateUser(Mockito.anyLong(), Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(expectedDto));

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Updated Name")));
    }

    @Test
    void testUpdateUserOnlyEmail() throws Exception {
        UserDTO updateDto = UserDTO.builder()
                .email("updated@email.com")
                .build();

        UserDTO expectedDto = UserDTO.builder()
                .name(dto.getName())
                .email("updated@email.com")
                .build();

        Mockito.when(client.updateUser(Mockito.anyLong(), Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(expectedDto));

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email", is("updated@email.com")));
    }

    @Test
    void testUpdateUserWithEmptyBody() throws Exception {
        Mockito.when(client.updateUser(Mockito.anyLong(), Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testPostWithVeryLongName() throws Exception {
        String longName = "A".repeat(1000);
        UserDTO longNameDto = UserDTO.builder()
                .name(longName)
                .email("test@email.com")
                .build();

        Mockito.when(client.saveUser(Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(longNameDto));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(longNameDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(longName)));
    }

    @Test
    void testPostWithSpecialCharactersInName() throws Exception {
        UserDTO specialDto = UserDTO.builder()
                .name("Shrek O'Neil Jr.")
                .email("test@email.com")
                .build();

        Mockito.when(client.saveUser(Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(specialDto));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(specialDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Shrek O'Neil Jr.")));
    }

    @Test
    void testPostWithComplexEmail() throws Exception {
        UserDTO complexEmailDto = UserDTO.builder()
                .name("Test User")
                .email("test.user+tag@sub.domain.co.uk")
                .build();

        Mockito.when(client.saveUser(Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(complexEmailDto));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(complexEmailDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email", is("test.user+tag@sub.domain.co.uk")));
    }

    @Test
    void testPostWithNullBody() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostWithFullDto() throws Exception {
        UserDTO fullDto = UserDTO.builder()
                .id(123L)
                .name("Full User")
                .email("full@email.com")
                .build();

        Mockito.when(client.saveUser(Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(fullDto));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(fullDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("Full User")))
                .andExpect(jsonPath("email", is("full@email.com")));

        Mockito.verify(client, Mockito.times(1))
                .saveUser(fullDto);
    }

    @Test
    void testMultipleOperations() throws Exception {
        Mockito.when(client.saveUser(Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.when(client.getUser(1L))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        Mockito.when(client.updateUser(Mockito.anyLong(), Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok(dto));

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.when(client.deleteUser(1L))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(client, Mockito.times(1)).saveUser(dto);
        Mockito.verify(client, Mockito.times(1)).getUser(1L);
        Mockito.verify(client, Mockito.times(1)).updateUser(1L, dto);
        Mockito.verify(client, Mockito.times(1)).deleteUser(1L);
    }
}
