package com.svalero.apibikes.exception;

public class RepairOrderNotFoundException extends Exception{
    public RepairOrderNotFoundException(){
        super("The order does not exist");
    }

    public RepairOrderNotFoundException(String message){
        super(message);
    }
}
