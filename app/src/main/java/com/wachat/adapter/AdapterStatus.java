package com.wachat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataStatus;

import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 26-08-2015.
 */
public class AdapterStatus extends RecyclerView.Adapter<AdapterStatus.VH> {

    private LayoutInflater layoutInflater;
    private Context mContext;
    private ArrayList<DataStatus> status_list;
    private AdapterCallback mCallback;

    public int selectedPosition=-1;

    public AdapterStatus(Context mContext,ArrayList<DataStatus> status_list, AdapterCallback mCallback) {
        this.layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = mContext;
        this.status_list = status_list;
        this.mCallback=mCallback;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH((LayoutInflater.from(parent.getContext())).inflate(R.layout.inflater_status, parent, false));
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        holder.tv_status.setText(status_list.get(position).getUserStatus());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                int pos = (Integer)v.getTag();
                notifyDataSetChanged();
                mCallback.OnClickPerformed(status_list.get(pos));
            }
        });

        if(selectedPosition==position)
            holder.tv_status.setSelected(true);
        //holder.tv_status.setTextColor(mContext.getResources().getColor(R.color.view_yellow_color));
        else
            holder.tv_status.setSelected(false);
            //holder.tv_status.setTextColor(mContext.getResources().getColor(R.color.app_Brown));
    }

    @Override
    public int getItemCount() {

        return status_list.size();
    }



    public static class VH extends RecyclerView.ViewHolder {
        public TextView tv_status;

        public VH(View itemView) {
            super(itemView);
            tv_status=(TextView)itemView.findViewById(R.id.tv_status);

        }
    }

    public void refreshList(ArrayList<DataStatus> mStatusList){
        this.status_list = mStatusList;
        this.notifyDataSetChanged();
    }
}
