package info.tomaszminiach.libshow;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import info.tomaszminiach.superspinner.SuperSpinner;

public class MainActivity extends AppCompatActivity {
    DataProvider dataProvider;
    Handler handler = new Handler();
    MyRunnable runnable;
    SuperSpinner superSpinner;
    final int ITEM_LAYOUT_RES_ID = android.R.layout.simple_list_item_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataProvider = Injector.getDataProvider(getApplicationContext());

        superSpinner = (SuperSpinner) findViewById(R.id.superSpinner);
        //setting adapter is not needed when you use filtering
        ArrayAdapter<String> simpleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataProvider.getItems(null));
        superSpinner.setAdapter(simpleAdapter);
        assert superSpinner != null;
        //superSpinner.setFilterable(true);
        superSpinner.setEmptyText("AAA!!\nNO ITEMS :(");
        superSpinner.setHintView(ITEM_LAYOUT_RES_ID, android.R.id.text1,
                "-please select-");
        superSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("On Item Selected", superSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        final CheckBox simulateLongQueryChB = (CheckBox) findViewById(R.id.simulateLongCheckbox);
//        assert simulateLongQueryChB != null;
//        simulateLongQueryChB.
//                setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                        setSpinnerMode(b);
//                    }
//                });
//        simulateLongQueryChB.setChecked(false);
//        setSpinnerMode(false);

    }

    private void setSpinnerMode(boolean simulateLongQuery){
        if (!simulateLongQuery) {
            //VERSION 1
            //use for fast updates on the main thread
            superSpinner.setCallbackFilterListener(null);
            superSpinner.setFilterListener(filterListener);
        } else {
            //VERSION 2
            //use for longer running filtering
            superSpinner.setFilterListener(null);
            superSpinner.setCallbackFilterListener(callbackFilterListener);
        }
    }

    SuperSpinner.FilterListener filterListener = new SuperSpinner.FilterListener() {
        @Override
        public ListAdapter onFilter(String text) {
            return new ArrayAdapter<>(MainActivity.this, ITEM_LAYOUT_RES_ID,
                    dataProvider.getItems(text));
        }
    };

    SuperSpinner.CallbackFilterListener callbackFilterListener =
            new SuperSpinner.CallbackFilterListener() {
                @Override
                public ListAdapter onFilter(String text, SuperSpinner.Callback callback) {
                    //simulate long running operation
                    handler.removeCallbacks(runnable);
                    runnable = new MyRunnable(callback, dataProvider, text);
                    handler.postDelayed(runnable, 1000);
                    return null;
                }
            };

    public void checkSelection(View view) {
        String selectedItem = (String) superSpinner.getSelectedItem();
        if (selectedItem == null)
            Toast.makeText(this, "[nothing is selected]", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, selectedItem, Toast.LENGTH_SHORT).show();
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
                callback.provideAdapter(new ArrayAdapter<>(MainActivity.this,
                        ITEM_LAYOUT_RES_ID, dataProvider.getItems(text)));

        }
    }


}
