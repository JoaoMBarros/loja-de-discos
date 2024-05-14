package br.com.sysmap.bootcamp.errors;

public class AlbumWasNotFoundException extends RuntimeException{
    public AlbumWasNotFoundException(String message) {
        super(message);
    }
}
