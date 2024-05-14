package br.com.sysmap.bootcamp.domain.service.integration;

import br.com.sysmap.bootcamp.domain.mapper.AlbumMapper;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import com.neovisionaries.i18n.CountryCode;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class SpotifyApi {

    private se.michaelthelin.spotify.SpotifyApi spotifyApi = new se.michaelthelin.spotify.SpotifyApi.Builder()
            .setClientId("a35ea1e18d4c4e6eb0dcbbc17a91633f")
            .setClientSecret("9db59b735b6e4666b2447fa29ee578d4")
            .build();

    public List<AlbumModel> getAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {
        spotifyApi.setAccessToken(getToken());
        return AlbumMapper.INSTANCE.toModel(spotifyApi.searchAlbums(search).market(CountryCode.BR)
                .limit(30)
                .build().execute().getItems()).stream()
                .peek(album -> album.setValue(BigDecimal.valueOf(Math.random() * ((100.00 - 12.00) + 1) + 12.00)
                        .setScale(2, BigDecimal.ROUND_HALF_UP))).toList();

    }


    public String getToken() throws IOException, ParseException, SpotifyWebApiException {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        return clientCredentialsRequest.execute().getAccessToken();
    }
}
