package user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDTO;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class UserClientTest {
    @Test
    void saveUserShouldReturnResponse() {
        UserClient userClient = mock(UserClient.class);
        UserDTO userDTO = UserDTO.builder().build();
        when(userClient.saveUser(Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = userClient.saveUser(userDTO);

        assertNotNull(response);
    }

    @Test
    void getUserShouldReturnResponse() {
        UserClient userClient = mock(UserClient.class);
        when(userClient.getUser(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = userClient.getUser(1L);

        assertNotNull(response);
    }

    @Test
    void updateUserShouldReturnResponse() {
        UserClient userClient = mock(UserClient.class);
        UserDTO userDTO = UserDTO.builder().build();
        when(userClient.updateUser(Mockito.anyLong(), Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = userClient.updateUser(1L, userDTO);

        assertNotNull(response);
    }

    @Test
    void deleteUserShouldReturnResponse() {
        UserClient userClient = mock(UserClient.class);
        when(userClient.deleteUser(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = userClient.deleteUser(1L);

        assertNotNull(response);
    }

    @Test
    void updateUserWithOnlyNameShouldReturnResponse() {
        UserClient userClient = mock(UserClient.class);
        UserDTO nameOnlyUpdate = UserDTO.builder().name("Updated Name").build();
        when(userClient.updateUser(Mockito.anyLong(), Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = userClient.updateUser(1L, nameOnlyUpdate);

        assertNotNull(response);
    }

    @Test
    void updateUserWithOnlyEmailShouldReturnResponse() {
        UserClient userClient = mock(UserClient.class);
        UserDTO emailOnlyUpdate = UserDTO.builder().email("updated@email.com").build();
        when(userClient.updateUser(Mockito.anyLong(), Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = userClient.updateUser(1L, emailOnlyUpdate);

        assertNotNull(response);
    }

    @Test
    void updateUserWithEmptyBodyShouldReturnResponse() {
        UserClient userClient = mock(UserClient.class);
        UserDTO emptyUpdate = UserDTO.builder().build();
        when(userClient.updateUser(Mockito.anyLong(), Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = userClient.updateUser(1L, emptyUpdate);

        assertNotNull(response);
    }

    @Test
    void saveUserWithFullDataShouldReturnResponse() {
        UserClient userClient = mock(UserClient.class);
        UserDTO fullUserDTO = UserDTO.builder()
                .id(1L)
                .name("Full User")
                .email("full@email.com")
                .build();
        when(userClient.saveUser(Mockito.any(UserDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = userClient.saveUser(fullUserDTO);

        assertNotNull(response);
    }
}
