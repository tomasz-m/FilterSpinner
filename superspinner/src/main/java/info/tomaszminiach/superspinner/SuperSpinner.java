package info.tomaszminiach.superspinner;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

@SuppressWarnings("unused")
public class SuperSpinner extends FrameLayout {

    //only package private fields
    Context mContext;
    boolean isFilterable = false;
    ListAdapter mAdapter;
    String emptyText = "no data";
    FilterListener filterListener;
    CallbackFilterListener callbackFilterListener;
    ListView list;
    View hintView;
    AdapterView.OnItemSelectedListener onItemSelectedListener;
    Object selectedItem;
    View searchIcon, searchProgressBar;

    OnClickListener internalOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.view_superspinner_popup, SuperSpinner.this, false);

            searchIcon = layout.findViewById(R.id.searchImage);
            searchProgressBar = layout.findViewById(R.id.progressBar);
            showState(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(layout);
            final AlertDialog dialog = builder.create();
            dialog.show();
            TextView emptyView = (TextView) layout.findViewById(R.id.emptyView);
            emptyView.setText(emptyText);
            list = (ListView) layout.findViewById(R.id.listView);
            list.setEmptyView(emptyView);
            list.setAdapter(mAdapter);
            final EditText filterEditText = (EditText) layout.findViewById(R.id.editText);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectItem(i);
                    dialog.dismiss();
                    if (onItemSelectedListener != null)
                        onItemSelectedListener.onItemSelected(adapterView, view, i, l);
                }
            });

            if (isFilterable) {
                filterEditText.setVisibility(VISIBLE);
                filter(null);//initial request
                filterEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        filter(filterEditText.getText().toString());
                    }
                });
            } else {
                filterEditText.setVisibility(GONE);
            }
        }
    };

    void showState(boolean isSearching){
        if(searchIcon==null || searchProgressBar==null)
            return;
        if(isSearching){
            searchProgressBar.setVisibility(VISIBLE);
            searchIcon.setVisibility(GONE);
        }else{
            searchProgressBar.setVisibility(GONE);
            searchIcon.setVisibility(VISIBLE);
        }
    }

    void filter(String text){
        if (filterListener != null) {
            mAdapter = filterListener.onFilter(text);
            list.setAdapter(mAdapter);
        }
        if (callbackFilterListener != null) {
            showState(true);
            ListAdapter result = callbackFilterListener.onFilter(text, registerCallback());
        }
    }

    Callback registerCallback() {
        return new Callback() {

            @Override
            public void provideAdapter(ListAdapter adapter) {
                showState(false);
                mAdapter = adapter;
                list.setAdapter(mAdapter);
            }
        };
    }

    /**
     * be careful when using this method - make sure that the requested position is valid for
     * adapter which is currently assigned (every filtering can replace adapter)
     * @param i position of item in adapter
     */
    public void selectItem(int i) {
        removeAllViews();
        addView(mAdapter.getView(i, null, SuperSpinner.this));
        selectedItem = mAdapter.getItem(i);
    }

    /**
     * This spinner do not require any adapter assigned - it can request for it only when it is needed.
     * You can set the selected item by passing just this item and corresponding view.
     * @param selectedItem object
     * @param view view
     */
    public void selectItem(Object selectedItem, View view) {
        removeAllViews();
        addView(view);
        this.selectedItem = selectedItem;
    }

    public int getCount() {
        return mAdapter.getCount();
    }


    public SuperSpinner(Context context) {
        super(context);
        init(context);
    }

    public SuperSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SuperSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SuperSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(final Context context) {
        mContext = context;
        hintView = new TextView(context);
        ((TextView) hintView).setText(R.string.select);
        hintView.setPadding(5, 5, 5, 5);
        removeAllViews();
        addView(hintView);
        setOnClickListener(internalOnClickListener);
    }

    public boolean isFilterable() {
        return isFilterable;
    }

    public void setFilterable(boolean IS_FILTERABLE) {
        this.isFilterable = IS_FILTERABLE;
    }

    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void swapAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
    }

    public FilterListener getFilterListener() {
        return filterListener;
    }

    public void setFilterListener(FilterListener filterListener) {
        this.filterListener = filterListener;
    }

    public void setCallbackFilterListener(CallbackFilterListener callbackFilterListener) {
        this.callbackFilterListener = callbackFilterListener;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    /**
     * Set the view for not initial spinner state when nothing is selected.
     * By default its a simple "select" text with small padding
     * @param v hint view
     */
    public void setHintView(View v) {
        removeAllViews();
        hintView = v;
        addView(hintView);
    }

    /**
     * Set the view for not initial spinner state when nothing is selected.
     * By default its a simple "select" text with small padding
     * @param resourceId resource id of hint view
     */
    public void setHintView(int resourceId) {
        removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        hintView = layoutInflater.inflate(resourceId, this);
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    public interface FilterListener {
        ListAdapter onFilter(String text);
    }

    public interface CallbackFilterListener {
        ListAdapter onFilter(String text, Callback callback);
    }

    public interface Callback {
        void provideAdapter(ListAdapter adapter);
    }

}
