package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.services.UsersServices;
import br.com.sysmap.bootcamp.dto.AuthDto;
import br.com.sysmap.bootcamp.dto.UserDto;
import br.com.sysmap.bootcamp.errors.IncorrectCredentialsException;
import br.com.sysmap.bootcamp.errors.MissingUserFieldsException;
import br.com.sysmap.bootcamp.errors.UserAlreadyExistsException;
import br.com.sysmap.bootcamp.errors.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ALL TESTS PASSING
// COVERAGE 100%
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersServices usersServices;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return users when valid users is saved")
    public void shouldReturnUsersWhenValidUsersIsSaved() throws Exception {
        Users user = Users.builder()
                .name("User Test")
                .email("test@email.com")
                .password("123")
                .build();

        when(usersServices.createUser(any())).thenReturn(user);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.name").value("User Test"))
                        .andExpect(jsonPath("$.email").value("test@email.com"))
                        .andExpect(jsonPath("$.password").exists());
    }

    @Test
    @DisplayName("Should return bad request error when missing fields")
    public void shouldReturnBadRequestWhenInvalidUsersIsSaved() throws Exception {
        // Missing email
        Users user = Users.builder()
                    .name("User Test")
                    .password("123")
                    .build();

        when(usersServices.createUser(any())).thenThrow(new MissingUserFieldsException("Name, email, and password are required"));

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                        .andExpect(status().isBadRequest())
            .andExpect(content().string("Name, email, and password are required"));
    }

    @Test
    @DisplayName("Should return bad request when user already exists")
    public void shouldReturnBadRequestWhenUserAlreadyExists() throws Exception {
        Users user = Users.builder()
                .name("User Test")
                .email("test@email.com")
                .password("123")
                .build();

        when(usersServices.createUser(any())).thenThrow(new UserAlreadyExistsException("User already exists"));

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                        .andExpect(status().isBadRequest())
            .andExpect(content().string("User already exists"));
    }

    @Test
    @DisplayName("Should return internal server error when different user json fields")
    public void shouldReturnInternalServerErrorWhenAdditionalInvalidFields() throws Exception {
        // Invalid field "invalid"
        String jsonContent = "{\"name\":\"User test\",\"email\":\"test@email.com\",\"password\":\"newpassword\", \"invalid\":\"invalid\"}";

        when(usersServices.createUser(any())).thenThrow(new RuntimeException("Invalid fields"));

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should return user when authenticate valid user")
    public void shouldReturnUserWhenAuthenticateUser() throws Exception {
        UserDto userDto = UserDto.builder().name("test").email("usertest@email.com").password("123").build();

        when(usersServices.auth(any())).thenReturn(AuthDto.builder().token("mock").build());

        mockMvc.perform(post("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.token").exists())
                        .andReturn();
    }

    @Test
    @DisplayName("Should return bad request when trying to authenticate an user which email doesn't exist")
    public void shouldReturnInternalServerErrorWhenAuthenticatingNotExistingUser() throws Exception {
        // Checking the auth endpoint
        UserDto userDto = UserDto.builder().email("invaliduser@email.com").password("123").build();

        when(usersServices.auth(any())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Should return bad request when trying to authenticate an user with incorrect password")
    public void shouldReturnInternalServerErrorWhenAuthenticatingUserWithIncorrectPassword() throws Exception {
        // Checking the auth endpoint
        UserDto userDto = UserDto.builder().email("invaliduser@email.com").password("123").build();

        when(usersServices.auth(any())).thenThrow(new IncorrectCredentialsException("Incorrect credentials"));

        mockMvc.perform(post("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                        .andExpect(status().isBadRequest())
            .andExpect(content().string("Incorrect credentials"));
    }

    @Test
    @DisplayName("Should return list of users when get all users")
    public void shouldReturnListOfUsersWhenGetAllUsers() throws Exception {
        Users user1 = Users.builder().build();
        Users user2 = Users.builder().build();

        when(usersServices.getAllUsers()).thenReturn(new ArrayList<Users>() {{
            add(user1);
            add(user2);
        }});

        mockMvc.perform(get("/users"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should return user when get user")
    public void shouldReturnUserWhenGetUser() throws Exception {
        Users user = Users.builder()
                        .name("test")
                        .email("usertest@email.com")
                        .password("123")
                        .build();

        when(usersServices.getUser(any())).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("usertest@email.com"));
    }

    @Test
    @DisplayName("Should bad request when getting user that doesn't exist")
    public void shouldReturnBadRequestWhenGettingNonExistentUser() throws Exception {

        when(usersServices.getUser(any())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Should return user when update user")
    public void shouldReturnUserWhenUpdateUser() throws Exception {
        Users updatedUser = Users.builder().name("User test").email("test@email.com").password("newpassword").build();

        when(usersServices.updateUser(any())).thenReturn(updatedUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"User test\",\"email\":\"test@email.com\",\"password\":\"newpassword\"}"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("User test"))
                        .andExpect(jsonPath("$.email").value("test@email.com"))
                        .andExpect(jsonPath("$.password").exists());
    }

    @Test
    @DisplayName("Should return bad request when user not found")
    public void shouldReturnBadRequestWhenUserNotFoundToUpdate() throws Exception {
        UserDto userDto = UserDto.builder().email("test").password("newpassword").build();

        when(usersServices.updateUser(any())).thenThrow(new UserNotFoundException("User not found"));
        mockMvc.perform(MockMvcRequestBuilders.put("/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                        .andExpect(status().isBadRequest())
            .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Should return bad request when missing any user json fields")
    public void shouldReturnBadRequestWhenMissingAnyUserJsonFields() throws Exception {
        UserDto userDto = UserDto.builder().email("test").password("newpassword").build();

        when(usersServices.updateUser(any())).thenThrow(new MissingUserFieldsException("Name, email, and password are required"));

        mockMvc.perform(MockMvcRequestBuilders.put("/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Name, email, and password are required"));
    }
}
