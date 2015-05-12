package com.example.vpetrosyan.converterbeta3;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;


/**
 * Created by vpetrosyan on 03.05.2015.
 * Class which updates currency rates
 * Everyone who wants to be able to listen currency changes must implement CurrencyUpdateListener interface
 */
public class CurrencyUpdateTask extends AsyncTask<Object,Object,String> {

    public interface CurrencyUpdateListener
    {
        public void onCurrencyUpdated(String baseUnit,HashMap<String,Double> obj);
    }

    public CurrencyUpdateTask(String baseCurrency,CurrencyUpdateListener listener)
    {
       if(baseCurrency.isEmpty())
       {
           BaseUnit = "USD";
       }
       else
       {
           BaseUnit = baseCurrency;
       }

       updateListener_ = listener;

       rates_ = new HashMap<String,Double>();
    }

    @Override
    protected String doInBackground(Object... params) {
        try
        {
            Log.d("CONVERTER-TASK", "background task");
            URL webServiceURL = new URL(BASE_URL + BaseUnit.toUpperCase() + API_KEY);

            Reader rateReader = new InputStreamReader(webServiceURL.openStream());

            JsonReader rateJsonReader = new JsonReader(rateReader);

            rateJsonReader.beginObject();

            String name = rateJsonReader.nextName();

            Log.d(TAG + name,rateJsonReader.nextString());

            name = rateJsonReader.nextName();

            Log.d(TAG + name,rateJsonReader.nextString());

            name = rateJsonReader.nextName();

            Log.d(TAG + name,"Achieved");

            if(rateJsonReader.hasNext())
            {
                Log.d(TAG + name,"HAS NEXT");
                rateJsonReader.beginObject();
                while (rateJsonReader.hasNext())
                {
                    rates_.put(rateJsonReader.nextName(),rateJsonReader.nextDouble());
                }
            }
        }
        catch (MalformedURLException e)
        {
            Log.v(TAG, e.toString());
        }
        catch (IOException e)
        {
            Log.v(TAG, e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        updateListener_.onCurrencyUpdated(BaseUnit.toUpperCase(),rates_);
    }

    private static final String TAG = "CurrencyUpdateTask.java";

    private static final String API_KEY = "&apiKey=jr-00ce355528ac3c5d42dabe83e15e65d3";
    private static final String BASE_URL = "http://jsonrates.com/get/?base=";
    private String BaseUnit;
    private HashMap<String,Double> rates_;
    private CurrencyUpdateListener updateListener_;

}

