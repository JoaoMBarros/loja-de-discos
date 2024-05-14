package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.services.WalletServices;
import br.com.sysmap.bootcamp.errors.InvalidValueException;
import br.com.sysmap.bootcamp.errors.UserNotFoundException;
import br.com.sysmap.bootcamp.errors.WalletWasNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletServices walletServices;

    @Operation(summary = "Get authenticated user wallet")
    @GetMapping
    public ResponseEntity<Object> getAuthenticatedUserWallet(){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.walletServices.getAuthenticatedUserWallet());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        } catch (WalletWasNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet not found");
        }
    }

    @Operation(summary = "Credit wallet")
    @PostMapping("/credit/{value}")
    public ResponseEntity<Object> credit(@PathVariable String value){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.walletServices.creditWallet(value));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        } catch (InvalidValueException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid value");
        } catch (WalletWasNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wallet not found");
        }
    }
}
