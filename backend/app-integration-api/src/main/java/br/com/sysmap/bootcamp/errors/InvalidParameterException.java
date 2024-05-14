package br.com.sysmap.bootcamp.errors;

public class InvalidParameterException extends RuntimeException{
    public InvalidParameterException(String message) {
        super(message);
    }
}
