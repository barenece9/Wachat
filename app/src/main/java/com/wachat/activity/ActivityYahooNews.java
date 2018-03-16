package com.wachat.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;

import com.wachat.R;
import com.wachat.adapter.AdapterYahooNewsList;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncGetYahooNewsList;
import com.wachat.data.DataYahooNews;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.LogUtils;

import java.util.ArrayList;

/**
 * Created by Sanjit on 10-02-2016.
 */
public class ActivityYahooNews extends BaseActivity {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(ActivityYahooNews.class);
    public Toolbar appBar;
    private RecyclerView rv;
    private LinearLayoutManager mLayoutManager;
    private AdapterYahooNewsList mAdapterYahooNews;
    private AsyncGetYahooNewsList asyncGetYahooNewsList=null;
    private ArrayList<DataYahooNews> dataYahooNewsList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yahoo_news);
        initComponent();
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        iniViews();
        showHideOverLayProgressLoad(true, true, "");
        callWebserviceForYahooNews();
    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
//        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.ActivityYahooNewsList));

    }

    private void iniViews() {

        rv = (RecyclerView) findViewById(R.id.rv);
        mLayoutManager = new LinearLayoutManager(ActivityYahooNews.this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager);
        mAdapterYahooNews = new AdapterYahooNewsList(this);
        mAdapterYahooNews.setClickListener(new AdapterYahooNewsList.OnItemClickListener() {
            @Override
            public void onClick(DataYahooNews itemData, int itemPosition) {
                onNewsSelect(itemData, itemPosition);
            }
        });
        rv.setAdapter(mAdapterYahooNews);

//        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mLayoutManager
//                        .getItemCount() - 1) {
//                    paginateList();
//                }
//            }
//        });

    }

    private void onNewsSelect(DataYahooNews itemData, int itemPosition) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.B_RESULT,itemData);
        resultIntent.putExtras(bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

//    private void paginateList() {
//        Log.d("YouTubeVideoList", "Paginate List: " + youtubeVideoList.getList().size());
//        showHidePaginationLoading(true);
//        getVideoListFromYouTube(youtubeVideoList);
//    }

//    private void showHidePaginationLoading(boolean show) {
//        findViewById(R.id.rl_pagination_loading).setVisibility(show ? View.VISIBLE : View.GONE);
//    }

    private void showHideOverLayProgressLoad(boolean showOverLay, boolean showProgress, String message) {
        findViewById(R.id.rl_loading).setVisibility(showOverLay ? View.VISIBLE : View.GONE);
        findViewById(R.id.progressBarCircularIndetermininate).setVisibility(showProgress ? View.VISIBLE : View.GONE);

        if (TextUtils.isEmpty(message)) {
            findViewById(R.id.tv_loading_message).setVisibility(View.GONE);
        } else {
            findViewById(R.id.tv_loading_message).setVisibility(View.GONE);
        }
    }


    private void callWebserviceForYahooNews() {
        asyncGetYahooNewsList = new AsyncGetYahooNewsList(new AsyncGetYahooNewsList.YahooNewsListCallBack() {


            @Override
            public void onSuccess(ArrayList<DataYahooNews> dataYahooNewsArrayList) {
                onListFetchCallComplete(dataYahooNewsArrayList,null);
            }

            @Override
            public void onFailure(Exception e) {
                onListFetchCallComplete(null,e);
            }
        });

        if (CommonMethods.checkBuildVersionAsyncTask()) {
            asyncGetYahooNewsList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncGetYahooNewsList.execute();
        }
    }

    private void onListFetchCallComplete(ArrayList<DataYahooNews> list, Exception e) {

        if (e != null) {
            showHideOverLayProgressLoad(true, false, CommonMethods.getAlertMessageFromException(this, e));
            return;
        }

        this.dataYahooNewsList = list;

        if (dataYahooNewsList != null && dataYahooNewsList.size() > 0) {
            LogUtils.i(LOG_TAG, "List: " + dataYahooNewsList.size());
            showHideOverLayProgressLoad(false, false, "");
            mAdapterYahooNews.refreshList(dataYahooNewsList);
        } else {
            LogUtils.i(LOG_TAG, getString(R.string.no_data));
            showHideOverLayProgressLoad(true, false, getString(R.string.no_data));
        }
        asyncGetYahooNewsList = null;
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
        if (asyncGetYahooNewsList != null) {
            asyncGetYahooNewsList.cancel(true);
            asyncGetYahooNewsList = null;
        }
        super.onDestroy();
    }

}
