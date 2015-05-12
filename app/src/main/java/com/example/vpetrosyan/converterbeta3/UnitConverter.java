package com.example.vpetrosyan.converterbeta3;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class to provide converter functionality
 * current version does not support add/remove unit action and all
 * categories are intended
 * Created by Vardan Petrosyan on 21.04.2015.
 */
public class UnitConverter implements CurrencyUpdateTask.CurrencyUpdateListener {

    public interface updateCurrencyRatesListener
    {
        public void onCurrencyUpdated(String currencyCategoryName);
    }

    public UnitConverter()
    {
        categories_ = new ArrayList<String>();

        unitValues_ = new HashMap<String,UnitCategory>();

        currencyCodes_ = new CurrencyCodes();

        listener_ = null;

        autoPopulate();
    }

    public ArrayList<String> getCategoryList()
    {
        return categories_;
    }

    public ArrayList<String> getUnits(String category_name)
    {
        UnitCategory unitTable = unitValues_.get(category_name);

        return unitTable.getAllUnits();
    }

    public UnitCategory getCategory(String name)
    {
        return unitValues_.get(name);
    }

    public Double convert(String category_,String unitFrom,String unitTo,Double value)
    {
        UnitCategory unitTable = unitValues_.get(category_);

        return unitTable.convert(unitFrom, unitTo, value);
    }

    public ArrayList<String> getSupportedCountries()
    {
        return currencyCodes_.getSupportedCountries();
    }

    public void updateCurrencyRates(String baseUnit)
    {
        Log.d("CONVERTER-CORE", "UPDATING");
        new CurrencyUpdateTask(baseUnit,this).execute();
    }


    @Override
    public void onCurrencyUpdated(String baseUnit, HashMap<String, Double> obj) {
        String currencyCategoryString = Constants.CONVERTER_CURRENCY_CATEGORY_TITLE;
        categories_.add(currencyCategoryString);

        UnitCategory currencyCategory = new UnitCategory(currencyCategoryString,baseUnit);

        Iterator it = obj.entrySet().iterator();

        while(it.hasNext())
        {
            Map.Entry<String, Double> pair = (Map.Entry)it.next();
            currencyCategory.addConvertItem(baseUnit,pair.getKey(),pair.getValue());
            it.remove();
        }

        unitValues_.put(currencyCategoryString, currencyCategory);

        if(listener_ != null) {
            listener_.onCurrencyUpdated(currencyCategoryString);
        }
    }

    public String getCurrencyCode(String country)
    {
        if(!country.isEmpty()) {
            return currencyCodes_.getCurrencyCode(country);
        }

        return null;
    }

    public String getCountryNameFromCurrencyCode(String code)
    {
        if(!code.isEmpty()) {
            return currencyCodes_.getCountryNameFromCurrencyCode(code);
        }

        return null;
    }

    public boolean registerCurrencyUpdateListener(updateCurrencyRatesListener obj)
    {
        if(obj != null)
        {
            if(listener_ == null)
            {
                listener_ = obj;
                return true;
            }
        }
        return false;
    }

    public void populateFromMap(Map<String,UnitCategory> newValues)
    {
        if(newValues != null)
        {
            Log.d("CONVERTER_CORE","populatedfrommap");

            unitValues_.clear();
            categories_.clear();

            Iterator it = newValues.entrySet().iterator();

            while(it.hasNext())
            {
                Map.Entry<String, UnitCategory> pair = (Map.Entry)it.next();
                categories_.add(pair.getKey());
                unitValues_.put(pair.getKey(),new UnitCategory(pair.getKey(),pair.getValue().getBaseName()).assign(pair.getValue()));
                it.remove();
            }
        }
        else
        {
            Log.d("CONVERTER_CORE","null map auto populate");

            categories_.clear();
            unitValues_.clear();

            autoPopulate();
        }
    }

    public Map<String,UnitCategory> getSaveData()
    {
        return unitValues_;
    }

    private ArrayList<String> categories_;
    private Map<String,UnitCategory> unitValues_;
    private CurrencyCodes currencyCodes_;
    private updateCurrencyRatesListener listener_;

    private void autoPopulate()
    {
        String lengthCategoryString = new String("Length");
        categories_.add(lengthCategoryString);

        UnitCategory lengthCategory = new UnitCategory(lengthCategoryString,"cm");

        lengthCategory.addUnit("mm",0.1);
        lengthCategory.addUnit("m",100.0);
        lengthCategory.addUnit("km",100000.0);

        lengthCategory.addConvertItem("km","m",1000.0);
        lengthCategory.addConvertItem("km","mm",1000000.0);

        lengthCategory.addConvertItem("m","mm",1000.0);

        unitValues_.put(lengthCategoryString,lengthCategory);

        String massCategoryString = new String("Mass");
        categories_.add(massCategoryString);

        UnitCategory massCategory = new UnitCategory(massCategoryString,"g");

        massCategory.addUnit("mg",0.001);
        massCategory.addUnit("dag",10.0);
        massCategory.addUnit("kg",1000.0);
        massCategory.addUnit("tonnes",1000000.0);

        massCategory.addConvertItem("tonnes","kg",1000.0);
        massCategory.addConvertItem("tonnes","dag",100000.0);
        massCategory.addConvertItem("tonnes","mg",1000000000.0);

        massCategory.addConvertItem("kg","dag",100.0);
        massCategory.addConvertItem("kg","mg",1000000.0);

        massCategory.addConvertItem("dag","mg",10000.0);

        //basicAutoPopulate();

        unitValues_.put(massCategoryString,massCategory);
    }

    private void basicAutoPopulate()
    {
       /* String areaCategoryString = new String("Area");
        categories_.add(areaCategoryString);

        UnitCategory lengthCategory = new UnitCategory(areaCategoryString,"cm*cm");

        lengthCategory.addUnit("mm*mm",0.01);
        lengthCategory.addUnit("m*m",10000.0);
        lengthCategory.addUnit("ha",1000000000.0);

        lengthCategory.addConvertItem("ha","mm*mm",1000.0);
        lengthCategory.addConvertItem("km","mm",1000000.0);

        lengthCategory.addConvertItem("m","mm",1000.0);

        unitValues_.put(lengthCategoryString,lengthCategory);*/
    }
}
