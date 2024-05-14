package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repositories.UsersRepository;
import br.com.sysmap.bootcamp.domain.services.UsersServices;
import br.com.sysmap.bootcamp.dto.AuthDto;
import br.com.sysmap.bootcamp.dto.UserDto;
import br.com.sysmap.bootcamp.dto.WalletCreationDto;
import br.com.sysmap.bootcamp.errors.IncorrectCredentialsException;
import br.com.sysmap.bootcamp.errors.MissingUserFieldsException;
import br.com.sysmap.bootcamp.errors.UserAlreadyExistsException;
import br.com.sysmap.bootcamp.errors.UserNotFoundException;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// ALL TESTS PASSING
@SpringBootTest
public class UsersServiceTest {

    @Autowired
    private UsersServices usersService;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should return users when valid users is saved")
    public void shouldReturnUsersWhenValidUsersIsSaved() {
        Users user = Users.builder().name("User Test").email("usertest@email.com").password("123").build();
        when(usersRepository.save(any())).thenReturn(user);

        Users savedUser = usersService.save(user);

        assertEquals(user, savedUser);
    }

    @Test
    @DisplayName("Should not throw error when valid fields are passed")
    public void shouldNotThrowErrorWhenValidFields() {
        UserDto userDto = UserDto.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password("password")
                .build();

        assertDoesNotThrow(() -> usersService.validateFields(userDto));
    }

    @Test
    @DisplayName("Should throw MissingUserFieldsException when invalid fields are passed")
    public void shouldMissingUserFieldsExceptionWhenInvalidFields() {
        UserDto userDto = UserDto.builder()
                .name("")
                .email("testuser@email.com")
                .build();

        assertThrows(MissingUserFieldsException.class, () -> usersService.validateFields(userDto));
    }

    @Test
    @DisplayName("Should create valid user wallet")
    public void shouldCreateValidUserWallet() {
        UserDto userDto = UserDto.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password("123")
                .build();

        WalletCreationDto expectedWalletDto = WalletCreationDto.builder()
                                                .balance(BigDecimal.ZERO)
                                                .points(0L)
                                                .lastUpdate(LocalDateTime.now())
                                                .user(userDto)
                                                .build();

        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(Users.builder().build()));

        WalletCreationDto result = usersService.createWallet(userDto);

        // Verify that the RabbitTemplate's convertAndSend method was called with the correct arguments
        verify(rabbitTemplate).convertAndSend(eq("WalletCreationQueue"), any(WalletCreationDto.class));

        assertEquals(expectedWalletDto.getBalance(), result.getBalance());
        assertEquals(expectedWalletDto.getPoints(), result.getPoints());
        // assertEquals(expectedWalletDto.getLastUpdate(), result.getLastUpdate());
        assertEquals(expectedWalletDto.getUser(), result.getUser());
    }

    @Test
    @DisplayName("Should return valid user when user is created")
    public void shouldReturnValidUserWhenUserIsCreated() throws BadRequestException {
        UserDto userDto = UserDto.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password("123")
                .build();

        Users user = Users.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password(passwordEncoder.encode("123"))
                .build();

        when(usersRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(usersRepository.save(any(Users.class))).thenReturn(user);

        Users createdUser = usersService.createUser(userDto);

        assertDoesNotThrow(() -> usersService.createUser(userDto));
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getPassword(), createdUser.getPassword());
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when duplicate user being created")
    public void shouldThrowUserAlreadyExistsExceptionWhenDuplicateUserBeingCreated() {
        UserDto userDto = UserDto.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password("123")
                .build();

        when(usersRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(Users.builder().build()));

        assertThrows(UserAlreadyExistsException.class, () -> usersService.createUser(userDto));
    }

    @Test
    @DisplayName("Should update user")
    public void shouldUpdateUser() {
        UserDto userDto = UserDto.builder()
                .name("New Name")
                .email("test@email.com")
                .password("newPassword")
                .build();

        Users existingUser = Users.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password("123")
                .build();

        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(existingUser));

        UserDetails userDetails = new User("testuser@email.com", "123", new ArrayList<>());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        assertDoesNotThrow(() -> usersService.updateUser(userDto));

        // Capturing the Users object passed to save()
        ArgumentCaptor<Users> usersCaptor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(usersCaptor.capture());

        Users capturedUser = usersCaptor.getValue();
        assertEquals("New Name", capturedUser.getName());
        assertEquals("test@email.com", capturedUser.getEmail());
    }


    @Test
    @DisplayName("Should return valid user details found by email")
    public void shouldReturnValidUserDetails() {
        Users user = Users.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password("123")
                .build();
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDetails userDetails = new User("testuser@email.com", "123", new ArrayList<>());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        UserDetails foundUserDetails = usersService.loadUserByUsername("test@email.com");

        assertEquals(user.getEmail(), foundUserDetails.getUsername());
    }

    @Test
    @DisplayName("Should return valid user found by email")
    public void shouldReturnValidUserFoundByEmail() {
        Users user = Users.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password("123")
                .build();
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        Users foundUser = usersService.findByEmail("testuser@email.com");

        assertEquals(user, foundUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when finding no user")
    public void shouldThrowUserNotFoundExceptionWhenFindingNoUser() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> usersService.findByEmail("invalid@email.com"));
    }

    @Test
    @DisplayName("Should authenticate user")
    public void shouldAuthenticateUser() {
        Users user = Users.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password(passwordEncoder.encode("123"))
                .build();
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        AuthDto authDto = usersService.auth(AuthDto.builder().email("testuser@email.com").password("123").build());

        assertNotNull(authDto.getToken());
    }

    @Test
    @DisplayName("Should throw IncorrectCredentialsException when authenticating false user")
    public void shouldThrowIncorrectCredentialsExceptionWhenAuthenticatingFalseUser() {
        Users user = Users.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password("123")
                .build();
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(IncorrectCredentialsException.class, () -> usersService.auth(AuthDto.builder().email("testuser@email.com").password("invalidpassword").build()));
    }

    @Test
    @DisplayName("Should return list of all users")
    public void shouldReturnListOfAllUsers() {
        List<Users> users = new ArrayList<>();
        users.add(Users.builder().build());
        when(usersRepository.findAll()).thenReturn(users);

        List<Users> foundUsers = usersService.getAllUsers();

        assertEquals(users, foundUsers);
    }

    @Test
    @DisplayName("Should return valid user by id")
    public void shouldReturnValidUserById() {
        Users user = Users.builder()
                .name("User Test")
                .email("testuser@email.com")
                .password("123")
                .build();
        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Users foundUser = usersService.getUser(1L);

        assertEquals(user, foundUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when getting false user")
    public void shouldThrowUserNotFoundExceptionWhenGettingFalseUser() {
        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> usersService.getUser(1L));
    }
}
