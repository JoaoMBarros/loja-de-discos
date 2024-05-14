package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDebitDto;
import br.com.sysmap.bootcamp.errors.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.client.RestTemplate;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlbumService {
    private final RabbitTemplate rabbitTemplate;
    private final SpotifyApi spotifyApi;
    private final RestTemplate restTemplate;
    private final UsersServices usersServices;
    private final AlbumRepository albumRepository;
    private final ObjectMapper objectMapper;

    public List<AlbumModel> getAlbums(String search) {
        if (search == null || search.isEmpty()) {
            throw new InvalidParameterException("Search parameter is required");
        }
        try {
           return this.spotifyApi.getAlbums(search);
        } catch (IOException | ParseException | SpotifyWebApiException e) {
           throw new UnavailableSpotifyApiException("Couldn't fetch albums from Spotify API");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Album albumSale(Album album) {
        this.validateAlbumFields(album);

        Users user = getUser();

        List<Album> userAlbums = this.albumRepository.findAllByUsers(user);

        // Check if user already bought the album and its sale status is "PROCESSING" or "COMPLETED"
        boolean userHasAlbum = userAlbums.stream()
                .anyMatch(existingAlbum ->
                        existingAlbum.getIdSpotify().equals(album.getIdSpotify()) &&
                                (existingAlbum.getSaleStatus().equals("PROCESSING") || existingAlbum.getSaleStatus().equals("COMPLETED")));

        if (userHasAlbum) {
            throw new DuplicateAlbumException("User already bought this album");
        } else {
            Album updatedAlbum = album.toBuilder()
                    .users(user)
                    .saleStatus("PROCESSING")
                    .build();
            WalletDebitDto walletDebitDto = WalletDebitDto.builder()
                    .email(user.getEmail())
                    .value(updatedAlbum.getValue())
                    .idSpotify(updatedAlbum.getIdSpotify())
                    .build();

            this.rabbitTemplate.convertAndSend("WalletDebitQueue", walletDebitDto);

            return this.albumRepository.save(updatedAlbum);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAlbumSale(Boolean isDebitConfirmed, String userEmail, String idSpotify) {
        try {
            Users user = this.usersServices.findByEmail(userEmail);
            Album album = this.albumRepository.findByIdSpotify(idSpotify);

            // Update album with user and sale status
            if (isDebitConfirmed) {
                album = album.toBuilder()
                        .users(user)
                        .saleStatus("COMPLETED")
                        .build();
                this.albumRepository.save(album);
            } else {
                this.albumRepository.delete(album);

                // Document said that can't save an album with the same idSpotify
//                album = album.toBuilder()
//                        .users(user)
//                        .saleStatus("CANCELED")
//                        .build();
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to save album {}", e);
            throw e;
        }
    }

    public List<Album> getUserAlbums() {
        Users user = getUser();
        return this.albumRepository.findAllByUsers(user);
    }

    public void removeAlbum(Long id) {
        // Remove album only if the user is the owner
        Users user = getUser();

        // It was asked that the album was removed via its id, so not using albumRepository.findByUsers()
        Optional<Album> album = albumRepository.findById(id);

        Album foundAlbum;
        if (album.isEmpty()) {
            throw new AlbumWasNotFoundException("Album not found");
        } else {
            foundAlbum = album.get();
        }

        if (!foundAlbum.getUsers().equals(user)) {
            throw new AccessDeniedException("User is not the owner of the album");
        }

        albumRepository.delete(foundAlbum);
    }

    public Users getUser(){
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();

        if (username == null || username.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        return this.usersServices.findByEmail(username);
    }

    public void validateAlbumFields(Album album) {
        if (album.getName() == null || album.getName().isEmpty() ||
                album.getIdSpotify() == null || album.getIdSpotify().isEmpty() ||
                album.getArtistName() == null || album.getArtistName().isEmpty() ||
                album.getImageUrl() == null || album.getImageUrl().isEmpty() ||
                album.getValue() == null) {
            throw new MissingAlbumFieldsException("All album fields are required");
        }
    }
}
