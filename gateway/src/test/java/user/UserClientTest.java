package user;

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
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.function.Supplier;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserClientTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    private UserClient userClient;
    private static final String BASE_URL = "http://test-server";
    private static final String API_PREFIX = "/users";

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

        userClient = new UserClient(BASE_URL, builder);

        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body("mock-response");
        when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.any(HttpMethod.class),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(mockResponse);
    }

    @Test
    void getUserByIdShouldCallGetWithIdInPath() {
        long userId = 1L;
        userClient.getUser(userId);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/1"),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getAllUsersShouldCallGetWithEmptyPath() {
        userClient.getAllUsers();
        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void saveUserShouldCallPostWithUserData() {
        UserDTO newUserDto = UserDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        userClient.saveUser(newUserDto);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == newUserDto),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void updateUserShouldCallPatchWithUserIdAndUpdateData() {
        long userId = 1L;

        UserDTO updateUserDto = UserDTO.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();

        userClient.updateUser(userId, updateUserDto);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/1"),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.argThat((HttpEntity<?> entity) ->
                        entity.getBody() == updateUserDto),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void deleteUserShouldCallDeleteWithIdInPath() {
        long userId = 1L;
        userClient.deleteUser(userId);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/1"),
                Mockito.eq(HttpMethod.DELETE),
                Mockito.any(),
                Mockito.eq(Object.class)
        );
    }
}
