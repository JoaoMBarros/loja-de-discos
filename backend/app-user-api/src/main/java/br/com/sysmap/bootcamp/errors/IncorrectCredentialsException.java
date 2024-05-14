package br.com.sysmap.bootcamp.errors;

public class IncorrectCredentialsException extends RuntimeException{
    public IncorrectCredentialsException(String message) {
        super(message);
    }
}
