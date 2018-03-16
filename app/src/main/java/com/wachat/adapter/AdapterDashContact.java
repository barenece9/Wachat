package com.wachat.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wachat.R;
import com.wachat.async.AsyncBlockUser;
import com.wachat.async.AsyncFavoriteUser;
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataClick;
import com.wachat.data.DataContact;
import com.wachat.dialog.DialogBlockFavouriteAlert;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.TableContact;
import com.wachat.util.CommonMethods;
import com.wachat.util.Prefs;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.util.ArrayList;

public class AdapterDashContact extends RecyclerView.Adapter<AdapterDashContact.VH> implements View.OnClickListener, InterfaceResponseCallback {


    Prefs mPrefs;
    private ArrayList<DataContact> mListContact;
    private ImageLoader mImageLoader;
    private AdapterCallback mAdapterCallback;
    private Context mContext;
    //    private DataContact mDataContact;
    private AsyncBlockUser mAsyncBlockUser;
    private AsyncFavoriteUser mAsyncFavoriteUser;
    //    private String imageUrl = "";
    private DisplayImageOptions options;

    public AdapterDashContact(Context mContext, ArrayList<DataContact> mListContact, AdapterCallback mAdapterCallback) {
        this.mContext = mContext;
        this.mListContact = mListContact;
        this.mAdapterCallback = mAdapterCallback;
        mPrefs = Prefs.getInstance(mContext);
        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_chats_noimage_profile)
                .showImageOnFail(R.drawable.ic_chats_noimage_profile)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageOnLoading(R.drawable.ic_chats_noimage_profile)
                .considerExifParams(true).build();
        mImageLoader = ImageLoader.getInstance();
//        mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
    }

    public void refreshData(ArrayList<DataContact> _mListContact) {
        this.mListContact = _mListContact;
        this.notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH((LayoutInflater.from(parent.getContext())).inflate(R.layout.inflater_contact_screen, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        final DataContact mDataContact = mListContact.get(position);
        holder.pos = position;
        String name = (mDataContact.getAppName().equals("")) ? ((mDataContact.getName().equals("")) ?
                "+" + mDataContact.getPhoneNumber() : mDataContact.getName()) : mDataContact.getAppName();
        holder.tv_ProfileName.setText(CommonMethods.getUTFDecodedString(name));
        holder.tv_ProfileStatus.setText(CommonMethods.getUTFDecodedString(mDataContact.getStatus()));


//         Picasso.with(mContext).load(mDataContact.getAppFriendImageLink()).placeholder(R.drawable.ic_chats_noimage_profile).into(holder.iv_contact_ProfileImage);
        mImageLoader.displayImage(mDataContact.getAppFriendImageLink(), holder.iv_contact_ProfileImage,
                options);


        holder.tv_ProfileStatus.setSelected(true);
        holder.iv_block.setSelected((mDataContact.getIsBlocked() > 0) ? true : false);
        holder.iv_Favorite.setSelected((mDataContact.getIsFavorite() > 0) ? true : false);

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mListContact.size() > 0) {
                    mAdapterCallback.OnClickPerformed(mDataContact);
//                }
            }
        });

        holder.iv_block.setTag(holder);
        holder.iv_Favorite.setTag(holder);
//        holder.lnr_name.setTag(holder);
//        holder.lnr_name.setOnClickListener(this);
        holder.rl_block.setOnClickListener(this);
        holder.rl_block.setTag(holder);
        holder.rl_Favorite.setOnClickListener(this);
        holder.rl_Favorite.setTag(holder);


    }

    @Override
    public int getItemCount() {
        return mListContact.size();
    }

    @Override
    public void onClick(View v) {
        int position;
        switch (v.getId()) {
            case R.id.rl_Favorite:
                if (CommonMethods.isOnline(mContext)) {
                    VH mVh = (VH) v.getTag();
                    //check if user is blocked,if blocked then fav not possible
                    if (mListContact.get(mVh.pos).getIsBlocked() == 0) {
                        if (mListContact.get(mVh.pos).getIsFavorite() > 0)
                            favouiteUser(mVh, false);
                        else
                            favouiteUser(mVh, true);
                    } else {
                        showAlertDialogUserBlockedNeedUnBlockToMakeFavourite(mVh);
                    }
                } else {
                    ToastUtils.showAlertToast(mContext, mContext.getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                }

                break;

            case R.id.rl_block:
                if (CommonMethods.isOnline(mContext)) {
                    final VH mVH = (VH) v.getTag();
                    //if want to block the user then unfav in bg and make it block
                    if (mListContact.get(mVH.pos).getIsFavorite() == 1) {
                        DialogBlockFavouriteAlert mDialogBlockUser = new DialogBlockFavouriteAlert(mContext,
                                mContext.getResources().getString(R.string.alert_warn_friend_will_be_unfavourite_once_block),
                                true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        mVH.isBlockedClicked = true;
                                        favouiteUser(mVH, true);
                                        break;
                                }
                            }
                        });
                        mDialogBlockUser.show();

                    } else {
                        blockUser(mVH, true);
                    }
                } else {
                    ToastUtils.showAlertToast(mContext, mContext.getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                }
                break;
            case R.id.lnr_name:
                VH mVH = (VH) v.getTag();
                if (mListContact.size() > 0) {
                    mAdapterCallback.OnClickPerformed(mListContact.get(mVH.pos));
                }
                break;
            default:
                break;
        }
    }

    private void showAlertDialogUserBlockedNeedUnBlockToMakeFavourite(VH mVh) {
        DialogBlockFavouriteAlert mDialogBlockUser = new DialogBlockFavouriteAlert(mContext,
                mContext.getResources().getString(R.string.alert_warn_user_blocked_need_to_unblock_to_favourite), false, null);
        mDialogBlockUser.show();
    }

    private void blockUser(VH mVh, boolean doBlock) {

        DataContact mDataContact = mListContact.get(mVh.pos);
        mVh.rl_block.setClickable(false);
        mVh.progressBar_iv_block.setVisibility(View.VISIBLE);
        mAsyncBlockUser = new AsyncBlockUser(mVh, mPrefs.getUserId(),
                mDataContact.getChatId(), String.valueOf(mDataContact.getIsBlocked()), this);
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncBlockUser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncBlockUser.execute();
        }
    }

    private void favouiteUser(VH mVh, boolean doBlock) {

        DataContact mDataContact = mListContact.get(mVh.pos);
        mVh.rl_Favorite.setClickable(false);
        mVh.progressBar_iv_Favorite.setVisibility(View.VISIBLE);
        mAsyncFavoriteUser = new AsyncFavoriteUser(mVh, mPrefs.getUserId(),
                mDataContact.getChatId(), String.valueOf(mDataContact.getIsFavorite()),
                new InterfaceResponseCallback() {
            @Override
            public void onResponseObject(Object mObject) {
                if (mObject instanceof DataClick) {

                    DataClick mDataClick = (DataClick) mObject;
                    VH mVh = (VH) mDataClick.getmRecyclerView();
                    if (mVh.isBlockedClicked) {
                        blockUser(mVh, true);
                        mVh.isBlockedClicked = false;
                    }
                    mVh.rl_Favorite.setClickable(true);
                    mVh.progressBar_iv_Favorite.setVisibility(View.GONE);
//                    if (mDataClick.isStatus()) {
                    if (mListContact.get(mVh.pos).getIsFavorite() > 0) {
                        mListContact.get(mVh.pos).setIsFavorite(0);
                    } else {
                        mListContact.get(mVh.pos).setIsFavorite(1);
                    }
                    mVh.iv_Favorite.setSelected((mListContact.get(mVh.pos).getIsFavorite() > 0) ? true : false);
//                        mVh.iv_Favorite.setSelected((mListContact.get(mVh.pos).getIsFavorite() > 0) ? true : false);
                    new TableContact(mContext).updateBlock(mListContact.get(mVh.pos));

                    //notifyDataSetChanged();
                }
//                }
            }

            @Override
            public void onResponseList(ArrayList<?> mList) {

            }

            @Override
            public void onResponseFaliure(String responseText) {

            }
        });
        if (CommonMethods.checkBuildVersionAsyncTask()) {
            mAsyncFavoriteUser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mAsyncFavoriteUser.execute();
        }
    }


    @Override
    public void onResponseObject(Object mObject) {
        if (mObject instanceof DataClick) {
            DataClick mDataClick = (DataClick) mObject;
            VH mVh = (VH) mDataClick.getmRecyclerView();
            mVh.rl_block.setClickable(true);
            mVh.progressBar_iv_block.setVisibility(View.GONE);
//            if (mDataClick.isStatus()) {
            if (mListContact.get(mVh.pos).getIsBlocked() > 0) {
                ToastUtils.showAlertToast(mContext,
                        "Successfully unblocked this person", ToastType.FAILURE_ALERT);
                mListContact.get(mVh.pos).setIsBlocked(0);
            } else {
                ToastUtils.showAlertToast(mContext,
                        "Successfully blocked this person", ToastType.FAILURE_ALERT);
                mListContact.get(mVh.pos).setIsBlocked(1);
            }
            mVh.iv_block.setSelected((mListContact.get(mVh.pos).getIsBlocked() > 0) ? true : false);
            new TableContact(mContext).updateBlock(mListContact.get(mVh.pos));
//                this.notifyDataSetChanged();
            //}
        }
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {
    }

    @Override
    public void onResponseFaliure(String responseText) {

    }

    public static class VH extends RecyclerView.ViewHolder {

        public TextView tv_ProfileName, tv_ProfileStatus;
        public ImageView iv_block, iv_Favorite, iv_contact_ProfileImage;
        public View rl_block,rl_Favorite,progressBar_iv_block,progressBar_iv_Favorite;
        public LinearLayout lnr_name;
        private int pos;
        private boolean isBlockedClicked = false;

        public VH(View itemView) {
            super(itemView);
            lnr_name = (LinearLayout) itemView.findViewById(R.id.lnr_name);
            tv_ProfileName = (TextView) itemView
                    .findViewById(R.id.tv_ProfileName);
            tv_ProfileStatus = (TextView) itemView.findViewById(R.id.tv_ProfileStatus);
            iv_block = (ImageView) itemView.findViewById(R.id.iv_block);
            iv_Favorite = (ImageView) itemView.findViewById(R.id.iv_Favorite);
            iv_contact_ProfileImage = (ImageView) itemView.findViewById(R.id.iv_contact_ProfileImage);
            rl_block = itemView.findViewById(R.id.rl_block);
            rl_Favorite = itemView.findViewById(R.id.rl_Favorite);
            progressBar_iv_block = itemView.findViewById(R.id.progressBar_iv_block);
            progressBar_iv_Favorite = itemView.findViewById(R.id.progressBar_iv_Favorite);
        }
    }

}
