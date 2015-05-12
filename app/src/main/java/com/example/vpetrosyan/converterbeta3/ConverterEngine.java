package com.example.vpetrosyan.converterbeta3;

/**
 * Created by vpetrosyan on 27.04.2015.
 */
public class ConverterEngine {

    public static UnitConverter getUnitConverter()
    {
        return converter;
    }

    public static String saveState(String path)
    {
        StateSaverModule.setSavePath(path);
        StateSaverModule.setData(converter.getSaveData());
        String fileName = StateSaverModule.executeSave();
        return fileName;
    }

    public static void recoverState(String filename,String path)
    {
        StateRecoverModule.setSavePath(path);
        StateRecoverModule.setFileName(filename);
        converter.populateFromMap(StateRecoverModule.recover());
    }

    final private static UnitConverter converter = new UnitConverter();
}
