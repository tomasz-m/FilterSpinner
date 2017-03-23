package info.tomaszminiach.superspinner;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
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
    boolean isFilterable = false;
    ListAdapter mAdapter;
    String emptyText = "no data";
    FilterListener filterListener;
    CallbackFilterListener callbackFilterListener;
    View hintView;
    AdapterView.OnItemSelectedListener onItemSelectedListener;
    Object selectedItem;
    OnClickListener externalOnClickListener;
    PopupViewHolder popupViewHolder;



    OnClickListener internalOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            showPopup();

            if (externalOnClickListener != null) {
                externalOnClickListener.onClick(view);
            }
        }
    };

    void showPopup(){
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.view_superspinner_popup, SuperSpinner.this, false);

        popupViewHolder = new PopupViewHolder();

        popupViewHolder.searchIcon = layout.findViewById(R.id.searchImage);
        popupViewHolder.searchProgressBar = layout.findViewById(R.id.progressBar);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                popupViewHolder=null;
            }
        });
        dialog.show();

        popupViewHolder.emptyView = (TextView) layout.findViewById(R.id.emptyView);
        popupViewHolder.emptyView.setGravity(Gravity.CENTER);
        popupViewHolder.emptyView.setText(emptyText);
        popupViewHolder.list = (ListView) layout.findViewById(R.id.listView);
        popupViewHolder.list.setEmptyView(popupViewHolder.emptyView);
        popupViewHolder.list.setAdapter(mAdapter);
        showState(false);

        final EditText filterEditText = (EditText) layout.findViewById(R.id.editText);
        View searchLayout = layout.findViewById(R.id.searchLayout);

        popupViewHolder.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectItem(i);
                dialog.dismiss();
                if (onItemSelectedListener != null)
                    onItemSelectedListener.onItemSelected(adapterView, view, i, l);
            }
        });

        if (filterListener != null || callbackFilterListener != null) {
            searchLayout.setVisibility(VISIBLE);
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
            searchLayout.setVisibility(GONE);
        }
    }

    public OnClickListener getExternalOnClickListener() {
        return externalOnClickListener;
    }

    @Override
    public void setOnClickListener(OnClickListener externalOnClickListener) {
        this.externalOnClickListener = externalOnClickListener;
    }

    void showState(boolean isSearching) {
        if (popupViewHolder==null)
            return;
        if (isSearching) {
            popupViewHolder.emptyView.setVisibility(INVISIBLE);
            popupViewHolder.searchProgressBar.setVisibility(VISIBLE);
            popupViewHolder.searchIcon.setVisibility(GONE);
        } else {
            popupViewHolder.emptyView.setVisibility(VISIBLE);
            popupViewHolder.searchProgressBar.setVisibility(GONE);
            popupViewHolder.searchIcon.setVisibility(VISIBLE);
        }
    }

    void filter(String text) {
        if(popupViewHolder==null)
            return;
        if (filterListener != null) {
            mAdapter = filterListener.onFilter(text);
            popupViewHolder.list.setAdapter(mAdapter);
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
                if(popupViewHolder==null)
                    return;
                showState(false);
                mAdapter = adapter;
                popupViewHolder.list.setAdapter(mAdapter);
            }
        };
    }

    /**
     * be careful when using this method - make sure that the requested position is valid for
     * adapter which is currently assigned (every filtering can replace adapter)
     *
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
     *
     * @param selectedItem object
     * @param view         view
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

    void init(final Context context) {
        hintView = new TextView(context);
        ((TextView) hintView).setText(R.string.select);
        hintView.setPadding(5, 5, 5, 5);
        removeAllViews();
        addView(hintView);
        super.setOnClickListener(internalOnClickListener);
    }

    public boolean isFilterable() {
        return isFilterable;
    }

    /**
     * set if filter is visible
     *
     * @deprecated The spinner is filterable when one of listeners is not null, and not filterable
     * when both are null. To set them use {@link #setFilterListener(FilterListener)} or
     * {@link #setCallbackFilterListener(CallbackFilterListener)}
     */
    @Deprecated
    public void setFilterable(boolean IS_FILTERABLE) {
        this.isFilterable = IS_FILTERABLE;
    }

    /**
     * Set the adapter for the spinner. The adapter is then passed to internal instance of
     * {@link ListView}.
     * If {@link #setFilterListener(FilterListener)} or
     * {@link #setCallbackFilterListener(CallbackFilterListener)} are used then there might be no
     * need to use this methid.
     *
     * @param adapter adapter to set
     */
    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void swapAdapter(ListAdapter adapter) {
        setAdapter(adapter);
    }

    public FilterListener getFilterListener() {
        return filterListener;
    }

    public void setFilterListener(FilterListener filterListener) {
        this.filterListener = filterListener;
    }

    public CallbackFilterListener getCallbackFilterListener() {
        return callbackFilterListener;
    }

    public void setCallbackFilterListener(CallbackFilterListener callbackFilterListener) {
        this.callbackFilterListener = callbackFilterListener;
    }

    /**
     * Text that is shown when there are no items in the list
     *
     * @param emptyText the shown text
     */
    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
        if (popupViewHolder != null)
            popupViewHolder.emptyView.setText(emptyText);
    }

    /**
     * Set the view for not initial spinner state when nothing is selected.
     * By default its a simple "select" text with small padding
     *
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
     *
     * @param resourceId resource id of hint view
     */
    public void setHintView(int resourceId) {
        removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        hintView = layoutInflater.inflate(resourceId, this);
    }

    /**
     * Set the view for not initial spinner state when nothing is selected.
     * By default its a simple "select" text with small padding
     *
     * @param resourceId resource id of hint view
     */
    public void setHintView(int resourceId, int textViewResourceId, String text) {
        setHintView(resourceId);
        ((TextView)hintView.findViewById(textViewResourceId)).setText(text);
    }

    /**
     * Set the view for not initial spinner state when nothing is selected.
     * By default its a simple "select" text with small padding
     *
     * @param resourceId resource id of hint view
     */
    public void setHintView(int resourceId, int textViewResourceId, int textResourceId) {
        setHintView(resourceId,textViewResourceId,getResources().getString(textResourceId));
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    //views on the popup
    class PopupViewHolder {
        ListView list;
        View searchIcon, searchProgressBar;
        TextView emptyView;
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
