package br.com.sysmap.bootcamp.domain.services;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UserRepository;
import br.com.sysmap.bootcamp.domain.service.UsersServices;
import br.com.sysmap.bootcamp.errors.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// ALL TESTS PASSING
@SpringBootTest
public class UsersServicesTest {

    @Autowired
    private UsersServices usersService;

    @MockBean
    private UserRepository usersRepository;

    @Test
    @DisplayName("Should return valid user details found by email")
    public void shouldReturnValidUserDetails() {
        Users user = Users.builder()
                .email("testuser@email.com")
                .password("123")
                .build();

        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UserDetails foundUserDetails = usersService.loadUserByUsername("test@email.com");

        assertEquals(user.getEmail(), foundUserDetails.getUsername());
    }

    @Test
    @DisplayName("Should return username not found by email")
    public void shouldThrowUsernameNotFound() {

        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usersService.loadUserByUsername("a"));
    }

    @Test
    @DisplayName("Should return valid user found by email")
    public void shouldReturnValidUserFoundByEmail() {
        Users user = Users.builder()
                .email("testuser@email.com")
                .password("123")
                .build();
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        Users foundUser = usersService.findByEmail("testuser@email.com");

        assertEquals(user, foundUser);
    }

    @Test
    @DisplayName("Should user was not found")
    public void shouldReturnUserWasNotFoundException() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> usersService.findByEmail("a"));
    }
}
