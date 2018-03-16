package com.wachat.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.adapter.AdapterSelectLanguage;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncLanguages;
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataLanguages;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.TableLanguage;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;

public class ActivitySelectLanguage extends BaseActivity implements AdapterCallback {

    private RecyclerView rv;

    private String searchText = "";
    public Toolbar appBar;
    private AdapterSelectLanguage mAdapterSelectLanguage;
    private EditText et_search_hint;
    private ArrayList<DataLanguages> mArrayList = new ArrayList<DataLanguages>();

    private View progressBarCircularIndetermininate;
    private boolean isApiCalling = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        initComponent();
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initViews();
        initClickListeners();

        getLanguageFromDB(searchText);
        getLanguageFromAPI();

    }

    private void getLanguageFromDB(String srchTXT) {
        mArrayList = new TableLanguage(this).getLangList(srchTXT);
        if(!isApiCalling){
            progressBarCircularIndetermininate.setVisibility(View.GONE);
        }
        mAdapterSelectLanguage.refreshList(mArrayList);
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

    private void getLanguageFromAPI() {
        if (!CommonMethods.isOnline(this)) {
            ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet),
                    ToastType.FAILURE_ALERT);
            isApiCalling = false;
            progressBarCircularIndetermininate.setVisibility(View.GONE);
            return;
        }

        progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
        isApiCalling = true;
        AsyncLanguages mAsyncLanguages = new AsyncLanguages(new InterfaceResponseCallback() {
            @Override
            public void onResponseObject(Object mObject) {
                isApiCalling = false;
                getLanguageFromDB(searchText);
            }

            @Override
            public void onResponseList(ArrayList<?> mList) {
                try {
                    new TableLanguage(getApplicationContext()).
                            insertAllLang((ArrayList<DataLanguages>) mList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isApiCalling = false;
                getLanguageFromDB(searchText);
            }

            @Override
            public void onResponseFaliure(String responseText) {
                isApiCalling = false;
                ToastUtils.showAlertToast(ActivitySelectLanguage.this,
                        TextUtils.isEmpty(responseText) ?
                                getResources().getString(
                                        R.string.alert_failure_server_connection_failed_) :
                                responseText,
                        ToastType.FAILURE_ALERT);
                getLanguageFromDB(searchText);
            }
        });
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncLanguages.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncLanguages.execute();
        }
    }


    private void performSearch(String s) {
        if (!TextUtils.isEmpty(s)) {
            searchText = s;
        }

        getLanguageFromDB(searchText);
    }

    private void initViews() {
        RelativeLayout rel_search = (RelativeLayout) findViewById(R.id.rel_search);
        et_search_hint = (EditText) rel_search.findViewById(R.id.et_search_hint);
        et_search_hint.setHint(R.string.select_lang_hint);
        progressBarCircularIndetermininate = findViewById(R.id.progressBarCircularIndetermininate);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(ActivitySelectLanguage.this, LinearLayoutManager.VERTICAL, false));
        mAdapterSelectLanguage = new AdapterSelectLanguage(this, mArrayList, this);
        rv.setAdapter(mAdapterSelectLanguage);

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
    protected void CommonMenuClcik() {

    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.selectLanguage));
    }

    @Override
    public void OnClickPerformed(Object mObject) {
        Intent mIntent = new Intent();
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.B_RESULT, ((DataLanguages) mObject).getName());
        mBundle.putString(Constants.B_CODE, ((DataLanguages) mObject).getLanguage());

        mIntent.putExtras(mBundle);
        setResult(RESULT_OK, mIntent);
        finish();
    }

    @Override
    public void onPagination() {

    }

}
