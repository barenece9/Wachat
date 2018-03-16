package com.wachat.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wachat.R;
import com.wachat.async.AsyncUnblockUser;
import com.wachat.callBack.AdapterCallback;
import com.wachat.callBack.DialogCallback;
import com.wachat.data.BaseResponse;
import com.wachat.data.DataContact;
import com.wachat.dialog.DialogBlockUser;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.TableContact;
import com.wachat.util.CommonMethods;
import com.wachat.util.Prefs;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;
import java.util.TreeSet;

public class AdapterViewBlockUser extends RecyclerView.Adapter<AdapterViewBlockUser.VH> implements View.OnClickListener, InterfaceResponseCallback, DialogCallback {

    private AdapterCallback mCallback;
    private ImageLoader mImageLoader;
    private TreeSet<String> selectedContactSet = new TreeSet<String>();
    private Context mContext;
    private ArrayList<DataContact> mContactArrayList;
//    private String imageUrl = "";
//    private DataContact mDataContact;
    private AsyncUnblockUser mAsyncBlockUser;
    private Prefs mPrefs;
    private int pos;
    private DisplayImageOptions options;
    private TextView emptyView;
    public AdapterViewBlockUser(Context mContext, ArrayList<DataContact> mContactArrayList,
                                ImageLoader mImageLoader, AdapterCallback mCallback,TextView emptyView ) {
        this.mContext = mContext;
        this.mCallback = mCallback;
        this.mContactArrayList = mContactArrayList;
        this.mImageLoader = mImageLoader;
        this.emptyView = emptyView;
        mPrefs = Prefs.getInstance(mContext);

        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_chats_noimage_profile)
                .showImageOnFail(R.drawable.ic_chats_noimage_profile)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.ic_chats_noimage_profile)
                .considerExifParams(true).build();
//        mImageLoader = ImageLoader.getInstance();
//        mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH((LayoutInflater.from(parent.getContext())).inflate(R.layout.inflater_block_user_screen, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);

        DataContact mDataContact = mContactArrayList.get(position);

        String name = (mDataContact.getAppName().equals("")) ? ((mDataContact.getName().equals("")) ?
                "+" + mDataContact.getPhoneNumber() : mDataContact.getName()) : mDataContact.getAppName();
        holder.tv_ProfileName.setText(CommonMethods.getUTFDecodedString(name));
        holder.tv_Status.setText(CommonMethods.getUTFDecodedString(mDataContact.getStatus()));

        if (mDataContact.getAppFriendImageLink() != null && !mDataContact.getAppFriendImageLink().equals("")) {
            String imageUrl = mDataContact.getAppFriendImageLink();
            mImageLoader.displayImage(imageUrl, holder.iv_ProfileImage,options);
        } /*else {
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/3/37/No_person.jpg";
        }*/

        if(selectedContactSet.contains(mDataContact.getChatId())){
            holder.progressBar_iv_block.setVisibility(View.VISIBLE);
        }else{
            holder.progressBar_iv_block.setVisibility(View.GONE);
        }
        holder.iv_block.setSelected(true);
        holder.tv_Status.setSelected(true);
        holder.iv_block.setTag(position);
        holder.rel_top.setTag(position);
        holder.rl_block.setTag(position);
        holder.rl_block.setOnClickListener(this);
        holder.rel_top.setOnClickListener(this);


    }

    @Override
    public int getItemCount() {
        return mContactArrayList.size();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rl_block:
                pos = (Integer) v.getTag();
                DialogBlockUser mDialogBlockUser = new DialogBlockUser(mContext, this, pos);
                mDialogBlockUser.show();

                break;
            case R.id.rel_top:
                pos = (Integer) v.getTag();
                if (mContactArrayList.size() > 0)
                    mCallback.OnClickPerformed(mContactArrayList.get(pos));
                break;

            default:
                break;
        }


    }

    @Override
    public void OnClickYes(Object mObject) {
        //no need here
    }

    @Override
    public void OnClickNo(Object mObject) {
//no need here
    }

    @Override
    public void onYes(int pos) {
        if (mContactArrayList.size() > 0) {
            //mCallback.OnClickPerformed(mContactArrayList.get(pos));
            blockUser(pos);
        }
    }


    private void blockUser(int pos) {
        DataContact mDataContact = mContactArrayList.get(pos);
        selectedContactSet.add(mDataContact.getChatId());
        refreshList(mContactArrayList);
        mAsyncBlockUser = new AsyncUnblockUser(mPrefs.getUserId(),
                mDataContact.getChatId(), String.valueOf(mDataContact.getIsBlocked()), this);
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncBlockUser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncBlockUser.execute();
        }
    }

    @Override
    public void onResponseObject(Object mObject) {
        if (mObject instanceof BaseResponse) {
            if (mContactArrayList.get(pos).getIsBlocked() > 0) {
                mContactArrayList.get(pos).setIsBlocked(0);
                ToastUtils.showAlertToast(mContext,
                        "Successfully unblocked this person", ToastType.FAILURE_ALERT);
            } else {
                ToastUtils.showAlertToast(mContext,
                        "Successfully blocked this person", ToastType.FAILURE_ALERT);
                mContactArrayList.get(pos).setIsBlocked(1);
            }
            DataContact mDataContact = mContactArrayList.get(pos);
            selectedContactSet.remove(mDataContact.getChatId());
            new TableContact(mContext).updateBlock(mContactArrayList.get(pos));
            mContactArrayList=new TableContact(mContext).getBlockUser();
            if (mContactArrayList.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                emptyView.setVisibility(View.GONE);
            }
            refreshList(mContactArrayList);
        }
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {

    }

    @Override
    public void onResponseFaliure(String responseText) {

    }


    public static class VH extends RecyclerView.ViewHolder {

        public TextView tv_ProfileName, tv_Status;
        public ImageView iv_block, iv_ProfileImage;
        private RelativeLayout rel_top;
        public View rl_block,progressBar_iv_block;
        public VH(View itemView) {
            super(itemView);

            rel_top = (RelativeLayout) itemView.findViewById(R.id.rel_top);
            tv_ProfileName = (TextView) itemView
                    .findViewById(R.id.tv_ProfileName);
            tv_Status = (TextView) itemView.findViewById(R.id.tv_Status);
            iv_block = (ImageView) itemView.findViewById(R.id.iv_block);
            iv_ProfileImage = (ImageView) itemView.findViewById(R.id.iv_ProfileImage);
            rl_block = itemView.findViewById(R.id.rl_block);
            progressBar_iv_block = itemView.findViewById(R.id.progressBar_iv_block);
        }
    }

    public void refreshList(ArrayList<DataContact> mContactArrayList){
        this.mContactArrayList = mContactArrayList;
        this.notifyDataSetChanged();
    }

}
