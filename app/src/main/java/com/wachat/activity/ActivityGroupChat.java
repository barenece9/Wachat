package com.wachat.activity;//package com.wachat.activity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Message;
//import android.os.RemoteException;
//import android.provider.ContactsContract;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.RecyclerView.OnScrollListener;
//import android.support.v7.widget.Toolbar;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.nostra13.universalimageloader.core.assist.FailReason;
//import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//import com.wachat.R;
//import com.wachat.adapter.AdapterChatSection;
//import com.wachat.adapter.AdapterGroupChat;
//import com.wachat.application.BaseActivity;
//import com.wachat.chatUtils.ConstantChat;
//import com.wachat.chatUtils.OneToOneChatJSonCreator;
//import com.wachat.customViews.DividerItemDecoration;
//import com.wachat.data.DataChat;
//import com.wachat.data.DataGroup;
//import com.wachat.data.DataTextChat;
//import com.wachat.dataClasses.GallerySetGet;
//import com.wachat.helper.HelperChat;
//import com.wachat.services.ConstantXMPP;
//import com.wachat.storage.TableChat;
//import com.wachat.storage.TableContact;
//import com.wachat.storage.TableGroup;
//import com.wachat.util.CommonMethods;
//import com.wachat.util.Constants;
//import com.wachat.util.ToastUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * Created by Priti Chatterjee on 18-08-2015.
// */
//public class ActivityGroupChat extends BaseActivity implements HelperChat.HelperCallback, TextWatcher {
//
//    private RecyclerView recycler_view_common;
//    private AdapterGroupChat mAdapter;
//    private AdapterChatSection mSectionedAdapter;
//    private FrameLayout frm_container;
//    private RelativeLayout rel_bottom;
//    public Toolbar appBar;
//
//    private HelperChat mHelperChat;
//
//    private LinearLayoutManager mLinearLayoutManager;
//    private LinearLayout lnr_Section, lnr_Share;
//    private TextView tvSection;
//    private ScrollGesture mScrollGesture;
//    private View view;
//    private ImageView iv_camera;
//    private EditText et_chatText;
//    private String groupId = "";
//    private DataGroup mDataGroup = new DataGroup();
//    private ArrayList<DataChat> mListChats = new ArrayList<DataChat>();
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_group_chat);
//
//        getDataFromIntent();
//        getGroupDataFromDB();
//        initViews();
//        initComponent();
//
//    }
//
//    private void getGroupDataFromDB() {
//        mDataGroup = new TableGroup(mActivity).getGroupById(groupId);
//
//    }
//
//    private void getDataFromIntent() {
//        Bundle mBundle = getIntent().getExtras();
//        if (mBundle != null && mBundle.containsKey(Constants.B_ID))
//            groupId = getIntent().getExtras().getString(Constants.B_ID);
//    }
//
//    /**
//     * Used To initialize the Views
//     */
//    private void initViews() {
//        iv_camera = (ImageView) findViewById(R.id.iv_camera);
//        iv_camera.setOnClickListener(this);
//        et_chatText = (EditText) findViewById(R.id.et_chatText);
//        et_chatText.addTextChangedListener(this);
//        rel_bottom = (RelativeLayout) findViewById(R.id.rel_bottom);
//        lnr_Section = (LinearLayout) findViewById(R.id.lnr_Section);
//        tvSection = (TextView) findViewById(R.id.tvSection);
//        lnr_Share = (LinearLayout) findViewById(R.id.lnr_Share);
//        view = findViewById(R.id.view);
//        view.setVisibility(View.GONE);
//        view.setOnClickListener(this);
//        recycler_view_common = (RecyclerView) findViewById(R.id.rv);
//
//        frm_container = (FrameLayout) findViewById(R.id.frm_container);
//        ViewTreeObserver mViewTreeObserver = frm_container.getViewTreeObserver();
//        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                ViewGroup.MarginLayoutParams marginLayoutParams =
//                        (ViewGroup.MarginLayoutParams) recycler_view_common.getLayoutParams();
//                if (marginLayoutParams.bottomMargin != rel_bottom.getHeight())
//                    marginLayoutParams.bottomMargin = rel_bottom.getHeight();
//
//            }
//        });
//
//        mHelperChat = new HelperChat(mActivity, this);
//    }
//
//    /**
//     * Used to Initialize the Components and Adapters and Adding Scroll Gesture
//     */
//    private void initComponent() {
//        appBar = (Toolbar) findViewById(R.id.appBar);
//        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
//
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_new_group_icon);
//        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, getResources().getInteger(R.integer.imageWH), getResources().getInteger(R.integer.imageWH), true));
//        appBar.setLogo(d);
//
//        if (!mDataGroup.getGroupImage().equals("")) {
//            imagePath = mDataGroup.getGroupImage();
//
//            mImageLoader.displayImage(imagePath, new ImageView(mActivity), new ImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    Drawable d = new BitmapDrawable(getResources(),
//                            CommonMethods.getCircularBitmap(Bitmap.createScaledBitmap(loadedImage, getResources().getInteger(R.integer.imageWH), getResources().getInteger(R.integer.imageWH), true)));
//                    appBar.setLogo(d);
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//
//                }
//            });
//        }
//
//        setSupportActionBar(appBar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
//        initSuperViews();
//        setTopBarValue();
//
//        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
//        recycler_view_common.setLayoutManager(mLinearLayoutManager);
//        recycler_view_common.setHasFixedSize(false);
//        mScrollGesture = new ScrollGesture(lnr_Section, tvSection, mLinearLayoutManager);
//
////        ArrayList<DataChat> mListChat = new ArrayList<DataChat>();
////        for (int i = 0; i < 50; i++) {
////            DataChat mDataChat = new DataChat();
////            mDataChat.setMsg("Hello");
////            mListChat.add(mDataChat);
////        }
//        //will be open when group chat will implement
//        mAdapter = new AdapterGroupChat(mListChats, mImageLoader);
//
//        List<AdapterChatSection.Section> sections =
//                new ArrayList<AdapterChatSection.Section>();
//
//        sections.add(new AdapterChatSection.Section(0, "Section 1"));
////        sections.add(new AdapterChatSection.Section(5, "Section 2"));
////        sections.add(new AdapterChatSection.Section(12, "Section 3"));
////        sections.add(new AdapterChatSection.Section(14, "Section 4"));
////        sections.add(new AdapterChatSection.Section(20, "Section 5"));
//
//        AdapterChatSection.Section[] dummy = new AdapterChatSection.Section[sections.size()];
//        mSectionedAdapter = new
//                AdapterChatSection(this, R.layout.section_chat, R.id.tvSection, mAdapter);
//        mSectionedAdapter.setSections(sections.toArray(dummy));
//
//        recycler_view_common.setAdapter(mSectionedAdapter);
//        recycler_view_common.setOnScrollListener(mScrollGesture);
//        recycler_view_common.addItemDecoration(new DividerItemDecoration(mActivity.getResources().getDrawable(
//                R.drawable.d_line_horizontal), false, false));
//    }
//
//    private void setTopBarValue() {
//        tv_title.setText(mDataGroup.getGroupName());
//
//       ArrayList<String> mArrStr = new ArrayList<String>();
//        for (int i = 0; i < mDataGroup.getMemberdetails().size(); i++) {
//            String nameOrPhone = "";
//            String name = new TableContact(this).getFriendDetailsByID(mDataGroup.getMemberdetails().get(i).getUserId()).getName();
//            if (TextUtils.isEmpty(mDataGroup.getMemberdetails().get(i).getUserName())) {
//                if (TextUtils.isEmpty(name)) {
//                     nameOrPhone =   "+" + mDataGroup.getMemberdetails().get(i).getUserCountryId() + mDataGroup.getMemberdetails().get(i).getUserPhNo();
//                } else
//                     nameOrPhone=  name;
//            } else
//                nameOrPhone= mDataGroup.getMemberdetails().get(i).getUserName();
//            mArrStr.add(nameOrPhone) ;
//
//        }
//        String membername = CommonMethods.commaSeperatedsStringsFromArray(mArrStr);
//        tv_subTitle.setText(membername);
//    }
//
//
//    @Override
//    protected void onLangChange() {
//
//    }
//
//    @Override
//    protected void onNetworkChange(boolean isActive) {
//
//    }
//
//    @Override
//    protected void onSearchPerformed(String result) {
//
//    }
//
//    @Override
//    protected void CommonMenuClcik() {
//        if (lnr_Share.getVisibility() == View.VISIBLE) {
//            lnr_Share.setVisibility(View.GONE);
//            view.setVisibility(View.GONE);
//        } else {
//            CommonMethods.hideSoftKeyboard(mActivity);
//            lnr_Share.setVisibility(View.VISIBLE);
//            view.setVisibility(View.VISIBLE);
//        }
//    }
//
////    @Override
////    protected void CommonProfileClick() {
////        Intent mIntent=new Intent(this,ActivityViewGroupDetails.class);
////        startActivity(mIntent);
////    }
//
//    @Override
//    public void onContactSelect() {
//        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Constants.ContactPickerChat);
//        lnr_Share.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//
//    }
//
//    @Override
//    public void onSketchSelect() {
//        Intent mInt = new Intent(mActivity, ActivitySketch.class);
//        startActivity(mInt);
//        lnr_Share.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onNewsSelect() {
//        ToastUtils.showToastForUnderDevelopment(mActivity);
//        lnr_Share.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onLocationSelect() {
//        Intent mIn = new Intent(mActivity, ActivityMap.class);
//        startActivity(mIn);
//        lnr_Share.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onVideoSelect() {
//        ToastUtils.showToastForUnderDevelopment(mActivity);
//        lnr_Share.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onGallerySelect() {
//        Intent mIntent = new Intent(mActivity, ActivityGallery.class);
//        Bundle mBundle = new Bundle();
//        mBundle.putSerializable(Constants.B_TYPE, Constants.SELECTION.CHAT_TO_IMAGE);
//        mIntent.putExtras(mBundle);
//        startActivityForResult(mIntent, Constants.ImagePickerChat);
//        lnr_Share.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onYouTubeSelect() {
//        ToastUtils.showToastForUnderDevelopment(mActivity);
//        lnr_Share.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onCameraSelect() {
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, Constants.ACTION_IMAGE_CAPTURE);
//        lnr_Share.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//    }
//
//    //navigate to details parallex screen
//    @Override
//    protected void onProfileClick(int id) {
//        super.onProfileClick(id);
//        Intent mIntent = new Intent(mActivity, ActivityViewGroupDetails.class);
//        mIntent.putExtra(Constants.B_RESULT, mDataGroup);
//        startActivity(mIntent);
//        lnr_Share.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
//    }
//
//
//    public static class ScrollGesture extends OnScrollListener {
//        private LinearLayoutManager mLinearLayoutManager;
//        private LinearLayout lnr_Section;
//        private TextView tvSection;
//
//        /**
//         * Used  To get the Scroll Gesture And show the Section Associated with the Recycler
//         */
//        public ScrollGesture(LinearLayout lnr_Section, TextView tvSection, LinearLayoutManager mLinearLayoutManager) {
//            super();
//            this.lnr_Section = lnr_Section;
//            this.tvSection = tvSection;
//            this.mLinearLayoutManager = mLinearLayoutManager;
//        }
//
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            super.onScrollStateChanged(recyclerView, newState);
//            switch (newState) {
//                case RecyclerView.SCROLL_STATE_IDLE:
//                    if (lnr_Section.getVisibility() == View.VISIBLE) {
//                        lnr_Section.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                lnr_Section.setVisibility(View.INVISIBLE);
//                            }
//                        }, 1000);
//                    }
//                    break;
//            }
//        }
//
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            try {
//                RecyclerView.ViewHolder mViewHolder = recyclerView.findViewHolderForLayoutPosition(mLinearLayoutManager.findFirstCompletelyVisibleItemPosition());
//                if (mViewHolder instanceof AdapterChatSection.SectionViewHolder) {
//                    lnr_Section.setVisibility(View.VISIBLE);
//                    tvSection.setText(((AdapterChatSection.SectionViewHolder) mViewHolder).title.getText());
//                }
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case (Constants.ContactPickerChat):
//                if (resultCode == Activity.RESULT_OK) {
//                    if (data != null) {
//                        Uri contactData = data.getData();
//                        // ContactSetget mDataContact = CommonMethods.getContact(mActivity, contactData);
//                        //   mDataContact.getContactName();
//                        // ToastUtils.showAlertToast(mActivity, "Contact fetched", ToastType.SUCESS_ALERT);
//                    }
//                }
//                break;
//            case Constants.ImagePickerChat:
//                if (data != null) {
//                    Bundle mBundle = data.getExtras();
//                    if (mBundle != null) {
//                        mBundle.putSerializable(Constants.B_TYPE, Constants.SELECTION.CHAT_TO_SELECTION);
//                        Intent mIntent = new Intent(mActivity, ActivityShareImage.class);
//                        mIntent.putExtras(mBundle);
//                        startActivityForResult(mIntent, Constants.ChatToSelection);
//                    }
//                }
//                break;
//            case Constants.ChatToSelection:
//                // Toast.makeText(mActivity, "Get Image", Toast.LENGTH_SHORT).show();
//                break;
//
//            case Constants.ACTION_IMAGE_CAPTURE:
//                // ToastUtils.showAlertToast(mActivity, "Image captured", ToastType.SUCESS_ALERT);
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//
//        switch (v.getId()) {
//            case R.id.view:
//                view.setVisibility(View.GONE);
//                lnr_Share.setVisibility(View.GONE);
//
//                break;
//
//            case R.id.iv_camera:
//                if (et_chatText.getText().toString().trim().length() <= 0) {
//                    iv_camera.setTag("camera");
//
//                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intent, Constants.ACTION_IMAGE_CAPTURE);
//                } else {
//                    iv_camera.setTag("send");
////                    createMessage(et_chatText.getText().toString().trim());
//
//                }
//                break;
//
//            default:
//                break;
//        }
//    }
//
//
//    @Override
//    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if (et_chatText.getText().toString().trim().length() > 0) {
//
//            iv_camera.setImageResource(R.drawable.ic_chat_send_icon);
//
//
//        } else {
//            iv_camera.setImageResource(R.drawable.ic_chat_share_chat_camera_icon);
//        }
//    }
//
//
//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//    }
//
//    @Override
//    public void afterTextChanged(Editable s) {
//
//        if (et_chatText.getText().toString().trim().length() > 0) {
//
//            iv_camera.setImageResource(R.drawable.ic_chat_send_icon);
//
//
//        } else {
//            iv_camera.setImageResource(R.drawable.ic_chat_share_chat_camera_icon);
//
//        }
//    }
//
//
//    private void createMessage(String message) {
//
//        DataTextChat mDataTextChat = new DataTextChat();
//        mDataTextChat.setMessageid(new org.jivesoftware.smack.packet.Message().getStanzaId());
//        mDataTextChat.setOnline(ConstantChat.ISONLINE);
//        mDataTextChat.setChattype("Text");
//        mDataTextChat.setBody(CommonMethods.getUTFEncodedString(message));
//        mDataTextChat.setLang("EN");
//        mDataTextChat.setFriendPhoneNo("919090908080");//replace it by groupId
//        mDataTextChat.setTimestamp(String.valueOf(System.currentTimeMillis()));
//        mDataTextChat.setMessageType(ConstantChat.TYPE_GROUPCHAT);
//
//        String SendMessageJSon = "";
//        if (!TextUtils.isEmpty(OneToOneChatJSonCreator.getTextMessageToJSON(mDataTextChat)))
//            SendMessageJSon = OneToOneChatJSonCreator.getTextMessageToJSON(mDataTextChat);
//
//        insertMessageToDB(mDataTextChat);
//        sendMessageToXMPP(SendMessageJSon);
//
//    }
//
//    private void insertMessageToDB(DataTextChat mDataTextChat) {
//        new TableChat(mActivity).insertChat(mDataTextChat);
//    }
//
//    private void sendMessageToXMPP(String processedMessage) {
//
//        if (mBound) {
//            Message chatMessage = Message.obtain();
//            chatMessage.what = ConstantXMPP.SEND_ONETOONE_TEXT_MESSAGE;
//
//            Bundle bundle = new Bundle();
//            bundle.putString(ConstantChat.SEND_TEXT_CHAT_B_MESSAGE, processedMessage);
//
//            // Set the bundle data to the Message
//            chatMessage.setData(bundle);
//            try {
//                messengerConnection.send(chatMessage);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//}
