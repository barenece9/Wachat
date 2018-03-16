package com.wachat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataContactForGroup;
import com.wachat.data.DataGroupMember;
import com.wachat.util.CommonMethods;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class AdapterAddNewPeopleInGroup extends RecyclerView.Adapter<AdapterAddNewPeopleInGroup.VH> implements View.OnClickListener {
//    private final Prefs mPrefs;
    int count = 0;
    Context mContext;
    private TreeSet<String> selectedContactSet = new TreeSet<String>();
    private TreeSet<String> selectedContactChatIdSet = new TreeSet<String>();
    private AdapterCallback mCallback;
    private LayoutInflater layoutInflater;
    //    private DataContactForGroup mDataContact;
    private ArrayList<DataContactForGroup> mListContact;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;


    public AdapterAddNewPeopleInGroup(Context mContext,
                                      ImageLoader mImageLoader, AdapterCallback mCallback) {

        this.layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = mContext;
        mListContact = new ArrayList<DataContactForGroup>();
        
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_chats_noimage_profile)
                .showImageForEmptyUri(R.drawable.ic_chats_noimage_profile)
                .showImageOnFail(R.drawable.ic_chats_noimage_profile)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageOnLoading(R.drawable.ic_chats_noimage_profile)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        this.mImageLoader = mImageLoader;
        this.mCallback = mCallback;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH((LayoutInflater.from(parent.getContext())).inflate(R.layout.inflater_add_people_group_screen, parent, false));
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {


//        final DataContactForGroup mDataContact = ;
        String name = (mListContact.get(position).getAppName().equals("")) ?
                ((mListContact.get(position).getName().equals("")) ?
                        mListContact.get(position).getPhoneNumber() : mListContact.get(position).getName())
                : mListContact.get(position).getAppName();
        holder.tv_ProfileName.setText(CommonMethods.getUTFDecodedString(name));
        holder.tv_Status.setText(mListContact.get(position).getStatus());
        holder.pos = position;

        String imageUrl = mListContact.get(position).getAppFriendImageLink();
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = "";
        }
        mImageLoader.displayImage(imageUrl,
                holder.iv_ProfileImage, options);


//        holder.iv_select.setTag(holder);
//        holder.iv_select.setSelected(false);
//        if(mListContact.get(position).getIsSelectedForGroup() == 1){
//            holder.iv_select.setVisibility(View.GONE);
//        }else {
        holder.iv_select.setVisibility(View.VISIBLE);
        if (selectedContactSet.contains(mListContact.get(position).getFriendId())) {
            holder.iv_select.setSelected(true);
            holder.tv_ProfileName.setTypeface(null, Typeface.BOLD);
        } else {
            holder.iv_select.setSelected(false);
            holder.tv_ProfileName.setTypeface(null, Typeface.NORMAL);
        }

        holder.iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListContact.get(position).getIsSelectedForGroup() != 1) {
                    onSelect(position);
                } else {
                    onExistingMemberItemClick(position);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListContact.get(position).getIsSelectedForGroup() != 1) {
                    onSelect(position);
                } else {
                    onExistingMemberItemClick(position);
                }
            }
        });
//        }
//        if (mListContact.get(position).getIsSelectedForGroup() != 0 && mListContact.get(position).getIsSelectedForGroup() == 1)
//            holder.iv_select.setSelected(true);
//        else
//            holder.iv_select.setSelected(false);


    }

    private void onExistingMemberItemClick(int position) {

        ToastUtils.showAlertToast(mContext, "This member is already added to the group", ToastType.SUCESS_ALERT);
    }

    public void addAlreadyMembersInSelectedItems(ArrayList<DataGroupMember> alreadyAddedMembers) {
        for (DataGroupMember member : alreadyAddedMembers) {
            if (!member.getGroupMemberUserId().equals(AppVhortex.sharedPrefs().getUserId())) {
//                for (DataContactForGroup dataContactForGroup : mListContact) {
//                    if (dataContactForGroup.getFriendId().equals(member.getGroupMemberUserId())) {
                        selectedContactSet.add(member.getGroupMemberUserId());
                        selectedContactChatIdSet.add(member.ChatId);
//                    }
//                }
            }
        }
    }


    private void onSelect(int position) {
        if (selectedContactSet.contains(mListContact.get(position).getFriendId())) {
            selectedContactSet.remove(mListContact.get(position).getFriendId());
        } else {
            selectedContactSet.add(mListContact.get(position).getFriendId());


        }


        if (selectedContactChatIdSet.contains(mListContact.get(position).getChatId())) {
            selectedContactChatIdSet.remove(mListContact.get(position).getChatId());
        } else {
            selectedContactChatIdSet.add(mListContact.get(position).getChatId());
        }
        notifyDataSetChanged();

        mCallback.OnClickPerformed(null);
    }


    @Override
    public int getItemCount() {
        return mListContact.size();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_select:
//                VH mVh = (VH) v.getTag();
//                mListContact.get(mVh.pos).setIsSelected(!mListContact.get(mVh.pos).isSelected);
//                if (mListContact.get(mVh.pos).isSelected)
//                    mListContact.get(mVh.pos).setIsSelectedForGroup(1);
//                else
//                    mListContact.get(mVh.pos).setIsSelectedForGroup(0);
//                new TableContact(mContext).setSelectedForGroup(mListContact.get(mVh.pos));
//                this.notifyDataSetChanged();
//                mCallback.OnClickPerformed(mListContact.get(mVh.pos));
//
//                break;
//        }
    }

    public int getSelectedCount() {
        return selectedContactSet.size();
    }

    public String getSelectedMemberCommaSeperatedChatIds() {
        if (selectedContactChatIdSet != null && selectedContactChatIdSet.size() > 0) {
            return StringUtils.join(
                    selectedContactChatIdSet.toArray(new String[selectedContactChatIdSet.size()]),
                    ",");
        } else {
            return "";
        }
    }

    public String getSelectedMemberCommaSeperatedIds() {
        if (selectedContactSet != null && selectedContactSet.size() > 0) {
            return StringUtils.join(
                    selectedContactSet.toArray(new String[selectedContactSet.size()]),
                    ",");
        } else {
            return "";
        }
    }

    public void refreshList(ArrayList<DataContactForGroup> mArrayList) {
        this.mListContact = mArrayList;

        this.notifyDataSetChanged();
    }

    public TreeSet<String> getSelectedMemberConatactSet() {
        return selectedContactSet;
    }

    public static class VH extends RecyclerView.ViewHolder {

        public TextView tv_ProfileName, tv_Status;
        public ImageView iv_select, iv_ProfileImage;
        public int pos;

        public VH(View itemView) {
            super(itemView);
            tv_ProfileName = (TextView) itemView
                    .findViewById(R.id.tv_ProfileName);
            tv_Status = (TextView) itemView.findViewById(R.id.tv_Status);
            iv_select = (ImageView) itemView.findViewById(R.id.iv_select);
            iv_ProfileImage = (ImageView) itemView.findViewById(R.id.iv_ProfileImage);

        }

    }
}
