package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(1L, "Boris", "chuguevb@gamil.com");

    @Test
    void saveNewUser() throws Exception {
        when(userService.saveUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void saveNewUserWithException() throws Exception {
        when((userService.saveUser(any())))
                .thenThrow(IllegalArgumentException.class);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto));
        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(List.of(userDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @SneakyThrows
    @Test
    void getUser() {
        long userId = 1L;
        mvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).findUserById(userId);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIsNotValid_thenReturnBadRequest() {
        userDto.setName(null);

        mvc.perform(patch("/users/{id}", userDto.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(userDto, userDto.getId());
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIsValid_thenReturnUpdatedUserDto() {
        UserDto userDtoForUpdate = new UserDto(1L, "UpdatedName",
                "new@email.com");
        mvc.perform(patch("/users/{id}", userDto.getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(userDtoForUpdate)))
                .andDo(print());

        verify(userService).updateUser(userDtoForUpdate, userDto.getId());
    }

    @SneakyThrows
    @Test
    void delete() {
        long id = 1L;
        Mockito.doNothing().when(userService).removeUser(id);
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.delete("/users/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Mockito.verify(userService, Mockito.times(1)).removeUser(id);
    }
}

