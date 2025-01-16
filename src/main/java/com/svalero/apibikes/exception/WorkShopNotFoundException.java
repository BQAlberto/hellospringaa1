package com.svalero.apibikes.exception;

public class WorkShopNotFoundException extends Exception{
    public WorkShopNotFoundException(){
        super("The workshop does not exist");
    }

    public WorkShopNotFoundException(String message){
        super(message);
    }
}
