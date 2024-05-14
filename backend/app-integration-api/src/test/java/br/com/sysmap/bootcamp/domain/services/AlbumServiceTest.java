package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repository.AlbumRepository;
import br.com.sysmap.bootcamp.domain.repository.UserRepository;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.errors.*;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// ALL TESTS PASSINGS
@SpringBootTest
class AlbumServiceTest {

    @MockBean
    private SpotifyApi spotifyApi;

    @MockBean
    private AlbumRepository albumRepository;

    @MockBean
    private UserRepository usersRepository;

    @Autowired
    private AlbumService albumService;

    @Test
    @DisplayName("Should return album model list from search")
    public void shouldReturnAlbumModelListFromSearch() throws IOException, SpotifyWebApiException, ParseException {
        String search = "test";
        List<AlbumModel> expectedAlbums = Collections.singletonList(new AlbumModel());
        when(spotifyApi.getAlbums(search)).thenReturn(expectedAlbums);

        List<AlbumModel> actualAlbums = albumService.getAlbums(search);

        assertEquals(expectedAlbums, actualAlbums);
        verify(spotifyApi).getAlbums(search);
    }

    @Test
    @DisplayName("Should invalid parameter exception from empty search")
    public void shouldReturnInvalidParameterExceptionFromEmptySearch() throws IOException, SpotifyWebApiException, ParseException {
        String search = "";

        assertThrows(InvalidParameterException.class, () -> albumService.getAlbums(search));
    }

    @Test
    @DisplayName("Should unavailable spotify api exception from spotify integration")
    public void shouldReturnUnavailableSpotifyApiException() throws IOException, SpotifyWebApiException, ParseException {
        String search = "a";
        when(spotifyApi.getAlbums(search)).thenThrow(new SpotifyWebApiException(""));
        assertThrows(UnavailableSpotifyApiException.class, () -> albumService.getAlbums(search));
    }

    @Test
    @DisplayName("Should return user albums")
    public void shouldReturnUserAlbums() {
        Users user = Users.builder()
                .email("testuser@email.com")
                .password("123")
                .build();

        Album album = Album.builder().users(user).build();
        List<Album> albums = Collections.singletonList(album);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("usertest@email.com", null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(albumRepository.findAllByUsers(any())).thenReturn(albums);

        List<Album> result = albumService.getUserAlbums();

        assertEquals(1, result.size());
        assertEquals(album, result.get(0));
    }

    @Test
    @DisplayName("Should return user")
    public void shouldReturnUser() {
        Users user = Users.builder().email("testuser@email.com").build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("usertest@email.com", null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        Users result = albumService.getUser();

        assertEquals(user, result);
    }

    @Test
    @DisplayName("Should return user not found")
    public void shouldReturnUserNotFoundException() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("usertest@email.com", null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        assertThrows(UserNotFoundException.class, () -> albumService.getUser());
    }


    @Test
    @DisplayName("Should remove user album")
    public void shouldRemoveUserAlbum() {
        Users user = Users.builder().email("testuser@email.com").build();
        Album album = Album.builder().users(user).build();

        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("testuser@email.com", null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(albumRepository.findById(any())).thenReturn(Optional.of(album));

        albumService.removeAlbum(1L);

        verify(albumRepository, times(1)).delete(album);
    }

    @Test
    @DisplayName("Should return user not found")
    public void shouldThrowUserNotFoundExceptionWhenRemovingAlbum() {
        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("usertest@email.com", null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        assertThrows(UserNotFoundException.class, () -> albumService.removeAlbum(1L));
    }

    @Test
    @DisplayName("Should return album not found")
    public void shouldThrowAlbumNotFoundExceptionWhenRemovingAlbum() {

        when(usersRepository.findByEmail(anyString())).thenReturn(Optional.of(Users.builder().build()));
        when(albumRepository.findById(any())).thenReturn(Optional.empty());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("usertest@email.com", null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        assertThrows(AlbumWasNotFoundException.class, () -> albumService.removeAlbum(1L));
    }

    @Test
    @DisplayName("Should return access denied")
    public void shouldThrowAccessDeniedWhenDeletingNotOwnedAlbum() {
        Users correctUser = Users.builder().email("testcorrectuser@email.com").password("123").build();
        Users fakeUser = Users.builder().email("testfakeuser@email.com").password("123").build();
        Album album = Album.builder()
                        .name("Test Album")
                        .idSpotify("12345")
                        .artistName("Test Artist")
                        .imageUrl("http://example.com/image.jpg")
                        .value(BigDecimal.valueOf(10.0))
                        .users(correctUser)
                        .build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("testfakeuser@email.com", null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(usersRepository.findByEmail(any())).thenReturn(Optional.of(fakeUser));
        when(albumRepository.findById(any())).thenReturn(Optional.of(album));

        assertThrows(AccessDeniedException.class, () -> albumService.removeAlbum(1L));
    }

    @Test
    @DisplayName("Should validate album fields")
    public void shouldNotThrowErrorWhenValidating() {
        Album album = Album.builder()
                .name("Test Album")
                .idSpotify("12345")
                .artistName("Test Artist")
                .imageUrl("http://example.com/image.jpg")
                .value(BigDecimal.valueOf(10.0))
                .build();

        assertDoesNotThrow(() -> albumService.validateAlbumFields(album));
    }

    @Test
    @DisplayName("Should return missing album fields")
    public void shouldThrowMissingAlbumFields() {
        Album album = Album.builder()
                .name("Test Album")
                .imageUrl("http://example.com/image.jpg")
                .value(BigDecimal.valueOf(10.0))
                .build();

        assertThrows(MissingAlbumFieldsException.class, () -> albumService.validateAlbumFields(album));
    }
}
