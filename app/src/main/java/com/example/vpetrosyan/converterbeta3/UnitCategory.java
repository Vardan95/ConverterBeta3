package com.example.vpetrosyan.converterbeta3;

import java.util.ArrayList;

/**
 * Class which provides unit's category
 * Include convert table
 * Created by vpetrosyan on 23.04.2015.
 */
public class UnitCategory {
    public UnitCategory(String category_name,String base_unit)
    {

        categoryName_ = new String("Unnamed Category" + String.valueOf(Math.random()*10));

        baseUnitName_ = new String("Unnamed Base Unit");

        if(!category_name.isEmpty())
        {
            categoryName_ = category_name;
        }

        if(!base_unit.isEmpty())
        {
            baseUnitName_ = base_unit;
        }

        units_ = new ArrayList<String>();
        table_ = new ArrayList<ConvertItem>();

        units_.add(base_unit);
        try
        {
            table_.add(new ConvertItem(baseUnitName_, baseUnitName_, 1));
        }
        catch (EmptyUnitException obj)
        {
            //nothing to do,as we guarantee that base_unit is not empty
        }
    }

    public ArrayList<String> getAllUnits()
    {
        return units_;
    }

    public String getCategoryName()
    {
        return categoryName_;
    }

    public String getBaseName() {return  baseUnitName_; }

    public boolean addConvertItem(String unitFrom,String unitTo,double value)
    {
        if(! units_.contains(unitFrom))
        {
            units_.add(unitFrom);

            try {
                table_.add(new ConvertItem(unitFrom, unitFrom, 1));
            }
            catch (EmptyUnitException obj)
            {
                return false;
            }
        }

        if(! units_.contains(unitTo))
        {
            units_.add(unitTo);

            try {
                table_.add(new ConvertItem(unitTo, unitTo, 1));
            }
            catch (EmptyUnitException obj)
            {
                return false;
            }
        }

        try {
            table_.add(new ConvertItem(unitFrom, unitTo, value));
            table_.add(new ConvertItem(unitTo, unitFrom, 1/value));
        }
        catch (EmptyUnitException obj)
        {
            return false;
        }

        return true;
    }

    public boolean addUnit(String unitFrom,double value)
    {
        if(! units_.contains(unitFrom) )
        {
            units_.add(unitFrom);
            try {
                table_.add(new ConvertItem(unitFrom, unitFrom, 1));
                table_.add(new ConvertItem(unitFrom, baseUnitName_, value));
                table_.add(new ConvertItem(baseUnitName_, unitFrom, 1/value));

                autoCompleteTable(unitFrom,value);
            }
            catch (EmptyUnitException obj)
            {
                return false;
            }

            return true;
        }

        return false;
    }

    private void autoCompleteTable(String unitFrom,double value) {

        for(int i=0; i< units_.size(); ++i)
        {
            String possibleToItem = units_.get(i);
            if(!(possibleToItem.equalsIgnoreCase(unitFrom)))
            {
                Double valuePerOne = findConvertValue(possibleToItem,baseUnitName_);
                Double returnValue = value/valuePerOne;
                addConvertItem(unitFrom,possibleToItem,returnValue);
                addConvertItem(unitFrom,possibleToItem,1/returnValue);
            }
        }
    }

    private Double findConvertValue(String from,String to)
    {
        for(int i = 0; i < table_.size(); ++i)
        {
            ConvertItem entry = table_.get(i);

            if(entry.unitFrom_.equalsIgnoreCase(from) )
            {
                if(entry.unitTo_.equalsIgnoreCase(to))
                {
                    return entry.valuePerOne_;
                }
            }
        }

        return 0.0;
    }

    public Double convert(String unitFrom,String unitTo,Double value)
    {
        Double valuePerOne = 0.0;

        for(int i = 0; i < table_.size(); ++i)
        {
            ConvertItem entry = table_.get(i);

            if(entry.unitFrom_.equalsIgnoreCase(unitFrom) )
            {
                if(entry.unitTo_.equalsIgnoreCase(unitTo))
                {
                    valuePerOne = entry.valuePerOne_;
                    break;
                }
            }
        }

        return value*valuePerOne;
    }

    public ArrayList<ConvertItem> getTable()
    {
        return table_;
    }

    public UnitCategory assign(UnitCategory other)
    {
        this.categoryName_ = other.categoryName_;
        this.baseUnitName_ = other.baseUnitName_;

        ArrayList<String> newUnits = other.getAllUnits();

        this.units_.clear();

        for(int i=0; i < newUnits.size(); ++i)
        {
            this.units_.add(newUnits.get(i));
        }


        ArrayList<ConvertItem> newTable = other.getTable();

        this.table_.clear();

        for(int i=0; i < newTable.size(); ++i)
        {
            this.table_.add(new ConvertItem().assign(newTable.get(i)));
        }

        return this;
    }

    private String categoryName_;
    private String baseUnitName_;
    private ArrayList<String> units_;
    private ArrayList<ConvertItem> table_;
}
