package com.wachat.activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.adapter.AdapterYouTubeVideoList;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncGetYouTubeVideoList;
import com.wachat.data.YouTubeVideo;
import com.wachat.data.YouTubeVideoList;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ActivityYouTubeVideoList extends BaseActivity {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivityYouTubeVideoList.class);
    public Toolbar appBar;
    private RecyclerView rv;
    private AdapterYouTubeVideoList mAdapterYouTubeVideoList;
    private YouTubeVideoList youtubeVideoList;
    private AsyncGetYouTubeVideoList asyncGetYouTubeVideoListTask;
    private LinearLayoutManager mLayoutManager;
    private String searchTxt = "";
    private EditText et_search_hint;
    private ImageView iv_cross;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video_list);
        initComponent();
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        iniViews();
        showHideOverLayProgressLoad(true, true, "");
        getVideoListFromYouTube(youtubeVideoList);
        searchClickListener();

    }

    private void searchClickListener() {
        et_search_hint.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        ||actionId == EditorInfo.IME_ACTION_DONE
                        ||actionId == EditorInfo.IME_ACTION_GO) {

                    showHideOverLayProgressLoad(true, true, "");
                    getVideoListFromYouTube(null);
                    CommonMethods.hideSoftKeyboard(ActivityYouTubeVideoList.this);
                    return true;
                }

                return false;
            }
        });

        iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideOverLayProgressLoad(true, true, "");
                et_search_hint.setText("");
                getVideoListFromYouTube(null);
            }
        });

    }


    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
//        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.ActivityYouTubeVideoList));
        et_search_hint = (EditText) findViewById(R.id.et_search_hint);
        iv_cross = (ImageView) findViewById(R.id.iv_cross);
    }

    private void iniViews() {
        rv = (RecyclerView) findViewById(R.id.rv);
        mLayoutManager = new LinearLayoutManager(ActivityYouTubeVideoList.this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        mAdapterYouTubeVideoList = new AdapterYouTubeVideoList(this);
        mAdapterYouTubeVideoList.setClickListener(new AdapterYouTubeVideoList.OnItemClickListener() {
            @Override
            public void onClick(YouTubeVideo itemData, int itemPosition) {
                onVideoSelect(itemData, itemPosition);
            }
        });
        rv.setAdapter(mAdapterYouTubeVideoList);

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mLayoutManager
                        .getItemCount() - 1) {
                    paginateList();
                }
            }
        });

    }

    private void onVideoSelect(YouTubeVideo itemData, int itemPosition) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.B_RESULT, itemData);
        resultIntent.putExtras(bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void paginateList() {
        //   Log.d("YouTubeVideoList", "Paginate List: " + youtubeVideoList.getList().size());
        showHidePaginationLoading(true);
        getVideoListFromYouTube(youtubeVideoList);
    }

    private void showHidePaginationLoading(boolean show) {
        findViewById(R.id.rl_pagination_loading).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showHideOverLayProgressLoad(boolean showOverLay,
                                             boolean showProgress, String message) {
        findViewById(R.id.rl_loading).setVisibility(showOverLay ? View.VISIBLE : View.GONE);
        findViewById(R.id.progressBarCircularIndetermininate).
                setVisibility(showProgress ? View.VISIBLE : View.GONE);

        if (TextUtils.isEmpty(message)) {
            findViewById(R.id.tv_loading_message).setVisibility(View.GONE);
        } else {
            findViewById(R.id.tv_loading_message).setVisibility(View.GONE);
        }
    }

    private void getVideoListFromYouTube(YouTubeVideoList list) {
        // if (!TextUtils.isEmpty(et_search_hint.getText().toString()))
        searchTxt = et_search_hint.getText().toString().trim();
        if(TextUtils.isEmpty(searchTxt)){
            searchTxt = "";
        }
        try {
            searchTxt = URLEncoder.encode(searchTxt, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            searchTxt = et_search_hint.getText().toString().trim();
            searchTxt = searchTxt.replace(" ","+");
        }
//        searchTxt = searchTxt.replace(" ","+");
        asyncGetYouTubeVideoListTask = new AsyncGetYouTubeVideoList(list,searchTxt,
                new AsyncGetYouTubeVideoList.YouTubeVideoListCallBack() {

            @Override
            public void onSuccess(YouTubeVideoList list) {

                onListFetchCallComplete(list, null);
            }

            @Override
            public void onFailure(Exception e) {
                onListFetchCallComplete(null, e);
            }
        });

        if (CommonMethods.checkBuildVersionAsyncTask()) {
            asyncGetYouTubeVideoListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncGetYouTubeVideoListTask.execute();
        }
    }

    private void onListFetchCallComplete(YouTubeVideoList list, Exception e) {
        showHidePaginationLoading(false);
        if (e != null) {
            showHideOverLayProgressLoad(true, false, CommonMethods.getAlertMessageFromException(this, e));
            return;
        }

        this.youtubeVideoList = list;

        if (youtubeVideoList != null && youtubeVideoList.getList().size() > 0) {
            LogUtils.i(LOG_TAG, "Paginate List: " + youtubeVideoList.getList().size());
            showHideOverLayProgressLoad(false, false, "");
            mAdapterYouTubeVideoList.refreshList(youtubeVideoList.getList());
        } else {
            LogUtils.i(LOG_TAG, getString(R.string.no_data));
            showHideOverLayProgressLoad(true, false, getString(R.string.no_data));
        }
        asyncGetYouTubeVideoListTask = null;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    protected void onDestroy() {
        if (asyncGetYouTubeVideoListTask != null) {
            asyncGetYouTubeVideoListTask.cancel(true);
            asyncGetYouTubeVideoListTask = null;
        }
        super.onDestroy();
    }


}
