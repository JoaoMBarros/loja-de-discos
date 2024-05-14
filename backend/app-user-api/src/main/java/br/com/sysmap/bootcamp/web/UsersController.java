package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.services.UsersServices;
import br.com.sysmap.bootcamp.dto.AuthDto;
import br.com.sysmap.bootcamp.dto.UserDto;
import br.com.sysmap.bootcamp.errors.IncorrectCredentialsException;
import br.com.sysmap.bootcamp.errors.MissingUserFieldsException;
import br.com.sysmap.bootcamp.errors.UserAlreadyExistsException;
import br.com.sysmap.bootcamp.errors.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersServices usersServices;

    @Operation(summary = "Create a new user")
    @PostMapping("/create")
    public ResponseEntity<Object> saveUser(@RequestBody UserDto userDto){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(this.usersServices.createUser(userDto));
        }
        catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        } catch (MissingUserFieldsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name, email, and password are required");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Authenticate an user")
    @PostMapping("/auth")
    public ResponseEntity<Object> auth(@RequestBody AuthDto authDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.usersServices.auth(authDto));
        }
        catch (IncorrectCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect credentials");
        } catch(UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
    }

    @Operation(summary = "Get list of users")
    @GetMapping()
    public ResponseEntity<List<Users>> getAllUsers(){
        // At most it returns an empty list
        return ResponseEntity.status(HttpStatus.OK).body(this.usersServices.getAllUsers());
    }

    @Operation(summary = "Get a specific user")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.usersServices.getUser(id));
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
    }

    @Operation(summary = "Update an existing user")
    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.usersServices.updateUser(userDto));
        }
        catch (MissingUserFieldsException e){
            // TEST FOR THIS CONTROLLER EXCEPTION IS PASSING BUT THE COVERAGE IS NOT WORKING
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name, email, and password are required");
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
    }

}
