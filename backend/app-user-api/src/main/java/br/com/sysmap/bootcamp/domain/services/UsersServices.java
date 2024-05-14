package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.mapper.UserMapper;
import br.com.sysmap.bootcamp.domain.repositories.UsersRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import br.com.sysmap.bootcamp.dto.UserDto;
import br.com.sysmap.bootcamp.dto.WalletCreationDto;
import br.com.sysmap.bootcamp.errors.IncorrectCredentialsException;
import br.com.sysmap.bootcamp.errors.MissingUserFieldsException;
import br.com.sysmap.bootcamp.errors.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import br.com.sysmap.bootcamp.errors.UserAlreadyExistsException;
import org.apache.coyote.BadRequestException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersServices implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    private final RabbitTemplate rabbitTemplate;

    @Transactional(propagation = Propagation.REQUIRED)
    public Users save(Users user){
        return this.usersRepository.save(user);
    }

    public void validateFields(UserDto userDto) throws RuntimeException {
        if (userDto.getName() == null || userDto.getName().isEmpty() ||
            userDto.getEmail() == null || userDto.getEmail().isEmpty() ||
            userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
            throw new MissingUserFieldsException("Name, email, and password are required");
        }
    }

    public WalletCreationDto createWallet(UserDto userDto){
        WalletCreationDto walletDto = WalletCreationDto.builder()
                .balance(BigDecimal.ZERO)
                .points(0L)
                .lastUpdate(LocalDateTime.now())
                .user(userDto)
                .build();

        this.rabbitTemplate.convertAndSend("WalletCreationQueue", walletDto);

        return walletDto;
    }

    public Users createUser(UserDto userDto) throws RuntimeException {
        Optional<Users> userExists = this.usersRepository.findByEmail(userDto.getEmail());

        if (userExists.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        this.validateFields(userDto);

        try{
            // Encode the password in the UserDto
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto = userDto.toBuilder().password(encodedPassword).build();

            // Save the user entity and retrieve the generated ID
            Users savedUser = this.save(UserMapper.INSTANCE.toEntity(userDto));

            // Update the UserDto with the generated ID
            userDto = userDto.toBuilder().id(savedUser.getId()).build();

            this.createWallet(userDto);

            return savedUser;

        } catch (Exception e) {
            log.error("An error occurred while trying to save user", e);
            throw e;
        }
    }

    public Users updateUser(UserDto userDto) {
        // Put method requires all fields to be sent
        this.validateFields(userDto);

        // Get authenticated user
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();

        Users user = this.findByEmail(username);

        user = user.toBuilder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();

        return this.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> optionalUser = this.usersRepository.findByEmail(username);
        return optionalUser.map(user -> new User(user.getEmail(), user.getPassword(), new ArrayList<GrantedAuthority>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Users findByEmail(String email){
       Optional<Users> user = this.usersRepository.findByEmail(email);

       if (user.isEmpty()) {
           throw new UserNotFoundException("User not found");
       }

       return user.get();
    }

    public AuthDto auth(AuthDto authDto) {
        Users user = this.findByEmail(authDto.getEmail());

        if (!passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
            throw new IncorrectCredentialsException("Invalid password");
        }

        StringBuilder password = new StringBuilder().append(user.getEmail()).append(":").append(user.getPassword());

        return AuthDto.builder().email(user.getEmail()).token(
                Base64.getEncoder().withoutPadding().encodeToString(password.toString().getBytes())
        ).id(user.getId()).build();

    }

    public List<Users> getAllUsers() {
        try{
            return this.usersRepository.findAll();
        } catch (Exception e) {
            log.error("An error occurred while trying to get all users", e);
            throw e;
        }
    }

    public Users getUser(Long id) {
        Optional<Users> user =  this.usersRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        } else {
            return user.get();
        }
    }
}
