package br.com.sysmap.bootcamp.errors;

public class DuplicateAlbumException extends RuntimeException{
    public DuplicateAlbumException(String message) {
        super(message);
    }
}
