package com.wachat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataContact;
import com.wachat.util.CommonMethods;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class AdapterDashFavorite extends RecyclerView.Adapter<AdapterDashFavorite.VH> {

    private Context mContext;
    private ImageLoader mImageLoader;
    private ArrayList<DataContact> mContactArrayList;
    private String imageUrl = "";
    private DataContact mDataContact;
    private AdapterCallback mAdapterCallback;
    private DisplayImageOptions options;

    public AdapterDashFavorite(Context mContext, ArrayList<DataContact> mContactArrayList, ImageLoader mImageLoader, AdapterCallback mAdapterCallback) {
        this.mContext = mContext;
        this.mContactArrayList = mContactArrayList;
        this.mAdapterCallback = mAdapterCallback;

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_chats_noimage_profile)
                .showImageForEmptyUri(R.drawable.ic_chats_noimage_profile)
                .showImageOnFail(R.drawable.ic_chats_noimage_profile)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_chats_noimage_profile)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();


        this.mImageLoader = ImageLoader.getInstance();
//        this.mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH((LayoutInflater.from(parent.getContext())).inflate(R.layout.inflater_favourite_screen, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {


        mDataContact = mContactArrayList.get(position);

        String name = (mDataContact.getAppName().equals("")) ? ((mDataContact.getName().equals("")) ?
                "+" + mDataContact.getPhoneNumber() : mDataContact.getName()) : mDataContact.getAppName();
        holder.tv_ProfileName.setText(CommonMethods.getUTFDecodedString(name));
        holder.tv_Status.setText(CommonMethods.getUTFDecodedString(mDataContact.getStatus()));


//        if (!TextUtils.isEmpty(mDataContact.getAppFriendImageLink())) {
//            imageUrl = mDataContact.getAppFriendImageLink();
        // Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.ic_chats_noimage_profile).into(holder.iv_contact_ProfileImage);

//        if (mDataContact.getAppFriendImageLink().contains("http://svn.indusnettechnologies.com/")){
//
//        }else {
            mImageLoader.displayImage(mDataContact.getAppFriendImageLink(), holder.iv_ProfileImage, options);
//        }



       /* if (mDataContact.getAppFriendImageLink() != null && !mDataContact.getAppFriendImageLink().equals("")) {
            imageUrl = mDataContact.getAppFriendImageLink();
            Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.ic_chats_noimage_profile).into(holder.iv_ProfileImage);
        }*//*15102015*//* else {
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/3/37/No_person.jpg";
        }*/
        //ic_chats_noimage_profile
        //   mImageLoader.displayImage(imageUrl, holder.iv_ProfileImage);

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                if (mContactArrayList.size() > 0) {
                    mAdapterCallback.OnClickPerformed(mContactArrayList.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContactArrayList.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        public TextView tv_ProfileName, tv_Status;
        public ImageView iv_ProfileImage;
        public RelativeLayout rel_top;

        public VH(View v) {
            super(v);
            tv_ProfileName = (TextView) v
                    .findViewById(R.id.tv_ProfileName);
            tv_Status = (TextView) v.findViewById(R.id.tv_Status);
            iv_ProfileImage = (ImageView) v.findViewById(R.id.iv_ProfileImage);
            rel_top = (RelativeLayout) v.findViewById(R.id.rel_top);

        }
    }
}