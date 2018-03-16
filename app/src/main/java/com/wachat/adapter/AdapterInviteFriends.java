package com.wachat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wachat.R;
import com.wachat.callBack.AdapterCallback;
import com.wachat.callBack.DialogCallback;
import com.wachat.data.DataContact;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class AdapterInviteFriends extends RecyclerView.Adapter<AdapterInviteFriends.VH> implements DialogCallback, InterfaceResponseCallback {

    private final DisplayImageOptions options;
    private Context mContext;
    private ImageLoader mImageLoader;
    private ArrayList<DataContact> mListContact;
//    private String imageUrl = "";
//    int pos;
//    private AsyncInviteFriend mAsyncInviteFriend;
    private TreeSet<String> selectedContactSet = new TreeSet<String>();
    private TreeSet<String> selectedContactChatIdSet = new TreeSet<String>();
    private AdapterCallback mCallBack;

    public AdapterInviteFriends(Context mContext, ImageLoader mImageLoader, AdapterCallback mCallBack) {
        this.mContext = mContext;
        this.mImageLoader = mImageLoader;
        this.mCallBack = mCallBack;
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true).cacheOnDisk(true).build();

    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH((LayoutInflater.from(parent.getContext())).inflate(R.layout.inflater_invite_friend_screen, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {

        DataContact mDataContact = mListContact.get(position);
        final String name = (mDataContact.getAppName().equals("")) ?
                ((mDataContact.getName().equals("")) ?
                        mDataContact.getPhoneNumber() : mDataContact.getName())
                : mDataContact.getAppName();
        String phNo = (mDataContact.getPhoneNumber().contains("+")) ?
                mDataContact.getPhoneNumber() : ("+" + mDataContact.getPhoneNumber());

        holder.itemView.setTag(position);
        holder.tv_ProfileName.setText(name);
        holder.tv_phno.setText(phNo);
        if (mDataContact.getImageUrl() != null && !mDataContact.getImageUrl().equals("")) {
            mImageLoader.displayImage(mDataContact.getImageUrl(), holder.iv_ProfileImage,options);
        }

        if (selectedContactSet.contains(mListContact.get(position).getPhoneNumber())) {
            holder.iv_select.setSelected(true);
            holder.tv_ProfileName.setTypeface(null, Typeface.BOLD);
        } else {
            holder.iv_select.setSelected(false);
            holder.tv_ProfileName.setTypeface(null, Typeface.NORMAL);
        }

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                pos = (Integer) v.getTag();
////                DialogInviteFriend mDialogInviteFriend = new DialogInviteFriend(mContext, AdapterInviteFriends.this, pos,name);
////                mDialogInviteFriend.show();
//
//            }
//        });

        holder.iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelect(position);
            }
        });
    }

    public int getSelectedCount() {
        return selectedContactSet.size();
    }

    public String getSelectedFriendNumbers() {
        if (selectedContactSet != null && selectedContactSet.size() > 0) {
            return StringUtils.join(
                    selectedContactSet.toArray(new String[selectedContactSet.size()]),
                    ",");
        } else {
            return "";
        }
    }

    private void onSelect(int position) {
        if (selectedContactSet.contains(mListContact.get(position).getPhoneNumber())) {
            selectedContactSet.remove(mListContact.get(position).getPhoneNumber());
        } else {
            selectedContactSet.add(mListContact.get(position).getPhoneNumber());


        }



        if (selectedContactChatIdSet.contains(mListContact.get(position).getPhoneNumber())) {
            selectedContactChatIdSet.remove(mListContact.get(position).getPhoneNumber());
        } else {
            selectedContactChatIdSet.add(mListContact.get(position).getPhoneNumber());
        }
        notifyDataSetChanged();

        mCallBack.OnClickPerformed(null);
    }


    @Override
    public int getItemCount() {
        return mListContact.size();
    }

    @Override
    public void OnClickYes(Object mObject) {

    }

    @Override
    public void OnClickNo(Object mObject) {

    }

    @Override
    public void onYes(int pos) {


    }

    @Override
    public void onResponseObject(Object mObject) {
        //on success of invite friend
        ToastUtils.showAlertToast(mContext, mContext.getString(R.string.invite_send_succ), ToastType.SUCESS_ALERT);
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {

    }

    @Override
    public void onResponseFaliure(String responseText) {
        ToastUtils.showAlertToast(mContext, mContext.getString(R.string.invite_unsucc), ToastType.FAILURE_ALERT);
    }



    public static class VH extends RecyclerView.ViewHolder {
        private TextView tv_ProfileName, tv_phno;
        private ImageView iv_ProfileImage,iv_select;

        public VH(View itemView) {
            super(itemView);
            tv_ProfileName = (TextView) itemView.findViewById(R.id.tv_ProfileName);
            tv_phno = (TextView) itemView.findViewById(R.id.tv_phno);
            iv_ProfileImage = (ImageView) itemView.findViewById(R.id.iv_ProfileImage);
            iv_select = (ImageView) itemView.findViewById(R.id.iv_select);

        }
    }

    public void refreshList(ArrayList<DataContact> mListContact) {
        this.mListContact = mListContact;
        this.notifyDataSetChanged();
    }

    public void resetSelection(){
        selectedContactSet = new TreeSet<String>();
        selectedContactChatIdSet = new TreeSet<String>();
        this.notifyDataSetChanged();
    }
}
