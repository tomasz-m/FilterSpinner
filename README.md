# Super Spinner
Custom android view to replace standard spinner. 

It gives you filtering options for any adapter and data source you use.
There is also default state with nothing selected.

![Alt text](/img/spinner1.png?raw=true)

![Alt text](/img/spinner2.png?raw=true)

## Adding to your project

In your top-level `build.gradle` add line
```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" } //<==line to add
    }
}
```
In your app-module-level `build.gradle` add
```groovy
android {
    ....
}
dependencies {
    compile 'com.github.tomasz-m:filterspinner:1.3.1' //<==line to add
}
```

You can of course build whole project from this repo (with example how to use it).


##Using
Add to your layout
```xml
<info.tomaszminiach.superspinner.SuperSpinner
    style="@style/Base.Widget.AppCompat.Spinner.Underlined"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:id="@+id/superSpinner"/>
```
        

In your Activity/Fragment/View
```java
final SuperSpinner superSpinner = (SuperSpinner) findViewById(R.id.superSpinner);
```
        
You can set adpaters and listeners as for normal spinner
```java
superSpinner.setAdapter(simpleAdapter);
superSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("On Item Selected",superSpinner.getSelectedItem().toString());
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
});
```
But you can set add filters
```java
superSpinner.setFilterable(true);
```
VERSION 1 - use for fast updates on the main thread
```java
superSpinner.setFilterListener(new SuperSpinner.FilterListener() {
    @Override
    public ListAdapter onFilter(String text) {
        return new ArrayAdapter<String>(MainActivity.this,android.R.layout.test_list_item,dataProvider.getItems(text));
    }
});
```            
VERSION 2 - use for longer running filterings
```java
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
```
