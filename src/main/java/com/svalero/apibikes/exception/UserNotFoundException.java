package com.svalero.apibikes.exception;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(){
        super("The user does not exist");
    }

    public UserNotFoundException(String message){
        super(message);
    }
}
