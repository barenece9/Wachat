package com.wachat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wachat.R;
import com.wachat.activity.ActivityViewGroupDetails;
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataGroupMember;
import com.wachat.storage.TableContact;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class AdapterViewGroupDetails extends BaseAdapter {

    private final DisplayImageOptions options;
    //    private final TableUserInfo tableUserInfo;
    private String userId = "";
    private Context mContext;
    private AdapterCallback mCallback;
    private ArrayList<DataGroupMember> dataGroupMemberArrayList;
//    private DataGroupMember dataGroupMember;
//    private String imageUrl = "";
    private ImageLoader mImageLoader;

    public AdapterViewGroupDetails(ActivityViewGroupDetails mContext, ArrayList<DataGroupMember> dataGroupMemberArrayList, ImageLoader mImageLoader, AdapterCallback mCallback) {
        this.mContext = mContext;
        this.dataGroupMemberArrayList = dataGroupMemberArrayList;
        this.mImageLoader = mImageLoader;
        this.mCallback = mCallback;

        userId = new TableUserInfo(mContext).getUser().getUserId();

        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_chats_noimage_profile)
                .showImageOnFail(R.drawable.ic_chats_noimage_profile)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.ic_chats_noimage_profile)
                .considerExifParams(true).build();
    }

    @Override
    public int getCount() {
        return dataGroupMemberArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = (LayoutInflater.from(parent.getContext())).inflate(R.layout.inflater_favourite_screen, null);
            holder.tv_ProfileName = (TextView) convertView.findViewById(R.id.tv_ProfileName);
            holder.tv_Status = (TextView) convertView.findViewById(R.id.tv_Status);
            holder.iv_ProfileImage = (ImageView) convertView.findViewById(R.id.iv_ProfileImage);


            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        DataGroupMember dataGroupMember = dataGroupMemberArrayList.get(position);


        String name = "";
        if(userId.equalsIgnoreCase(dataGroupMember.getGroupMemberUserId())){
            name = mContext.getString(R.string.you);
        }else {
            name = dataGroupMember.getMemberName();
            if(TextUtils.isEmpty(name)) {
                //get friend name from contact table
                name = new TableContact(mContext).getFriendDetailsByID(dataGroupMember.getGroupMemberUserId()).getName();
            }
            if(TextUtils.isEmpty(name)){
                name = "+" + dataGroupMember.getMemberCountryId() + dataGroupMember.getMemberPhNo();
            }
        }

        holder.tv_ProfileName.setText(name);
//        if (TextUtils.isEmpty(dataGroupMember.getMemberName())) {
//            if (TextUtils.isEmpty(name)) {
//                holder.tv_ProfileName.setText("+" + dataGroupMember.getMemberCountryId() + dataGroupMember.getMemberPhNo());
//            } else
//                holder.tv_ProfileName.setText(name);
//        } else
//            holder.tv_ProfileName.setText(dataGroupMember.getMemberName());

        if (!TextUtils.isEmpty(dataGroupMember.getStatus()))
            holder.tv_Status.setText(CommonMethods.getUTFDecodedString(dataGroupMember.getStatus()));

        if (!TextUtils.isEmpty(dataGroupMember.getMemberImage())) {
            mImageLoader.displayImage(dataGroupMember.getMemberImage(), holder.iv_ProfileImage,options);
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.OnClickPerformed(String.valueOf(position));
            }
        });
        return convertView;
    }

//    @Override
//    public void onClick(View v) {
//
////        int pos = (Integer)v.getTag();
////        mCallback.OnClickPerformed("user-" + v.getTag());
////        String userId =  new TableUserInfo(mContext).getUser().getUserId();
////
////        if(userId.equalsIgnoreCase(dataGroupMember.getUserId())){
////
////        }else
////        {
////          if(dataGroupMember.getUserId().equalsIgnoreCase(userId)){
////
////          }else {
////              Bundle bundle = new Bundle();
////              bundle.putBoolean("isProfileview", true);
////
////              mDialogGroupDetails = new DialogGroupDetails();
////              mDialogGroupDetails.setArguments(bundle);
////              mDialogGroupDetails.show((ActivityViewGroupDetails) mContext., DialogGroupDetails.class.getSimpleName());
////          }
////
////
////        }
//    }

    public void refreshList(ArrayList<DataGroupMember> mArrayList) {
        this.dataGroupMemberArrayList = mArrayList;
        this.notifyDataSetChanged();
    }

    public class Holder {
        TextView tv_ProfileName, tv_Status;
        ImageView iv_ProfileImage;
    }
}
