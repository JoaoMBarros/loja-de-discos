package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.Album;
import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.domain.service.UsersServices;
import br.com.sysmap.bootcamp.dto.DebitConfirmationDto;
import br.com.sysmap.bootcamp.errors.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ALL TESTS PASSING
// COVERAGE 100%
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumService albumService;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsersServices usersServices;

    @Test
    @DisplayName("Should return album model list")
    public void shouldReturnAlbumModelList() throws Exception {
        AlbumModel expectedAlbum = new AlbumModel();
        expectedAlbum.setId("123");
        expectedAlbum.setReleaseDate("2021-01-01");
        expectedAlbum.setValue(BigDecimal.valueOf(97.10));

        when(albumService.getAlbums(any())).thenReturn(List.of(expectedAlbum));

        mockMvc.perform(MockMvcRequestBuilders.get("/albums/all?search=TEST ARTIST")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").value("123"))
                        .andExpect(jsonPath("$[0].releaseDate").value("2021-01-01"))
                        .andExpect(jsonPath("$[0].value").value(BigDecimal.valueOf(97.10)));
    }

    @Test
    @DisplayName("Should bad request on empty search")
    public void shouldReturnBadRequestOnEmptySearch() throws Exception {
        when(albumService.getAlbums(any())).thenThrow(new InvalidParameterException("Search parameter is required"));

        mockMvc.perform(MockMvcRequestBuilders.get("/albums/all?search="))
                        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should internal server error when spotify api not available")
    public void shouldReturnInternalServerErrorOnUnavailableSpotifyApi() throws Exception {
        when(albumService.getAlbums(any())).thenThrow(new UnavailableSpotifyApiException("Search parameter is required"));

        mockMvc.perform(MockMvcRequestBuilders.get("/albums/all?search=Andryev dos teclados"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should return album with its user")
    public void shouldReturnAlbumByIdWhenAuthenticated() throws Exception {
        Album album = Album.builder()
                        .name("Na Balada (Ao Vivo)")
                        .idSpotify("4AYFuW0")
                        .artistName("Michel Teló")
                        .imageUrl("https://i.scdn.co/image/ab67616d0000b273c55a2d31ef4b957aaf4f3a9b")
                        .value(BigDecimal.valueOf(97.10))
                        .build();

        when(usersServices.findByEmail(any())).thenReturn(Users.builder().build());
        when(albumService.albumSale(any())).thenReturn(Album.builder().build());

        mockMvc.perform(MockMvcRequestBuilders.post("/albums/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                        .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return bad request when error on debiting")
    public void shouldReturnBadRequestWhenErrorOnDebiting() throws Exception {
        Album album = Album.builder()
                .name("Na Balada (Ao Vivo)")
                .idSpotify("4AYFuW0")
                .artistName("Michel Teló")
                .imageUrl("https://i.scdn.co/image/ab67616d0000b273c55a2d31ef4b957aaf4f3a9b")
                .value(BigDecimal.valueOf(97.10))
                .build();

        DebitConfirmationDto debitConfirmationDto = DebitConfirmationDto.builder()
                .isDebitConfirmed(false)
                .build();

        when(usersServices.findByEmail(any())).thenReturn(Users.builder().build());
        when(rabbitTemplate.convertSendAndReceive(any(), Optional.ofNullable(any()))).thenReturn(debitConfirmationDto);
        when(albumService.albumSale(any())).thenThrow(new WalletDebitException("Error debiting wallet"));

        mockMvc.perform(MockMvcRequestBuilders.post("/albums/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when trying to buy an owned album")
    public void shouldReturnBadRequestWhenTryingToBuyOwnedAlbum() throws Exception {
        Album album = Album.builder()
                .name("Na Balada (Ao Vivo)")
                .idSpotify("4AYFuW0")
                .artistName("Michel Teló")
                .imageUrl("https://i.scdn.co/image/ab67616d0000b273c55a2d31ef4b957aaf4f3a9b")
                .value(BigDecimal.valueOf(97.10))
                .build();

        when(usersServices.findByEmail(any())).thenReturn(Users.builder().build());
        when(rabbitTemplate.convertSendAndReceive(any(), Optional.ofNullable(any()))).thenReturn(DebitConfirmationDto.builder().build());
        when(albumService.albumSale(any())).thenThrow(DuplicateAlbumException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/albums/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Should return bad request when album misses fields")
    public void shouldReturnBadRequestWhenAlbumMissesFields() throws Exception {
        Album album = Album.builder()
                .artistName("Michel Teló")
                .imageUrl("https://i.scdn.co/image/ab67616d0000b273c55a2d31ef4b957aaf4f3a9b")
                .value(BigDecimal.valueOf(97.10))
                .build();

        when(albumService.albumSale(any())).thenThrow(new MissingAlbumFieldsException("All albums fields are required"));

        mockMvc.perform(MockMvcRequestBuilders.post("/albums/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when user not found")
    public void shouldReturnBadRequestWhenUserNotFound() throws Exception {
        Album album = Album.builder()
                .name("Na Balada (Ao Vivo)")
                .idSpotify("4AYFuW0")
                .artistName("Michel Teló")
                .imageUrl("https://i.scdn.co/image/ab67616d0000b273c55a2d31ef4b957aaf4f3a9b")
                .value(BigDecimal.valueOf(97.10))
                .build();

        when(albumService.albumSale(any())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/albums/sale")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(album)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return album list of authenticated user")
    public void shouldReturnAlbumListOfAuthenticatedUser() throws Exception {
        Album album = Album.builder()
                .name("Na Balada (Ao Vivo)")
                .idSpotify("4AYFuW0")
                .artistName("Michel Teló")
                .imageUrl("https://i.scdn.co/image/ab67616d0000b273c55a2d31ef4b957aaf4f3a9b")
                .value(BigDecimal.valueOf(97.10))
                .build();

        when(albumService.getUserAlbums()).thenReturn(List.of(album));

        mockMvc.perform(MockMvcRequestBuilders.get("/albums/my-collection")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].name").value("Na Balada (Ao Vivo)"))
                        .andExpect(jsonPath("$[0].idSpotify").value("4AYFuW0"))
                        .andExpect(jsonPath("$[0].artistName").value("Michel Teló"))
                        .andExpect(jsonPath("$[0].imageUrl").value("https://i.scdn.co/image/ab67616d0000b273c55a2d31ef4b957aaf4f3a9b"))
                        .andExpect(jsonPath("$[0].value").value(97.10));
    }

    @Test
    @DisplayName("Should return bad request when getting user not found albums")
    public void shouldReturnBadRequestWhenGettingUserNotFoundAlbums() throws Exception {
        when(albumService.getUserAlbums()).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/albums/my-collection")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should remove album from user by id")
    public void shouldRemoveAlbumFromUserById() throws Exception {
        doNothing().when(albumService).removeAlbum(any());
        mockMvc.perform(delete("/albums/remove/1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return bad request when deleting album from user that doesnt exist")
    public void shouldReturnUserWasNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(albumService).removeAlbum(any());

        mockMvc.perform(delete("/albums/remove/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request when trying to delete a not owned album")
    public void shouldReturnBadRequestWhenDeletingNotOwnedAlbum() throws Exception {
        doThrow(new AccessDeniedException("User is not the owner of the album")).when(albumService).removeAlbum(any());

        mockMvc.perform(delete("/albums/remove/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return bad request when trying to delete an album that don't exist")
    public void shouldReturnBadRequestWhenDeletingAnAlbumThatDontExist() throws Exception {
        doThrow(new AlbumWasNotFoundException("Album was not found")).when(albumService).removeAlbum(any());

        mockMvc.perform(delete("/albums/remove/1"))
                .andExpect(status().isNotFound());
    }
}