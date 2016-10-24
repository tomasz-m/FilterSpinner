package info.tomaszminiach.libshow;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import info.tomaszminiach.superspinner.DataProvider;
import info.tomaszminiach.superspinner.MockDataProvider;
import info.tomaszminiach.superspinner.SuperSpinner;

public class MainActivity extends AppCompatActivity {
    DataProvider dataProvider;
    Handler handler = new Handler();
    MyRunnable runnable;
    boolean isBlocking=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SuperSpinner superSpinner = (SuperSpinner) findViewById(R.id.superSpinner);

        dataProvider = MockDataProvider.getInstance(getApplicationContext());

        ArrayAdapter<String> simpleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataProvider.getItems(null));

        superSpinner.setAdapter(simpleAdapter);
        superSpinner.setFilterable(true);

        superSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("On Item Selected",superSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if(isBlocking)
            //VERSION 1
            //use for fast updates on teh main thread
            superSpinner.setFilterListener(new SuperSpinner.FilterListener() {
                @Override
                public ListAdapter onFilter(String text) {
                    return new ArrayAdapter<String>(MainActivity.this,android.R.layout.test_list_item,dataProvider.getItems(text));
                }
            });
        else {
            //VERSION 2
            //use for longer running filtering
            superSpinner.setCallbackFilterListener(new SuperSpinner.CallbackFilterListener() {
                @Override
                public ListAdapter onFilter(String text, SuperSpinner.Callback callback) {
                    //simulate long running operation
                    handler.removeCallbacks(runnable);
                    runnable = new MyRunnable(callback, dataProvider, text);
                    handler.postDelayed(runnable, 1000);
                    return null;
                }
            });
        }


    }


    public class MyRunnable implements Runnable {
        private SuperSpinner.Callback callback;
        private DataProvider dataProvider;
        private String text;

        public MyRunnable(SuperSpinner.Callback callback, DataProvider dataProvider, String text) {
            this.callback = callback;
            this.dataProvider = dataProvider;
            this.text = text;
        }

        @Override
        public void run() {
            if (callback != null)
                callback.provideAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.test_list_item, dataProvider.getItems(text)));

        }
    }


}
