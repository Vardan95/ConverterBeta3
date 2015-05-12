package com.example.vpetrosyan.converterbeta3;

import android.os.Environment;
import android.util.JsonWriter;
import android.util.Log;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.FileWriter;
/**
 * Created by vpetrosyan on 04.05.2015.
 * Class which provides recover/save functionality for converter app's core
 */
public class StateSaverModule {
    public static void setData(Map<String,UnitCategory> data)
    {
        if(data != null)
        {
            saveData_ = data;
        }
    }

    public static void setSavePath(String path)
    {
        if((path != null) && (!(path.isEmpty())))
        {
            savePath_ = path;
        }
    }

    public static String executeSave() {
        ArrayList<String> fileNames_ = new ArrayList<String>();

        for (Map.Entry<String, UnitCategory> pair : saveData_.entrySet())
        {
            try{
                File newFile = new File(savePath_  + "/" + pair.getKey() + ".json");
                newFile.delete();
            }
            catch(Exception e){
                Log.d("CONVERTER_SAVER",e.toString());
            }

            try (OutputStream out = new FileOutputStream(savePath_  + "/" + pair.getKey() + ".json")) {

                fileNames_.add(pair.getKey());

                JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.setIndent("  ");
                writer.beginObject();
                writer.name(BASE_STRING).value(pair.getValue().getBaseName());

                ArrayList<ConvertItem> table = pair.getValue().getTable();

                for(int i = 0; i < table.size(); ++i)
                {
                    writer.name(table.get(i).unitFrom_ + "->" + table.get(i).unitTo_).value(table.get(i).valuePerOne_);
                }

                writer.endObject();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter writer = new FileWriter(savePath_ + "/" + SAVE_FILE_STRING +".txt");

            for(String str: fileNames_) {
                writer.write(str);
                writer.write("\n");
            }
            writer.close();
            return SAVE_FILE_STRING;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       return null;
    }


    private static Map<String,UnitCategory> saveData_;
    private static String savePath_;

    //String to represent json structure
    final private static  String BASE_STRING  = new String("base");
    final private static  String SAVE_FILE_STRING  = new String("savedCategories");
}
