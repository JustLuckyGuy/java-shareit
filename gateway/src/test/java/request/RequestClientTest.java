package request;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Objects;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class RequestClientTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RestTemplateBuilder builder;

    private RequestClient requestClient;
    private static final String BASE_URL = "http://test-server";
    private static final String API_PREFIX = "/requests";
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

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

        requestClient = new RequestClient(BASE_URL, builder);

        ResponseEntity<Object> mockResponse = ResponseEntity.ok().body("mock-response");
        when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.any(HttpMethod.class),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(mockResponse);
    }

    @Test
    void postRequestShouldCallPostWithRequestDataAndUserIdHeader() {
        long userId = 1L;
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Test description")
                .build();

        requestClient.postRequest(userId, requestDto);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == requestDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void postRequestWithFullDtoShouldCallPostWithCompleteRequestData() {
        long userId = 1L;
        ItemRequestDto fullDto = ItemRequestDto.builder()
                .id(123L)
                .description("Full request description")
                .created(LocalDateTime.of(2023, 10, 1, 10, 0))
                .items(List.of(ItemDto.builder().id(1L).name("Test Item").build()))
                .build();

        requestClient.postRequest(userId, fullDto);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == fullDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void postRequestWithLongDescriptionShouldCallPostWithLongDescription() {
        long userId = 1L;
        String longDescription = "A".repeat(1000);
        ItemRequestDto longDto = ItemRequestDto.builder()
                .description(longDescription)
                .build();

        requestClient.postRequest(userId, longDto);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == longDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getUserRequestsShouldCallGetWithEmptyPathAndUserIdHeader() {
        long userId = 1L;

        requestClient.getUserRequests(userId);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getUserRequestsWithDifferentUsersShouldCallGetWithDifferentUserIds() {
        requestClient.getUserRequests(1L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );

        requestClient.getUserRequests(2L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "2")),
                Mockito.eq(Object.class)
        );

        requestClient.getUserRequests(3L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "3")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getAllRequestsShouldCallGetWithAllPathAndUserIdHeader() {
        long userId = 1L;

        requestClient.getAllRequests(userId);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/all"),
                Mockito.eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getRequestByIdShouldCallGetWithIdInPath() {
        long requestId = 123L;

        requestClient.getRequestById(requestId);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/123"),
                Mockito.eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        !entity.getHeaders().containsKey(X_SHARER_USER_ID)),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getRequestByIdWithZeroIdShouldCallGetWithZeroIdPath() {
        long requestId = 0L;

        requestClient.getRequestById(requestId);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/0"),
                Mockito.eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        !entity.getHeaders().containsKey(X_SHARER_USER_ID)),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getRequestByIdWithLargeIdShouldCallGetWithLargeIdPath() {
        long requestId = 999999999L;

        requestClient.getRequestById(requestId);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/999999999"),
                Mockito.eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        !entity.getHeaders().containsKey(X_SHARER_USER_ID)),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getAllRequestsWithDifferentUsersShouldCallGetWithDifferentUserIds() {
        requestClient.getAllRequests(1L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/all"),
                Mockito.eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );

        requestClient.getAllRequests(2L);
        Mockito.verify(restTemplate).exchange(
                Mockito.eq("/all"),
                Mockito.eq(HttpMethod.GET),
                argThat((HttpEntity<?> entity) ->
                        Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "2")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void postRequestWithEmptyDescriptionShouldCallPostWithEmptyDescription() {
        long userId = 1L;
        ItemRequestDto emptyDescriptionDto = ItemRequestDto.builder()
                .description("")
                .build();

        requestClient.postRequest(userId, emptyDescriptionDto);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == emptyDescriptionDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void postRequestWithSpecialCharactersShouldCallPostWithSpecialCharacters() {
        long userId = 1L;
        ItemRequestDto specialCharsDto = ItemRequestDto.builder()
                .description("Special chars: !@#$%^&*()_+{}[]|:;<>,.?/~`")
                .build();

        requestClient.postRequest(userId, specialCharsDto);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(""),
                Mockito.eq(HttpMethod.POST),
                argThat((HttpEntity<?> entity) ->
                        entity.getBody() == specialCharsDto &&
                                entity.getHeaders().containsKey(X_SHARER_USER_ID) &&
                                Objects.equals(entity.getHeaders().getFirst(X_SHARER_USER_ID), "1")),
                Mockito.eq(Object.class)
        );
    }
}
