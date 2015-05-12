package com.example.vpetrosyan.converterbeta3;

import android.content.Intent;
import android.util.JsonReader;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by vpetrosyan on 04.05.2015.
 *
 */
public class StateRecoverModule {

    public static void setSavePath(String path)
    {
        if((path != null) && (!(path.isEmpty())))
        {
            savePath_ = path;
        }
    }

    public static void setFileName(String fileName)
    {
        if((fileName != null) && (!(fileName.isEmpty())))
        {
            fileName_ = fileName;
        }
    }

    public static Map<String,UnitCategory> recover()
    {
        try {
            Scanner s = new Scanner(new File(savePath_ + "/" + fileName_ + ".txt"));
            ArrayList<String> list = new ArrayList<String>();
            while (s.hasNext()) {
                list.add(s.next());
            }
            s.close();

            if(list.isEmpty() == false)
            {

                HashMap<String,UnitCategory> returnMap = new HashMap<>();

                for(int i =0; i < list.size(); ++i) {

                    String categoryName;
                    String basename = new String("noname");

                    JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(savePath_ + "/" + list.get(i) + ".json"), "UTF-8"));
                    reader.beginObject();
                    categoryName = list.get(i);

                    String baseStringName = reader.nextName();

                    if (baseStringName.equalsIgnoreCase(BASE_STRING)) {
                        basename = reader.nextString();
                    }

                    UnitCategory category = new UnitCategory(categoryName,basename);

                    while (reader.hasNext()) {

                        String item = reader.nextName();
                        Double value = reader.nextDouble();

                        if (item.indexOf("->") != -1) {
                            String[] array = item.split("->", -1);
                            category.addConvertItem(array[0],array[1],value);
                        }
                    }
                    reader.endObject();

                    returnMap.put(categoryName,category);
                }

                Log.d("CONVERTER_CORE_SAVER","+WAY");
                return returnMap;
            }
        }
        catch (IOException e)
        {
            Log.d(TAG,e.toString());
        }

        Log.d("CONVERTER_CORE_SAVER","-WAY");
        return null;
    }

    private static String savePath_;
    private static String fileName_;
    final private static  String BASE_STRING  = new String("base");
    final private static  String TAG = new String("CONVERTER::RECOVERY_MODUL");
}
