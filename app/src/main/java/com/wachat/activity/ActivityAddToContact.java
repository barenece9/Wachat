package com.wachat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.adapter.AdapterAddToContact;
import com.wachat.application.BaseActivity;
import com.wachat.data.DataTextChat;
import com.wachat.dataClasses.ContactSetget;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;

import java.util.ArrayList;

public class ActivityAddToContact extends BaseActivity implements View.OnClickListener {
    public Toolbar appBar;
    private ListView contact_ph_num_list;
    private TextView tv_Headername, tv_add_to_contact;
    private ImageView iv_contact_img;
    private ArrayList<ContactSetget> mArrayList = new ArrayList<ContactSetget>();
    private AdapterAddToContact mAdapterAddtoContact;
    DataTextChat mDataTextChat;
    ContactSetget mContactSetget;
    ArrayList<ContactSetget> mDataTextChatArrayList = new ArrayList<ContactSetget>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_contact);
        initComponent();

        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        getIntentValue();
        initView();
        initClickListeners();
    }

    private void getIntentValue() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null && mBundle.containsKey(Constants.B_RESULT))
            mDataTextChat = (DataTextChat) mBundle.getSerializable(Constants.B_RESULT);
        String[] temp = TextUtils.split(mDataTextChat.getAttachContactNo(), ",");

        if (!TextUtils.isEmpty(mDataTextChat.getAttachContactNo())) {

            for (int i = 0; i < temp.length; i++) {
                mContactSetget = new ContactSetget();
                mContactSetget.setContactType("Mobile");
                mContactSetget.setContactNumber(temp[i]);
                mContactSetget.setContactName(mDataTextChat.getAttachContactName());
                mContactSetget.setmBitmap(mDataTextChat.getAttachBase64str4Img());
                mDataTextChatArrayList.add(mContactSetget);
            }

        }
    }


    private void initView() {
        contact_ph_num_list = (ListView) findViewById(R.id.contact_ph_num_list);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.inflate_contact_detatails_header, contact_ph_num_list, false);
        contact_ph_num_list.addHeaderView(header, null, false);
        tv_Headername = (TextView) header.findViewById(R.id.tv_name);
        tv_Headername.setText(CommonMethods.getUTFDecodedString(mDataTextChat.getAttachContactName()));
        iv_contact_img = (ImageView) header.findViewById(R.id.iv_contact_img);
        if (!TextUtils.isEmpty(mContactSetget.getmBitmap()))
            iv_contact_img.setImageBitmap(CommonMethods.decodeBase64(mContactSetget.getmBitmap()));

        mAdapterAddtoContact = new AdapterAddToContact(this, mDataTextChatArrayList);
        contact_ph_num_list.setAdapter(mAdapterAddtoContact);

        tv_add_to_contact = (TextView) findViewById(R.id.tv_add_to_contact);

    }

    private void initClickListeners() {
        tv_add_to_contact.setOnClickListener(this);

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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_add_to_contact:
                addAsContactConfirmed(this, mContactSetget);

                finish();
                break;
            default:
                break;

        }
    }


    /**
     * Open the add-contact screen with pre-filled info
     *
     * @param context Activity context
     * @param person  {@linkPerson} to add to contacts list
     */
    public static void addAsContactConfirmed(Context context, ContactSetget person) {

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, CommonMethods.getUTFDecodedString(person.getContactName()));
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, person.getContactNumber());

        context.startActivity(intent);


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


}
