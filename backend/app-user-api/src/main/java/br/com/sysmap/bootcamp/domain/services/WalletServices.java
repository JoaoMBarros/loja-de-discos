package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.enums.WeekDayPoints;
import br.com.sysmap.bootcamp.domain.mapper.WalletMapper;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import br.com.sysmap.bootcamp.dto.DebitConfirmationDto;
import br.com.sysmap.bootcamp.dto.WalletCreationDto;
import br.com.sysmap.bootcamp.dto.WalletDebitDto;
import br.com.sysmap.bootcamp.errors.InvalidValueException;
import br.com.sysmap.bootcamp.errors.UserNotFoundException;
import br.com.sysmap.bootcamp.errors.WalletWasNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServices {

    private final WalletRepository walletRepository;

    private final UsersServices usersServices;

    private final RabbitTemplate rabbitTemplate;

    public Optional<Wallet> getAuthenticatedUserWallet() throws RuntimeException {
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();

        if (username == null) {
            throw new UserNotFoundException("User not found");
        }

        Users user = this.usersServices.findByEmail(username);

        Optional<Wallet> wallet = this.walletRepository.findByUser(user);

        if (wallet.isEmpty()) {
            throw new WalletWasNotFoundException("Wallet not found");
        }

        return wallet;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet save(Wallet wallet){
        try {
            wallet = wallet.toBuilder().lastUpdate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))).build();
            return this.walletRepository.save(wallet);
        } catch (Exception e) {
            log.error("Error on save wallet", e);
            throw new RuntimeException("Error on save wallet");
        }
    }

    public Wallet createWallet(WalletCreationDto walletDto){
        try {
            Wallet wallet = WalletMapper.INSTANCE.toEntity(walletDto);

            return this.save(wallet);
        } catch (Exception e) {
            log.error("Error on createWallet", e);
            throw new RuntimeException("Error on createWallet");
        }
    }

    public Wallet creditWallet(String value) throws RuntimeException {
        // Value has to be a number
        if (!value.matches("^[0-9]+$")) {
            throw new InvalidValueException("Value has to be a number");
        }

        Optional<Wallet> wallet = this.getAuthenticatedUserWallet();

        Wallet newWallet = wallet.get().toBuilder().balance(wallet.get().getBalance().add(new BigDecimal(value))).build();

        return save(newWallet);
    }

    public void debitWallet(WalletDebitDto walletDebitDto) {
        Users user = usersServices.findByEmail(walletDebitDto.getEmail());
        Optional<Wallet> wallet = walletRepository.findByUser(user);
        Wallet returnedWallet = wallet.orElseThrow(() -> new WalletWasNotFoundException("Wallet not found"));

        Long pointsToAdd = (long) WeekDayPoints.valueOf(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).getDayOfWeek().name()).getPoints();
        BigDecimal value = walletDebitDto.getValue();

        if(returnedWallet.getBalance().compareTo(value) < 0){
            DebitConfirmationDto debitConfirmationDto = DebitConfirmationDto.builder()
                    .userEmail(user.getEmail())
                    .isDebitConfirmed(false)
                    .idSpotify(walletDebitDto.getIdSpotify())
                    .build();

            rabbitTemplate.convertAndSend("DebitConfirmationQueue", debitConfirmationDto);
        } else {
            returnedWallet = returnedWallet.toBuilder()
                    .balance(returnedWallet.getBalance().subtract(value))
                    .points(returnedWallet.getPoints() + pointsToAdd)
                    .build();

            this.save(returnedWallet);

            DebitConfirmationDto debitConfirmationDto = DebitConfirmationDto.builder()
                    .userEmail(user.getEmail())
                    .isDebitConfirmed(true)
                    .idSpotify(walletDebitDto.getIdSpotify())
                    .build();

            rabbitTemplate.convertAndSend("DebitConfirmationQueue", debitConfirmationDto);
        }
    }
}
