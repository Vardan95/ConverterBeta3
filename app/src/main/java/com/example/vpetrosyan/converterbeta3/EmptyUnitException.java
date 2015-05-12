package com.example.vpetrosyan.converterbeta3;

/**
 * Created by vpetrosyan on 21.04.2015.
 */
public class EmptyUnitException extends Exception {
    public EmptyUnitException()
    {
       super("One of units is empty,please enter correct unit name");
    }
}
