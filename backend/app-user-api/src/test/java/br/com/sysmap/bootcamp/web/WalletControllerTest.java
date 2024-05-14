package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.services.WalletServices;
import br.com.sysmap.bootcamp.errors.InvalidValueException;
import br.com.sysmap.bootcamp.errors.UserNotFoundException;
import br.com.sysmap.bootcamp.errors.WalletWasNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ALL TESTS PASSING
// COVERAGE 100%
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletServices walletServices;

    @Test
    @DisplayName("Should return wallet when get authenticated user wallet")
    public void shouldReturnWalletWhenGetAuthenticatedUserWallet() throws Exception {
        Users user = Users.builder()
                .name("test")
                .email("usertest@email.com")
                .password("123")
                .build();

        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.ZERO)
                .points(0L)
                .lastUpdate(LocalDateTime.now())
                .user(user)
                .build();

        when(walletServices.getAuthenticatedUserWallet()).thenReturn(Optional.of(wallet));

        mockMvc.perform(get("/wallet"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    @DisplayName("Should return bad request when get not authenticated user wallet")
    public void shouldReturnBadRequestWhenGetNotAuthenticatedUserWallet() throws Exception {
        when(walletServices.getAuthenticatedUserWallet()).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/wallet"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Should return internal server error when the wallet was not created")
    public void shouldReturnInternalServerErrorWhenTheWalletWasNotCreated() throws Exception {
        when(walletServices.getAuthenticatedUserWallet()).thenThrow(new WalletWasNotFoundException("Wallet not found"));

        mockMvc.perform(get("/wallet"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Wallet not found"));
    }

    @Test
    @DisplayName("Should return wallet when credit wallet")
    public void shouldReturnWalletWhenCreditWallet() throws Exception {
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(50))
                .points(0L)
                .lastUpdate(LocalDateTime.now())
                .user(Users.builder().build())
                .build();

        when(walletServices.creditWallet("50")).thenReturn(wallet);

        mockMvc.perform(post("/wallet/credit/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(50));
    }

    @Test
    @DisplayName("Should return bad request when credit wallet with invalid user")
    public void shouldReturnBadRequestWhenCreditWalletWithInvalidUser() throws Exception {
        when(walletServices.creditWallet("1")).thenThrow(new UserNotFoundException("Error on creditWallet"));

        mockMvc.perform(post("/wallet/credit/1"))
                        .andExpect(status().isBadRequest())
            .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Should return bad request when credit wallet with invalid value")
    public void shouldReturnBadRequestWhenCreditWalletWithInvalidValue() throws Exception {
        when(walletServices.creditWallet("invalid")).thenThrow(new InvalidValueException("Error on creditWallet"));

        mockMvc.perform(post("/wallet/credit/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid value"));
    }

    @Test
    @DisplayName("Should return internal server error wallet not found")
    public void shouldReturnInternalServerErrorWhenWalletNotFound() throws Exception {
        when(walletServices.creditWallet("1")).thenThrow(new WalletWasNotFoundException("Error on creditWallet"));

        mockMvc.perform(post("/wallet/credit/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Wallet not found"));
    }
}