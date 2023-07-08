package com.innowise.usermicroservice.json;

import com.innowise.usercommon.domain.domainenum.RoleEnum;
import com.innowise.usermicroservice.dto.RegistrationUserDto;
import com.innowise.usermicroservice.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@JsonTest
@AutoConfigureJson
class JsonUnitTest {

    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;

    @Autowired
    private JacksonTester<RegistrationUserDto> registrationUserDtoJacksonTester;

    @Test
    void mustSerializeUserDto() throws IOException {
        UserDto userDto = new UserDto(
                1,
                "testUsername",
                "testEmail",
                Set.of(RoleEnum.ROLE_USER),
                LocalDateTime.now(),
                true,
                true,
                true,
                true
        );

        String userDtoJson = userDtoJacksonTester.write(userDto).getJson();
        Assertions.assertNotNull(userDtoJson);
        Assertions.assertTrue(userDtoJson.contains("\"id\":1"));
        Assertions.assertTrue(userDtoJson.contains("\"username\":\"testUsername\""));
        Assertions.assertTrue(userDtoJson.contains("\"email\":\"testEmail\""));
        Assertions.assertTrue(userDtoJson.contains("\"roleSet\":[\"ROLE_USER\"]"));
    }

    @Test
    void mustSerializeNonNullFields() throws IOException {
        UserDto nullableUserDto = new UserDto(
                1,
                null,
                null,
                null,
                null,
                true,
                true,
                true,
                true
        );

        String nullableUseDtoJson = userDtoJacksonTester.write(nullableUserDto).getJson();
        Assertions.assertNotNull(nullableUseDtoJson);
        Assertions.assertFalse(nullableUseDtoJson.contains("username"));
        Assertions.assertFalse(nullableUseDtoJson.contains("email"));
        Assertions.assertFalse(nullableUseDtoJson.contains("roleSet"));
    }

    @Test
    void mustDeserializeUserDto() throws IOException {
        UserDto expectedUserDto = new UserDto(
                1,
                "testUsername",
                "testEmail",
                Set.of(RoleEnum.ROLE_USER),
                LocalDateTime.parse("2023-07-02T16:06:31.4356334"),
                true,
                true,
                true,
                true
        );
        UserDto userDto = userDtoJacksonTester.readObject("/json/01-userDto.json");
        Assertions.assertEquals(expectedUserDto, userDto);
    }

    @Test
    void mustDeserializeNotNullFields() throws IOException {
        UserDto expectedUserDto = new UserDto(
                1,
                null,
                null,
                null,
                null,
                true,
                true,
                true,
                true
        );

        UserDto userDto = userDtoJacksonTester.readObject("/json/02-userDto.json");
        Assertions.assertEquals(expectedUserDto, userDto);
    }

    @Test
    void mustSerializeRegistrationUserDto() throws IOException {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("email")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .password("pass")
                .confirmedPassword("pass")
                .build();

        String json = registrationUserDtoJacksonTester.write(registrationUserDto).getJson();
        System.out.println(json);
        Assertions.assertNotNull(json);
        Assertions.assertTrue(json.contains("\"email\""));
        Assertions.assertTrue(json.contains("\"username\""));
        Assertions.assertTrue(json.contains("\"password\""));
        Assertions.assertTrue(json.contains("\"confirmedPassword\""));
        Assertions.assertTrue(json.contains("\"roleSet\""));
    }

    @Test
    void mustDeserializeRegistrationUserDto() throws IOException {
        RegistrationUserDto expectedUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("email")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .password("pass")
                .confirmedPassword("pass")
                .build();

        RegistrationUserDto userDto = registrationUserDtoJacksonTester.readObject("/json/03-registrationUserDto.json");
        Assertions.assertEquals(expectedUserDto, userDto);
    }
}
