package com.svalero.apibikes.exception;

public class MechanicNotFoundException extends Exception{
    public MechanicNotFoundException(){
        super("The mechanic does not exist");
    }

    public MechanicNotFoundException(String message){
        super(message);
    }
}
