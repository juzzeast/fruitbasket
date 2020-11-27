package org.yuri.glushko.assesment.fruitbasket.exceptions;

// Covers database-related issues
public class DatabaseException extends Exception{
    public DatabaseException(String errMsg) {
        super(errMsg);
    }
}
