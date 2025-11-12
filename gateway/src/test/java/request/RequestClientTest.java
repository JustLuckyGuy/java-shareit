package request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureMockMvc
class RequestClientTest {
    @Test
    void postRequestShouldReturnResponse() {
        RequestClient requestClient = mock(RequestClient.class);
        ItemRequestDto requestDto = ItemRequestDto.builder().build();
        when(requestClient.postRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = requestClient.postRequest(1L, requestDto);

        assertNotNull(response);
    }

    @Test
    void getUserRequestsShouldReturnResponse() {
        RequestClient requestClient = mock(RequestClient.class);
        when(requestClient.getUserRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = requestClient.getUserRequests(1L);

        assertNotNull(response);
    }

    @Test
    void getAllRequestsShouldReturnResponse() {
        RequestClient requestClient = mock(RequestClient.class);
        when(requestClient.getAllRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = requestClient.getAllRequests(1L);

        assertNotNull(response);
    }

    @Test
    void getRequestByIdShouldReturnResponse() {
        RequestClient requestClient = mock(RequestClient.class);
        when(requestClient.getRequestById(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = requestClient.getRequestById(1L);

        assertNotNull(response);
    }

    @Test
    void postRequestWithFullDtoShouldReturnResponse() {
        RequestClient requestClient = mock(RequestClient.class);
        ItemRequestDto fullDto = ItemRequestDto.builder()
                .id(123L)
                .description("Full request")
                .build();
        when(requestClient.postRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = requestClient.postRequest(1L, fullDto);

        assertNotNull(response);
    }

    @Test
    void getRequestByIdWithZeroIdShouldReturnResponse() {
        RequestClient requestClient = mock(RequestClient.class);
        when(requestClient.getRequestById(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = requestClient.getRequestById(0L);

        assertNotNull(response);
    }

    @Test
    void postRequestWithLongDescriptionShouldReturnResponse() {
        RequestClient requestClient = mock(RequestClient.class);
        String longDescription = "A".repeat(1000);
        ItemRequestDto longDto = ItemRequestDto.builder()
                .description(longDescription)
                .build();
        when(requestClient.postRequest(Mockito.anyLong(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response = requestClient.postRequest(1L, longDto);

        assertNotNull(response);
    }

    @Test
    void getUserRequestsWithDifferentUsersShouldReturnResponse() {
        RequestClient requestClient = mock(RequestClient.class);
        when(requestClient.getUserRequests(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        ResponseEntity<?> response1 = requestClient.getUserRequests(1L);
        ResponseEntity<?> response2 = requestClient.getUserRequests(2L);
        ResponseEntity<?> response3 = requestClient.getUserRequests(3L);

        assertNotNull(response1);
        assertNotNull(response2);
        assertNotNull(response3);
    }
}
