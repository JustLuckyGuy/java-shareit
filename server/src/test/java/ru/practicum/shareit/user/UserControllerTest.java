package ru.practicum.shareit.user;

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
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemRequestService itemRequestService;

    private UserDTO userDto;
    private UserDTO responseUserDto;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        userDto = UserDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        responseUserDto = UserDTO.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void allUsersShouldReturnAllUsers() throws Exception {
        List<UserDTO> users = List.of(responseUserDto);

        Mockito.when(userService.allUsers())
                .thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userId.intValue())))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")));

        Mockito.verify(userService).allUsers();
    }

    @Test
    void allUsersWhenNoUsersShouldReturnEmptyList() throws Exception {
        Mockito.when(userService.allUsers())
                .thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        Mockito.verify(userService).allUsers();
    }

    @Test
    void getUserShouldReturnUser() throws Exception {
        Mockito.when(userService.userById(userId))
                .thenReturn(responseUserDto);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        Mockito.verify(userService).userById(userId);
    }


    @Test
    void saveUserShouldCreateUser() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(UserDTO.class)))
                .thenReturn(responseUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        Mockito.verify(userService).createUser(Mockito.any(UserDTO.class));
    }


    @Test
    void updateUserShouldUpdateUser() throws Exception {
        UserDTO updatedUser = UserDTO.builder()
                .id(userId)
                .name("John Updated")
                .email("john.updated@example.com")
                .build();

        Mockito.when(userService.updateUser(eq(userId), Mockito.any(UserDTO.class)))
                .thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.email", is("john.updated@example.com")));

        Mockito.verify(userService).updateUser(eq(userId), Mockito.any(UserDTO.class));
    }

    @Test
    void updateUserWithPartialDataShouldWork() throws Exception {
        UserDTO partialUpdate = UserDTO.builder()
                .name("Only Name Updated")
                .build();

        UserDTO updatedUser = UserDTO.builder()
                .id(userId)
                .name("Only Name Updated")
                .email("john.doe@example.com")
                .build();

        Mockito.when(userService.updateUser(eq(userId), Mockito.any(UserDTO.class)))
                .thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Only Name Updated")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        Mockito.verify(userService).updateUser(eq(userId), Mockito.any(UserDTO.class));
    }


    @Test
    void removeUserShouldDeleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteUser(userId);
    }


    @Test
    void getUserWithInvalidIdFormatShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/users/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithInvalidIdFormatShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/users/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeUserWithInvalidIdFormatShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/users/invalid"))
                .andExpect(status().isBadRequest());
    }
}
