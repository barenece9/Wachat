package com.wachat.customViews;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wachat.R;

/**
 * Created by Priti Chatterjee on 19-08-2015.
 */
public class ChatCustomRelative extends RelativeLayout implements TextWatcher {

    private Context context;
    private TextView tv_count;
    public EditText et_name;
    public ImageView iv_smiley;
    public int count = 25;

    public ChatCustomRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public ChatCustomRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ChatCustomRelative(Context context) {
        super(context);
        this.context = context;
        init();
    }


    private void init() {
        (LayoutInflater.from(getContext())).inflate(R.layout.layout_custom_realtive, this, true);
        this.tv_count = (TextView) this.findViewById(R.id.tv_count);
        this.et_name = (EditText) this.findViewById(R.id.et_name);
        this.iv_smiley=(ImageView)this.findViewById(R.id.iv_smiley);
        this.tv_count.setText(String.valueOf(count));
        setTextFilter();
    }

    private void setTextFilter() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(count);
        this.et_name.setFilters(filters);
        this.et_name.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int remains = count - this.et_name.getText().toString().trim().length();
        if (remains >= 0) {
            this.tv_count.setText(String.valueOf(remains));
        }
    }
}
