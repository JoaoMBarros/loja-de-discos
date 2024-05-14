package br.com.sysmap.bootcamp.errors;

public class MissingAlbumFieldsException extends RuntimeException{
    public MissingAlbumFieldsException(String message) {
        super(message);
    }
}
