package com.wachat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.adapter.AdapterImageSelection;
import com.wachat.adapter.AdapterPagerImage;
import com.wachat.application.BaseActivity;
import com.wachat.customViews.ChatGridLayoutManager;
import com.wachat.data.DataShareImage;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 29-08-2015.
 */
public class ActivityShareImage extends BaseActivity implements AdapterImageSelection.StartActivityResult, View.OnClickListener {

    public Toolbar appBar;
    private RecyclerView rv;
    private ViewPager vp;
    private TextView tvCancel, tvSend;
    private ArrayList<DataShareImage> mListString = new ArrayList<DataShareImage>();
    private AdapterPagerImage mAdapterPagerImage;
    private AdapterImageSelection mAdapter;
    //    public static ArrayList<GallerySetGet> arrGallerySetGets_AllImages;
    private Constants.SELECTION mSelection;
    private int width;
    private ChatGridLayoutManager mChatGridLayoutManager;
    private EditText et_caption;
    private boolean isCamera = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_image);
//
        initComponent();
//        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        getBundleData();
        initView();
        initCaption(mListString);
        initListener();
        initAdapter(mListString);
    }

    private void initListener() {

        et_caption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListString.get(vp.getCurrentItem()).setCaption(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initCaption(ArrayList<DataShareImage> mListString) {
        et_caption.setText(mListString.get(vp.getCurrentItem()).getCaption().toString());

    }


    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
//        appBar.setLogo(null);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.shareImage));
//        tv_act_name.setGravity(Gravity.LEFT);
        mListString = new ArrayList<DataShareImage>();
    }

    private void getBundleData() {
        Bundle mBundle = getIntent().getExtras();

        if (mBundle != null && mBundle.getBoolean(Constants.B_CAMERA_INMAGE)) {
            if (mBundle != null && mBundle.containsKey("cameraImage")) {
                this.isCamera = true;
                DataShareImage mDataShareImage = (DataShareImage) mBundle.getSerializable("cameraImage");
                mListString.add(mDataShareImage);
            }
        } else {
            if (mBundle != null && mBundle.containsKey(Constants.B_TYPE)) {
                this.isCamera = false;
                mSelection = (Constants.SELECTION) mBundle.getSerializable(Constants.B_TYPE);
                DataShareImage mDataShareImage = new DataShareImage();
                mDataShareImage.setImgUrl(mBundle.getString(Constants.B_RESULT));
                mListString.add(mDataShareImage);
            }
        }

    }

    private void initView() {
        et_caption = (EditText) findViewById(R.id.et_caption);
        vp = (ViewPager) findViewById(R.id.vp);
        tvSend = (TextView) findViewById(R.id.tvSend);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        rv = (RecyclerView) findViewById(R.id.sec);
        mChatGridLayoutManager = new ChatGridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
//        mChatGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (mListString.size() > 5)
//                    return 5;
//                else
//                    return 4;
//            }
//        });
        rv.setLayoutManager(mChatGridLayoutManager);

        tvSend.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        lnr_right.setOnClickListener(this);
    }

    private void initAdapter(ArrayList<DataShareImage> mLocalListString) {
        width = CommonMethods.getScreenWidth(this).widthPixels;
        width = width / 5;
        mAdapter = new AdapterImageSelection(this, mImageLoader, mLocalListString, width, this, isCamera);
        rv.setAdapter(mAdapter);
        //rv.addItemDecoration(new DividerItemDecoration(getBaseContext(),android.R.attr.actionModeStyle));
        mAdapterPagerImage = new AdapterPagerImage(this, mLocalListString, mImageLoader);
        vp.setAdapter(mAdapterPagerImage);
        mAdapterPagerImage.notifyDataSetChanged();
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                et_caption.setText(mListString.get(position).getCaption().toString());
                if (mListString.get(vp.getCurrentItem()).isMasked) {
                    tv_right.setSelected(true);
                } else {
                    tv_right.setSelected(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                finish();

                break;
            case R.id.tvSend:
                Intent mIntent = new Intent();
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(Constants.B_RESULT, mListString);
                mIntent.putExtras(mBundle);
                setResult(Constants.ChatToSelection, mIntent);
                finish();
                break;


            //top section
            case R.id.iv_tick:

                if (mListString != null && mListString.size() == 1) {
                    mListString.remove(vp.getCurrentItem());
//                    initAdapter(mListString);
                    finish();
                } else {
                    mListString.remove(vp.getCurrentItem());
                    initAdapter(mListString);
                }

                break;
            case R.id.tv_right:
                //For masking on top button click
                try {
                    if (mListString.get(vp.getCurrentItem()).isMasked) {
                        mListString.get(vp.getCurrentItem()).setIsMasked(false);
                        tv_right.setSelected(false);
                    } else {
                        mListString.get(vp.getCurrentItem()).setIsMasked(true);
                        tv_right.setSelected(true);
                    }

                    int currPOs = vp.getCurrentItem();
                    mAdapterPagerImage.updateAdapter(mListString);
//                    initAdapter(mListString);
                    vp.setCurrentItem(currPOs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                super.onClick(v);
                break;
        }
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
    public void startActivity() {
        Intent mIntent = new Intent(this, ActivityGallery.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.B_TYPE, Constants.SELECTION.SELECTION_TO_IMAGE);
        mBundle.putSerializable(Constants.B_RESULT, mListString);
        mIntent.putExtras(mBundle);
        startActivityForResult(mIntent, Constants.ImagePickerSelection);
    }

    @Override
    public void changeSelectionOnPager(int pos) {
        vp.setCurrentItem(pos);

        et_caption.setText(mListString.get(pos).getCaption().toString());
        if (mListString.get(vp.getCurrentItem()).isMasked) {
            tv_right.setSelected(true);
        } else {
            tv_right.setSelected(false);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ImagePickerSelection && resultCode == Constants.ImageResultSelection) {
            if (data != null && data.getExtras() != null) {
                Bundle mBundle = data.getExtras();
//                ArrayList<DataShareImage> list =
                mListString = (ArrayList<DataShareImage>) mBundle.getSerializable(Constants.B_RESULT);
                mAdapter.updateAdapter(mListString);
//                mAdapter = new AdapterImageSelection(mActivity, mImageLoader, mListString, width, this,isCamera);
//                rv.setAdapter(mAdapter);
//                mAdapterPagerImage = new AdapterPagerImage(mActivity, mListString, mImageLoader);
//                vp.setAdapter(mAdapterPagerImage);
                mAdapterPagerImage.updateAdapter(mListString);
//                vp.setOffscreenPageLimit(mListString.size());
                vp.setCurrentItem(mListString.size());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
