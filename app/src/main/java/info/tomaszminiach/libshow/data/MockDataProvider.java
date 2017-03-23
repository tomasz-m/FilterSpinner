package info.tomaszminiach.libshow.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.tomaszminiach.libshow.DataProvider;
import info.tomaszminiach.libshow.R;

/**
 * Created by Tomek on 2016-10-21.
 */
public class MockDataProvider implements DataProvider {

    private String[] baseList = null;


    public MockDataProvider(Context appContext){
        baseList = appContext.getResources().getStringArray(R.array.countries_array);
    }

    @Override
    public List<String> getItems(String filter) {
        if(filter==null) {
            return Arrays.asList(baseList);
        }
        filter=filter.toLowerCase();
        ArrayList<String> resultList = new ArrayList<>();
        for(String sourceItem:baseList){
            if(sourceItem!=null && sourceItem.toLowerCase().contains(filter)){
                resultList.add(sourceItem);
            }
        }
        return resultList;
    }

}
