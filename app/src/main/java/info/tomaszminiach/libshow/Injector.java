package info.tomaszminiach.libshow;

import android.content.Context;

import info.tomaszminiach.libshow.data.MockDataProvider;

/**
 * Created by tomaszminiach on 23/03/2017.
 */

public class Injector {

   public static int MODE_COUNTRIES =1, MODE_EMPTY=2;
   private static int dataMode = MODE_COUNTRIES;

    public static void setDataMode(int mode) {
        dataMode = mode;
    }

    public static DataProvider getDataProvider(Context appContext){
        if(dataMode == MODE_EMPTY)
            return new MockDataProvider(appContext);
        else
            return new MockDataProvider(appContext);
    }


}
