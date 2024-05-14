package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
@Slf4j
public class AlbumController {
    private final AlbumService albumService;

    @Operation(summary = "Get all albums")
    @GetMapping("/all")
    public ResponseEntity<Object> getAlbums(@RequestParam("search") String search){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.albumService.getAlbums(search));
        } catch (InvalidParameterException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Search parameter is required");
        } catch (UnavailableSpotifyApiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Coudn't fetch albums from Spotify API");
        }
    }

    @Operation(summary = "Sale album")
    @PostMapping("/sale")
    public ResponseEntity<Object> saveAlbum(@RequestBody Album album) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.albumService.albumSale(album));
        } catch (DuplicateAlbumException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Album already bought");
        } catch (MissingAlbumFieldsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing album fields");
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        } catch(WalletDebitException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error debiting wallet");
        }
    }

    @Operation(summary = "Get user albums")
    @GetMapping("/my-collection")
    public ResponseEntity<Object> getUserAlbums(){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.albumService.getUserAlbums());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
    }

    @Operation(summary = "Remove user album")
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeAlbum(@PathVariable("id") Long id){
        try {
            this.albumService.removeAlbum(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        } catch (AccessDeniedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        } catch (AlbumWasNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Album not found");
        }
    }
}
