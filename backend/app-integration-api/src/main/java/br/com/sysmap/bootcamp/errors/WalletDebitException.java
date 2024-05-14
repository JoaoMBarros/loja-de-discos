package br.com.sysmap.bootcamp.errors;

public class WalletDebitException extends RuntimeException{
    public WalletDebitException(String message) {
        super(message);
    }
}
