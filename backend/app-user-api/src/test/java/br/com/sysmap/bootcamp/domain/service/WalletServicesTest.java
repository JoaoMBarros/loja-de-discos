package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.repositories.UsersRepository;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import br.com.sysmap.bootcamp.domain.services.WalletServices;
import br.com.sysmap.bootcamp.dto.WalletCreationDto;
import br.com.sysmap.bootcamp.dto.WalletDebitDto;
import br.com.sysmap.bootcamp.errors.UserNotFoundException;
import br.com.sysmap.bootcamp.errors.WalletWasNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WalletServicesTest {

    @MockBean
    private WalletRepository walletRepository;

    @Autowired
    private WalletServices walletServices;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("Should save wallet via save method")
    public void shouldSaveWallet() {
        Wallet wallet = Wallet.builder().build();
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet result = walletServices.save(wallet);

        assertEquals(wallet, result);
    }

    @Test
    @DisplayName("Should throw WalletNotFoundException when no wallet was found")
    public void shouldThrowWalletWasNotFoundExceptionWhenNoWalletFound() {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("usertest@email.com", null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(Users.builder().build()));
        when(walletRepository.findByUser(any())).thenReturn(Optional.empty());

        assertThrows(WalletWasNotFoundException.class, () -> walletServices.getAuthenticatedUserWallet());
    }

    @Test
    @DisplayName("Should throw UserWasNotFoundException when no authenticated user was found")
    public void shouldThrowUserWasNotFoundExceptionWhenNoWalletFound() {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("usertest@email.com", null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(usersRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> walletServices.getAuthenticatedUserWallet());
    }

    @Test
    @DisplayName("Should create wallet via the create method")
    public void shouldCreateWallet() {
        WalletCreationDto walletDto = WalletCreationDto.builder().build();
        Wallet wallet = Wallet.builder().build();
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        Wallet result = walletServices.createWallet(walletDto);

        verify(walletRepository).save(any(Wallet.class));
        assertEquals(wallet, result);
    }

    @Test
    @DisplayName("Should debit the user wallet")
    public void shouldDebitUserWallet() {
        String email = "testuser@email.com";
        WalletDebitDto walletDebitDto = WalletDebitDto.builder().email(email).value(BigDecimal.valueOf(90)).build();
        Users user = Users.builder().email(email).build();
        Wallet wallet = Wallet.builder().balance(BigDecimal.valueOf(500)).points(0L).build();

        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(any(Users.class))).thenReturn(Optional.ofNullable(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        walletServices.debitWallet(walletDebitDto);

        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException user not found when wallet debit")
    public void shouldThrowUserNotFoundWhenNoUserWhenDebitingTheWallet() {
        WalletDebitDto walletDebitDto = WalletDebitDto.builder().build();

        when(usersRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> walletServices.debitWallet(walletDebitDto));
    }

    @Test
    @DisplayName("Should throw WalletWasNotFound user's wallet was not found")
    public void shouldThrowWalletWasNotFoundWhenNoWalletWhenDebiting() {
        WalletDebitDto walletDebitDto = WalletDebitDto.builder().build();

        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(Users.builder().build()));
        when(walletRepository.findByUser(any())).thenReturn(Optional.empty());

        assertThrows(WalletWasNotFoundException.class, () -> walletServices.debitWallet(walletDebitDto));
    }
}
