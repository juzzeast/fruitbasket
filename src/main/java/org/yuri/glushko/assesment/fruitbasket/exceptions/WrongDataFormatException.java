package org.yuri.glushko.assesment.fruitbasket.exceptions;

// Covers data-related issues
public class WrongDataFormatException extends Exception{
    public WrongDataFormatException(String errMsg) {
        super(errMsg);
    }
}