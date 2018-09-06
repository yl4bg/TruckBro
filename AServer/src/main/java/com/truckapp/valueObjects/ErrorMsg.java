package com.truckapp.valueObjects;

/**
 * Wrapper of a String for error messages
 */
public class ErrorMsg {

    private String text;

    public void setText(String text){
    	this.text = text;
    }
    
    public String getText() {
        return text;
    }
}
