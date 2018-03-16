package com.wachat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.callBack.AdapterCallback;
import com.wachat.data.DataLanguages;

import java.util.ArrayList;

/**
 * Created by Gourav Kundu on 19-08-2015.
 */
public class AdapterSelectLanguage extends RecyclerView.Adapter<AdapterSelectLanguage.VH> implements View.OnClickListener {

    private LayoutInflater layoutInflater;
    private AdapterCallback mCallback;
    private ArrayList<DataLanguages> languageList;

    public AdapterSelectLanguage(Context mContext, ArrayList<DataLanguages> languageList, AdapterCallback mCallback) {

        this.layoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.languageList = languageList;
        this.mCallback = mCallback;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH((LayoutInflater.from(parent.getContext())).inflate(R.layout.inflater_select_language_screen, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.tv_language.setText(languageList.get(position).getName());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.tv_language:
        int pos = (Integer) v.getTag();
        mCallback.OnClickPerformed(languageList.get(pos));
//                break;
//        }
    }

    public static class VH extends RecyclerView.ViewHolder {

        private TextView tv_language;
        private int pos;

        public VH(View itemView) {
            super(itemView);
            tv_language = (TextView) itemView
                    .findViewById(R.id.tv_language);

        }
    }


    public void refreshList(ArrayList<DataLanguages> mDataLanguages) {
        this.languageList = mDataLanguages;
        notifyDataSetChanged();
    }

}
