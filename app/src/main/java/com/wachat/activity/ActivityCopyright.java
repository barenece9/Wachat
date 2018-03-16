package com.wachat.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.application.BaseActivity;
import com.wachat.util.WebContstants;

/**
 * Created by Priti Chatterjee on 20-08-2015.
 */
public class ActivityCopyright extends BaseActivity implements View.OnClickListener{
    TextView copyright_tv;
    public Toolbar appBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);
        initComponent();
        initViews();
    }

    private void initViews() {
    //Commit test
    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.copyright_info));
        TextView  copyright_tv_version_number = (TextView) findViewById(R.id.copyright_tv_version_number);
        copyright_tv = (TextView) findViewById(R.id.copyright_tv);

//        copyright_tv.setPaintFlags(copyright_tv.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        String info = copyright_tv.getText().toString();

        SpannableString spanString = new SpannableString(info);
//        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
//        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        copyright_tv.setText(spanString);
        copyright_tv.setOnClickListener(this);
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(),
                    0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }

        copyright_tv_version_number.setText(TextUtils
                .isEmpty(versionName) ? "v 1.0.0" : "" + "v "+versionName);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.copyright_tv:

                showDialog();
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    void showDialog(){
//        CopyRightDialog copyRightDialog = new CopyRightDialog();
//        copyRightDialog = new CopyRightDialog();
//        copyRightDialog.show(getSupportFragmentManager(), CopyRightDialog.class.getSimpleName());

//        try {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(WebContstants.copyright));
//            startActivityForResult(intent, 1234);
//        } catch (Exception e) {
//            e.printStackTrace();
//            ToastUtils.showAlertToast(ActivityCopyright.this, getString(R.string.alert_failure_yahoo_not_found), ToastType.FAILURE_ALERT);
//        }

        Intent mIntent1 = new Intent(this, ActivityAbout.class);
        Bundle bundle = new Bundle();
        bundle.putString(ActivityAbout.TARGET_URL, WebContstants.copyright);
        bundle.putString(ActivityAbout.TITLE,getString(R.string.intellectual_property_rights));
        mIntent1.putExtras(bundle);
        startActivity(mIntent1);
    }
}
