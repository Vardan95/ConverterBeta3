package com.example.vpetrosyan.converterbeta3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by vpetrosyan on 25.04.2015
 * ISO4217 currency codes.
 */
public final class CurrencyCodes {

    public String getCurrencyCode(String country)
    {
        if(country.isEmpty())
        {
            return null;
        }

        if(europeCountries_.contains(country))
        {
            return "EUR";
        }
        else
        {
            if(currencyCodes_.containsKey(country.toUpperCase()))
            {
                return currencyCodes_.get(country.toUpperCase());
            }
            else
            {
                return null;
            }
        }
    }

    public ArrayList<String> getSupportedCountries()
    {
        Set<String> countries = currencyCodes_.keySet();

        ArrayList<String> countryList = new ArrayList<String>();

        Iterator<String> it = countries.iterator();

        while (it.hasNext())
        {
            countryList.add(it.next());
        }

        return countryList;
    }

    public String getCountryNameFromCurrencyCode(String code)
    {

        if(code.equalsIgnoreCase("EUR"))
        {
            return "Country From Europe";
        }

        for (String item : currencyCodes_.keySet()) {
            if (currencyCodes_.get(item).equals(code)) {
                return item;
            }
        }

        return null;
    }

    public CurrencyCodes()
    {
        currencyCodes_ =  new HashMap<String,String>();

        currencyCodes_.put("AFGHANISTAN","AFA");
        currencyCodes_.put("ALBANIA","ALL");
        currencyCodes_.put("ALGERIA","DZD");
        currencyCodes_.put("ANGOLA","AON");
        currencyCodes_.put("ANGUILLA","XCD");
        currencyCodes_.put("ARGENTINA","ARS");
        currencyCodes_.put("ARMENIA","AMD");
        currencyCodes_.put("ARUBA","AWG");
        currencyCodes_.put("AUSTRALIA","AUD");
        currencyCodes_.put("AUSTRIA","ATS");
        currencyCodes_.put("AZERBAIJAN","AZM");

        currencyCodes_.put("BAHAMAS","BSD");
        currencyCodes_.put("BAHRAIN","BHD");
        currencyCodes_.put("BANGLADESH","BDT");
        currencyCodes_.put("BARBADOS","BBD");
        currencyCodes_.put("BELARUS","BYB");
        currencyCodes_.put("BELGIUM","BEF");
        currencyCodes_.put("BELIZE","BZD");
        currencyCodes_.put("BOLIVIA","BOB");
        currencyCodes_.put("BRAZIL","BRL");
        currencyCodes_.put("BULGARIA","BGL");

        currencyCodes_.put("CANADA","CAD");
        currencyCodes_.put("CHINA","CNY");
        currencyCodes_.put("CROATIA","HRK");
        currencyCodes_.put("CUBA", "CUP");
        currencyCodes_.put("CYPRUS","CYP");
        currencyCodes_.put("CZECH REPUBLIC","CZK");

        currencyCodes_.put("DENMARK","DKK");

        currencyCodes_.put("FRANCE","FRF");
        currencyCodes_.put("FINLAND","FIM");

        currencyCodes_.put("GEORGIA","GEL");
        currencyCodes_.put("GERMANY","DEM");
        currencyCodes_.put("GREECE","GRD");
        currencyCodes_.put("HUNGARY","HUF");
        currencyCodes_.put("INDIA","INR");
        currencyCodes_.put("IRAN","IRR");

        currencyCodes_.put("ITALY","ITL");
        currencyCodes_.put("JAPAN","JPY");
        currencyCodes_.put("NETHERLANDS","NLG");
        currencyCodes_.put("PORTUGAL","PTE");

        currencyCodes_.put("RUSSIA","RUB");
        currencyCodes_.put("SPAIN","ESP");

        currencyCodes_.put("TURKEY","TRL");
        currencyCodes_.put("UKRAINE","UAK");
        currencyCodes_.put("UNITED ARAB EMIRATES","AED");
        currencyCodes_.put("UNITED KINGDOM","GBP");
        currencyCodes_.put("UNITED STATES","USD");
        currencyCodes_.put("URUGUAY","UYU");

        europeCountries_ = new ArrayList<>();

        europeCountries_.add("AUSTRIA");
        europeCountries_.add("BELGIUM");
        europeCountries_.add("BULGARIA");
        europeCountries_.add("CROATIA");
        europeCountries_.add("CYPRUS");
        europeCountries_.add("CZECH REPUBLIC");
        europeCountries_.add("DENMARK");

        europeCountries_.add("ESTONIA");
        europeCountries_.add("FINLAND");
        europeCountries_.add("FRANCE");
        europeCountries_.add("GERMANY");
        europeCountries_.add("GREECE");
        europeCountries_.add("HUNGARY");
        europeCountries_.add("IRELAND");

        europeCountries_.add("ITALY");
        europeCountries_.add("LATVIA");
        europeCountries_.add("LITHUANIA");
        europeCountries_.add("LUXEMBOURG");
        europeCountries_.add("MALTA");
        europeCountries_.add("THE NETHERLANDS");
        europeCountries_.add("POLAND");
        europeCountries_.add("PORTUGAL");
        europeCountries_.add("ROMANIA");
        europeCountries_.add("SLOVAKIA");
        europeCountries_.add("SLOVENIA");
        europeCountries_.add("SPAIN");
        europeCountries_.add("SWEDEN");
        europeCountries_.add("UNITED KINGDOM");
    }

    private Map<String,String> currencyCodes_;
    private ArrayList<String> europeCountries_;
}
