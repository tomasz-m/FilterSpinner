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

    //private int maxCount;
    private Context mContext;
    private boolean IS_FILTERABLE = false;
    private ListAdapter mAdapter;
    private String emptyText = "no data";
    private FilterListener filterListener;
    private CallbackFilterListener callbackFilterListener;
    private ListView list;
    private View hintView;
    private AdapterView.OnItemSelectedListener onItemSelectedListener;
    private Object selectedItem;
    private int selectedItemPosition;

    private OnClickListener internalOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.view_superspinner_popup, SuperSpinner.this, false);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(layout);
            final AlertDialog dialog = builder.create();
            dialog.show();
            TextView emptyView = (TextView) layout.findViewById(R.id.emptyView);
            emptyView.setText(emptyText);
            list = (ListView) layout.findViewById(R.id.listView);
            list.setEmptyView(emptyView);
            final EditText filterEditText = (EditText) layout.findViewById(R.id.editText);
            list.setAdapter(mAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectItem(i);
                    dialog.dismiss();
                    if (onItemSelectedListener != null)
                        onItemSelectedListener.onItemSelected(adapterView, view, i, l);
                }
            });

            if (IS_FILTERABLE) {
                filterEditText.setVisibility(VISIBLE);
                filterEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (filterListener != null) {
                            mAdapter = filterListener.onFilter(filterEditText.getText().toString());
                            list.setAdapter(mAdapter);
                        }
                        if (callbackFilterListener != null) {
                            ListAdapter result = callbackFilterListener.onFilter(filterEditText.getText().toString(), registerCallback());
                        }
                    }
                });
            } else {
                filterEditText.setVisibility(GONE);
            }
        }
    };

    private Callback registerCallback() {
        return new Callback() {

            @Override
            public void provideAdapter(ListAdapter adapter) {
                mAdapter = adapter;
                list.setAdapter(mAdapter);
            }
        };
    }

    public void selectItem(int i) {
        selectedItemPosition = i;
        removeAllViews();
        addView(mAdapter.getView(i, null, SuperSpinner.this));
        selectedItem = mAdapter.getItem(i);
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
        ((TextView) hintView).setText("select");
        hintView.setPadding(5, 5, 5, 5);
        removeAllViews();
        addView(hintView);
        setOnClickListener(internalOnClickListener);
    }

    public boolean isFilterable() {
        return IS_FILTERABLE;
    }

    public void setFilterable(boolean IS_FILTERABLE) {
        this.IS_FILTERABLE = IS_FILTERABLE;
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

    public void setHintView(View v) {
        removeAllViews();
        hintView = v;
        addView(hintView);
    }

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
