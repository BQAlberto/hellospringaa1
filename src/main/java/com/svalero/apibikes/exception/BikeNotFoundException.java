package com.svalero.apibikes.exception;

public class BikeNotFoundException extends Exception{
    public BikeNotFoundException(){
        super("The bike does not exist");
    }

    public BikeNotFoundException(String message){
        super(message);
    }
}
