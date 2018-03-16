package com.wachat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wachat.R;

/**
 * Created by Priti Chatterjee on 18-08-2015.
 */
public class AdapterCommonRecycler extends RecyclerView.Adapter<AdapterCommonRecycler.ViewHolder> {

    private String[] mDataset;
//    private Context mcContext;
    String time;
    int resId;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tv_ProfileName, tv_Status, tv_receive_time;
        public ImageView iv_ProfileImage, iv_Favourie;

        public ViewHolder(View v) {
            super(v);
            tv_ProfileName = (TextView) v
                    .findViewById(R.id.tv_ProfileName);
            tv_Status = (TextView) v.findViewById(R.id.tv_Status);
            iv_ProfileImage = (ImageView) v.findViewById(R.id.iv_ProfileImage);
            tv_receive_time = (TextView) v.findViewById(R.id.tv_receive_time);
            iv_Favourie = (ImageView) v.findViewById(R.id.iv_Favourie);

        }
    }

    public AdapterCommonRecycler(Context mContexts, String[] mDataset) {

        this.mDataset = mDataset;
        time=mContexts.getResources().getString(R.string.receive_at) + " 02:30AM";
        resId= R.drawable.ic_launcher;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.inflater_chat_screen, parent, false));
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tv_ProfileName.setText(mDataset[position]);
        holder.tv_Status.setText(mDataset[position]);
        holder.tv_receive_time.setText(time);
        holder.iv_ProfileImage.setImageResource(resId);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
