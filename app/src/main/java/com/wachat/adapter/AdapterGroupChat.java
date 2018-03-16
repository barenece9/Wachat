package com.wachat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wachat.customViews.components.Cell;
import com.wachat.customViews.components.CellChat;
import com.wachat.customViews.components.CellContact;
import com.wachat.customViews.components.CellPhoto;
import com.wachat.data.DataChat;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 24-08-2015.
 */
public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.Holder> {

    private ArrayList<DataChat> mListChats;
    private ImageLoader mImageLoader;
    private DataChat mDataChat;

    public static int CHAT = 0, PHOTO = 1, CONTACT = 2;

    public AdapterGroupChat(ArrayList<DataChat> mListChats, ImageLoader mImageLoader) {
        this.mListChats = mListChats;
        this.mImageLoader = mImageLoader;
    }

    public int getItemViewType(int position) {
        //Some logic to know which type will come next;
        return (Math.random() < 0.3) ? CONTACT : Math.random() < 0.5 ? CHAT : PHOTO;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = null;
        switch (viewType) {
            case 0:
                mView = new CellChat(parent.getContext());
                break;
            case 1:
                mView = new CellPhoto(parent.getContext());
                break;
            case 2:
                mView = new CellContact(parent.getContext());
                break;
        }
//        return new Holder((LayoutInflater.from(parent.getContext())).inflate(R.layout.inflater_favourite_screen, parent, false));
        return new Holder(mView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        if (position % 2 == 0) {
           // ((Cell) holder.itemView).setUpView(false,);
        } else {
           // ((Cell) holder.itemView).setUpView(true);
        }
    }

    @Override
    public int getItemCount() {
        return mListChats.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }
}