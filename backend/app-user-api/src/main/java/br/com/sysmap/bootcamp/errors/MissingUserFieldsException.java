package br.com.sysmap.bootcamp.errors;

public class MissingUserFieldsException extends RuntimeException{
    public MissingUserFieldsException(String message){
        super(message);
    }
}
