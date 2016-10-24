# FilterSpinner
Custom android view to replace standard spinner. 

It gives you filtering options for any adapter and data source you use.
There is also default state with nothing selected.

![Alt text](/img/spinner1.png?raw=true)

![Alt text](/img/spinner2.png?raw=true)

## Adding to your project
Now the only possibility is downloading the 'superspinner' module and adding it to your porject.
There are multiple tutorials how to do that - for example [this](http://www.truiton.com/2015/02/android-studio-add-library-project/)

You can of course build whole project in this repo (with example how to use it).

*I will make it availeb in some jscenter-like repository this week*

##Using
Add yo your layout
    <info.tomaszminiach.superspinner.SuperSpinner
        style="@style/Base.Widget.AppCompat.Spinner.Underlined"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/superSpinner"
        android:layout_alignParentStart="true" />
        

In your Activity/Fragment/View

        final SuperSpinner superSpinner = (SuperSpinner) findViewById(R.id.superSpinner);
        
You can set adpaters and listeners as for normal spinner

        superSpinner.setAdapter(simpleAdapter);
        superSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("On Item Selected",superSpinner.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        
But you can set add filters

        superSpinner.setFilterable(true);

VERSION 1 - use for fast updates on the main thread

            superSpinner.setFilterListener(new SuperSpinner.FilterListener() {
                @Override
                public ListAdapter onFilter(String text) {
                    return new ArrayAdapter<String>(MainActivity.this,android.R.layout.test_list_item,dataProvider.getItems(text));
                }
            });
            
VERSION 2 - use for longer running filterings

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
