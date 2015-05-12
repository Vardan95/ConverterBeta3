package com.example.vpetrosyan.converterbeta3;

import java.util.logging.Logger;

/**
 * Class which provides simple convert entry
 * Created by Vardan Petrosyan on 21.04.2015.
 */

final public class ConvertItem{

    public ConvertItem(String from,String to,double value)
            throws EmptyUnitException
    {
        if(from.isEmpty() || to.isEmpty())
        {
            throw  new EmptyUnitException();
        }
        else
        {
            unitFrom_ = from;
            unitTo_ = to;
            valuePerOne_ = value;
        }
    }

    public ConvertItem()
    {
        unitFrom_ = "noname";
        unitTo_ = "noname";
        valuePerOne_ = 0.0;
    }

    @Override
    public String toString() {
        return "CONVERTER::" + unitFrom_ + " TO " + unitTo_ + " VALUE " + valuePerOne_;
    }

    public ConvertItem assign(ConvertItem other)
    {
        this.unitTo_ = other.unitTo_;
        this.unitFrom_ = other.unitFrom_;
        this.valuePerOne_ = other.valuePerOne_;

        return this;
    }



    public String unitFrom_;
    public String unitTo_;
    public double valuePerOne_;
}