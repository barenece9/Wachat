package com.wachat.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.adapter.AdapterContactDetails;
import com.wachat.application.BaseActivity;
import com.wachat.dataClasses.ContactSetget;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;

public class ActivityContactDetails extends BaseActivity implements View.OnClickListener {
    public Toolbar appBar;
    private ListView contact_ph_num_list;
    private TextView tv_Headername, tv_save, tv_cancel;
    private ImageView iv_contact_img;
    private ArrayList<ContactSetget> mArrayList = new ArrayList<ContactSetget>();
    private AdapterContactDetails mAdapterContactDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Constants.ContactPickerChat);
        initComponent();
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initView();
        initClickListeners();
    }


    private void initView() {
        contact_ph_num_list = (ListView) findViewById(R.id.contact_ph_num_list);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.inflate_contact_detatails_header, contact_ph_num_list, false);
        contact_ph_num_list.addHeaderView(header, null, false);
        tv_Headername = (TextView) header.findViewById(R.id.tv_name);
        iv_contact_img = (ImageView) header.findViewById(R.id.iv_contact_img);

        mAdapterContactDetails = new AdapterContactDetails(this, mArrayList);
        contact_ph_num_list.setAdapter(mAdapterContactDetails);

        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_save.setText(getResources().getString(R.string.send));
    }

    private void initClickListeners() {
        tv_save.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initSuperViews();
        tv_act_name.setText(R.string.contact_details);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (Constants.ContactPickerChat):
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri contactData = data.getData();
                        mArrayList = CommonMethods.getContact(this, contactData);
                        if(mArrayList.size()!=0) {
                            mAdapterContactDetails.refreshList(mArrayList);
                            tv_Headername.setText(mArrayList.get(0).getContactName());
                            if (!TextUtils.isEmpty(mArrayList.get(0).getmBitmap()))
                                iv_contact_img.setImageBitmap(CommonMethods.decodeBase64(mArrayList.get(0).getmBitmap()));
                            // mDataContact.getContactName();
                            //  ToastUtils.showAlertToast(mActivity, "Contact fetched", ToastType.SUCESS_ALERT);
                        }else{
                            ToastUtils.showAlertToast(this, getString(R.string.cotact_pick_no_number),
                                    ToastType.FAILURE_ALERT);
                            finish();
                        }
                    }
                } else {
                    finish();
                }
                break;


            default:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_save:

                    ContactSetget mContactSetget
                            = mAdapterContactDetails.getCheckedValue();
                if (mContactSetget!=null&&!TextUtils.isEmpty(mContactSetget.getContactNumber())) {
                    Intent resultIntent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(Constants.B_RESULT, mContactSetget);
                    resultIntent.putExtras(mBundle);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    ToastUtils.showAlertToast(ActivityContactDetails.this, getString(R.string.contact_pick_no_number), ToastType.FAILURE_ALERT);

                }
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
}
