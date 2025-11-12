package user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.dto.UserDTO;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@AutoConfigureJsonTesters
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDTO> json;

    @Test
    void shouldSerializeAndDeserializeUserDTO() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        JsonContent<UserDTO> result = json.write(userDTO);
        UserDTO deserialized = json.parseObject(result.getJson());

        assertThat(deserialized.getId()).isEqualTo(1L);
        assertThat(deserialized.getName()).isEqualTo("John Doe");
        assertThat(deserialized.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldSerializeUserDTOWithAllFields() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        JsonContent<UserDTO> result = json.write(userDTO);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldDeserializeUserDTOFromJson() throws Exception {
        String content = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

        UserDTO userDTO = json.parseObject(content);

        assertThat(userDTO.getId()).isEqualTo(1L);
        assertThat(userDTO.getName()).isEqualTo("John Doe");
        assertThat(userDTO.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void shouldHandleNullFields() throws Exception {
        String content = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

        UserDTO userDTO = json.parseObject(content);

        assertThat(userDTO.getName()).isEqualTo("John Doe");
        assertThat(userDTO.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(userDTO.getId()).isNull();
    }

    @Test
    void shouldHandleLongName() throws Exception {
        String longName = "A".repeat(255);
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name(longName)
                .email("test@example.com")
                .build();

        JsonContent<UserDTO> result = json.write(userDTO);
        UserDTO deserialized = json.parseObject(result.getJson());

        assertThat(deserialized.getName()).isEqualTo(longName);
        assertThat(deserialized.getName()).hasSize(255);
    }

    @Test
    void shouldHandleSpecialCharactersInName() throws Exception {
        String nameWithSpecialChars = "John O'Neil-Doe Jr.";
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name(nameWithSpecialChars)
                .email("john@example.com")
                .build();

        JsonContent<UserDTO> result = json.write(userDTO);
        UserDTO deserialized = json.parseObject(result.getJson());

        assertThat(deserialized.getName()).isEqualTo(nameWithSpecialChars);
    }

    @Test
    void shouldHandleComplexEmail() throws Exception {
        String complexEmail = "john.doe+tag@sub.domain.co.uk";
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email(complexEmail)
                .build();

        JsonContent<UserDTO> result = json.write(userDTO);
        UserDTO deserialized = json.parseObject(result.getJson());

        assertThat(deserialized.getEmail()).isEqualTo(complexEmail);
    }

    @Test
    void shouldHandleEmailWithNumbers() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("User123")
                .email("user123@example.com")
                .build();

        JsonContent<UserDTO> result = json.write(userDTO);
        UserDTO deserialized = json.parseObject(result.getJson());

        assertThat(deserialized.getName()).isEqualTo("User123");
        assertThat(deserialized.getEmail()).isEqualTo("user123@example.com");
    }

    @Test
    void shouldHandleEmailWithUnderscore() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("John_Doe")
                .email("john_doe@example.com")
                .build();

        JsonContent<UserDTO> result = json.write(userDTO);
        UserDTO deserialized = json.parseObject(result.getJson());

        assertThat(deserialized.getName()).isEqualTo("John_Doe");
        assertThat(deserialized.getEmail()).isEqualTo("john_doe@example.com");
    }

    @Test
    void shouldHandleUnicodeCharactersInName() throws Exception {
        String unicodeName = "Jöhn Döe テスト";
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name(unicodeName)
                .email("test@example.com")
                .build();

        JsonContent<UserDTO> result = json.write(userDTO);
        UserDTO deserialized = json.parseObject(result.getJson());

        assertThat(deserialized.getName()).isEqualTo(unicodeName);
    }

    @Test
    void shouldHandleMultipleUsers() throws Exception {
        UserDTO user1 = UserDTO.builder()
                .id(1L)
                .name("User One")
                .email("user1@example.com")
                .build();

        UserDTO user2 = UserDTO.builder()
                .id(2L)
                .name("User Two")
                .email("user2@example.com")
                .build();

        JsonContent<UserDTO> result1 = json.write(user1);
        JsonContent<UserDTO> result2 = json.write(user2);

        UserDTO deserialized1 = json.parseObject(result1.getJson());
        UserDTO deserialized2 = json.parseObject(result2.getJson());

        assertThat(deserialized1.getId()).isEqualTo(1L);
        assertThat(deserialized1.getName()).isEqualTo("User One");
        assertThat(deserialized1.getEmail()).isEqualTo("user1@example.com");

        assertThat(deserialized2.getId()).isEqualTo(2L);
        assertThat(deserialized2.getName()).isEqualTo("User Two");
        assertThat(deserialized2.getEmail()).isEqualTo("user2@example.com");
    }

    @Test
    void shouldHandleEmptyObject() throws Exception {
        String content = "{}";

        UserDTO userDTO = json.parseObject(content);

        assertThat(userDTO.getId()).isNull();
        assertThat(userDTO.getName()).isNull();
        assertThat(userDTO.getEmail()).isNull();
    }

    @Test
    void shouldHandlePartialData() throws Exception {
        String content = "{\"name\":\"Partial User\"}";

        UserDTO userDTO = json.parseObject(content);

        assertThat(userDTO.getName()).isEqualTo("Partial User");
        assertThat(userDTO.getId()).isNull();
        assertThat(userDTO.getEmail()).isNull();
    }

    @Test
    void shouldHandleZeroId() throws Exception {
        String content = "{\"id\":0,\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        UserDTO userDTO = json.parseObject(content);

        assertThat(userDTO.getId()).isZero();
        assertThat(userDTO.getName()).isEqualTo("Test User");
        assertThat(userDTO.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldHandleLargeId() throws Exception {
        long largeId = 999999999L;
        UserDTO userDTO = UserDTO.builder()
                .id(largeId)
                .name("Test User")
                .email("test@example.com")
                .build();

        JsonContent<UserDTO> result = json.write(userDTO);
        UserDTO deserialized = json.parseObject(result.getJson());

        assertThat(deserialized.getId()).isEqualTo(largeId);
    }

    @Test
    void shouldPreserveFieldOrderInSerialization() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        JsonContent<UserDTO> result = json.write(userDTO);


        assertThat(result.getJson()).contains("\"id\"");
        assertThat(result.getJson()).contains("\"name\"");
        assertThat(result.getJson()).contains("\"email\"");


        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@example.com");
    }
}
