package info.tomaszminiach.libshow.data;

import android.content.Context;

import java.util.List;

import info.tomaszminiach.libshow.DataProvider;

/**
 * Created by Tomek on 2016-10-21.
 */
public class EmptyDataProvider implements DataProvider {


    public EmptyDataProvider(Context appContext){
    }

    @Override
    public List<String> getItems(String filter) {

        return null;
    }

}
