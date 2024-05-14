package br.com.sysmap.bootcamp.errors;

public class WalletWasNotFoundException extends RuntimeException{
    public WalletWasNotFoundException(String message) {
        super(message);
    }
}
