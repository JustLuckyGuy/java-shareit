package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<ItemRequestDto> postRequest(long userId, ItemRequestDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<List<ItemRequestDto>> getUserRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<List<ItemRequestDto>> getAllRequests(long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<ItemRequestDto> getRequestById(long requestId) {
        return get("/" + requestId);
    }
}
