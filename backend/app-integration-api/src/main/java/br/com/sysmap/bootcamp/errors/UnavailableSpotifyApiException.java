package br.com.sysmap.bootcamp.errors;

public class UnavailableSpotifyApiException extends RuntimeException{
    public UnavailableSpotifyApiException(String message) {
        super(message);
    }
}
