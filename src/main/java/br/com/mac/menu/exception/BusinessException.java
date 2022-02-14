package br.com.mac.menu.exception;

public class BusinessException extends Exception{
    public BusinessException(String errorMessage) {
        super(errorMessage);
    }
}
