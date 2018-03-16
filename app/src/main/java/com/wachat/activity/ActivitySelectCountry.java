package com.wachat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.adapter.AdapterSelectCountry;
import com.wachat.application.BaseActivity;
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataCountry;
import com.wachat.storage.TableCountry;
import com.wachat.util.Constants;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 25-08-2015.
 */
public class ActivitySelectCountry extends BaseActivity implements AdapterCallback {

    private RelativeLayout rel_search;
    private EditText et_search_hint;
    public Toolbar appBar;
    private ListView lv_country;
    private ImageView iv_cross;
    private ArrayList<DataCountry> mListCountry;
    private AdapterSelectCountry mAdapter;
    private String searchTxt = "";
    private TableCountry mTableCountry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_select_country);


        mTableCountry = new TableCountry(this);
        initComponent();
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        getDataFromDB();
        // country_names = this.getResources().getStringArray(R.array.CountryCodes);
        initViews();
        initClickListeners();

    }

    private void initClickListeners() {

        et_search_hint.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString());
                    return true;
                }
                return false;
            }


        });


        et_search_hint.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                performSearch(String.valueOf(s));
            }
        });
    }

    private void getDataFromDB() {
        mListCountry = mTableCountry.getList(searchTxt);
        mAdapter = new AdapterSelectCountry(this, mListCountry, this);
    }

    private void initViews() {
        rel_search = (RelativeLayout) findViewById(R.id.rel_search);
        et_search_hint = (EditText) rel_search.findViewById(R.id.et_search_hint);
        et_search_hint.setHint(getResources().getString(R.string.search_your_country));
        iv_cross = (ImageView) findViewById(R.id.iv_cross);
        iv_cross.setOnClickListener(this);


        lv_country = (ListView) findViewById(R.id.lv_country);
        lv_country.setAdapter(mAdapter);
        lv_country.setEmptyView(findViewById(android.R.id.empty));
        lv_country.setFastScrollEnabled(true);

    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String result) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void CommonMenuClcik() {

    }


    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.selectCountry));


    }

    private void performSearch(String searchText) {

        mListCountry = mTableCountry.getList(searchText);
        mAdapter.refreshList(mListCountry);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_cross:
                et_search_hint.setText("");
                break;
        }
    }

    @Override
    public void OnClickPerformed(Object mObject) {

        // ToastUtils.showAlertToast(mActivity, ((DataCountry)mObject).getCountryName(), ToastType.SUCESS_ALERT);
        Intent mIntent = new Intent();
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.B_RESULT, ((DataCountry) mObject));
        mIntent.putExtras(mBundle);
        setResult(0, mIntent);
        finish();
    }

    @Override
    public void onPagination() {

    }

    @Override
    public void onCountryListFetchSuccess(ArrayList<DataCountry> mListCountries) {
        super.onCountryListFetchSuccess(mListCountries);
        this.mListCountry = mListCountries;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter = new AdapterSelectCountry(ActivitySelectCountry.this, mListCountry, ActivitySelectCountry.this);
                lv_country.setAdapter(mAdapter);
            }
        });
    }
}
