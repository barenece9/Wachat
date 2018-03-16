package com.wachat.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wachat.chatUtils.ConstantChat;
import com.wachat.customViews.components.Cell;
import com.wachat.customViews.components.CellChat;
import com.wachat.customViews.components.CellContact;
import com.wachat.customViews.components.CellGroupNotification;
import com.wachat.customViews.components.CellLocation;
import com.wachat.customViews.components.CellPhoto;
import com.wachat.customViews.components.CellSticker;
import com.wachat.customViews.components.CellUnknownFriendFooter;
import com.wachat.customViews.components.CellVideo;
import com.wachat.customViews.components.CellYahooNews;
import com.wachat.customViews.components.CellYoutubeVideo;
import com.wachat.data.DataTextChat;
import com.wachat.storage.TableChat;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Gourav Kundu on 24-08-2015.
 */
public class AdapterChat extends RecyclerView.Adapter<AdapterChat.Holder> {

    public void setShowAcceptRejectFooter(boolean showAcceptRejectFooter) {
        this.showAcceptRejectFooter = showAcceptRejectFooter;
    }

    private boolean showAcceptRejectFooter = false;
    private ArrayList<DataTextChat> mListChats = new ArrayList<DataTextChat>();
    private Activity mActivity;

    private TreeSet<String> originalTextShowingPositionSet = new TreeSet<String>();

    private String friendOrGroupChatID = "", myChatId = "", chatType = "";

    public static final int CHAT = 0, PHOTO = 1, CONTACT = 2, LOCATION = 3,
            VIDEO = 4, YOUTUBE = 5, YAHOO = 6, TYPE_FOOTER = 7,
            FIRST_TIME_STICKER_TYPE = 8,
            NOTIFICATION_TYPE_CREATED = 9,
            NOTIFICATION_TYPE_ADDED = 10,
            NOTIFICATION_TYPE_REMOVED = 11,
            NOTIFICATION_TYPE_LEFT = 12;
    Cell.OnCellItemClickListener clickListener;
    Cell.OnCellItemLongClickListener longClickListener;

    public AdapterChat(ArrayList<DataTextChat> mListChats, String chatType,
                       String friendOrGroupChatID, String myChatId,
                       Activity mActivity, Cell.OnCellItemClickListener clickListener,
                       Cell.OnCellItemLongClickListener longClickListener,
                       boolean showAcceptRejectFooter) {

        originalTextShowingPositionSet = new TreeSet<String>();
        this.mListChats = mListChats;
        this.friendOrGroupChatID = friendOrGroupChatID;
        this.myChatId = myChatId;
        this.chatType = chatType;
        this.mActivity = mActivity;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.showAcceptRejectFooter = showAcceptRejectFooter;
    }

    public void constructorData(String chatType,
                                String friendOrGroupChatID, String myChatId,
                                boolean showAcceptRejectFooter){
        originalTextShowingPositionSet = new TreeSet<String>();
        this.friendOrGroupChatID = friendOrGroupChatID;
        this.myChatId = myChatId;
        this.chatType = chatType;
        this.showAcceptRejectFooter = showAcceptRejectFooter;

    }
    public void setData(ArrayList<DataTextChat> mListChats){
        this.mListChats = mListChats;
        this.notifyDataSetChanged();
    }
    public int getItemViewType(int position) {
        //Some logic to know which type will come next;
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        } else if (position < mListChats.size()) {
            if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.MESSAGE_TYPE))
                return CHAT;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.IMAGE_TYPE))
                return PHOTO;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.IMAGECAPTION_TYPE))
                return PHOTO;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.SKETCH_TYPE))
                return PHOTO;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.CONTACT_TYPE))
                return CONTACT;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.LOCATION_TYPE))
                return LOCATION;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.VIDEO_TYPE))
                return VIDEO;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.YOUTUBE_TYPE))
                return YOUTUBE;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.YAHOO_TYPE))
                return YAHOO;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.FIRST_TIME_STICKER_TYPE))
                return FIRST_TIME_STICKER_TYPE;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_ADDED))
                return NOTIFICATION_TYPE_ADDED;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_CREATED))
                    return NOTIFICATION_TYPE_CREATED;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_LEFT))
                return NOTIFICATION_TYPE_LEFT;
            else if (mListChats.get(position).getMessageType().equalsIgnoreCase(ConstantChat.NOTIFICATION_TYPE_REMOVED))
                return NOTIFICATION_TYPE_REMOVED;
            else
                return CHAT;
        } else
            return CHAT;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = null;
        switch (viewType) {
            case TYPE_FOOTER:
                mView = new CellUnknownFriendFooter(parent.getContext());
                break;
            case CHAT:
                mView = new CellChat(parent.getContext());
                break;
            case PHOTO:
                mView = new CellPhoto(parent.getContext());
                break;
            case CONTACT:
                mView = new CellContact(parent.getContext());
                break;
            case LOCATION:
                mView = new CellLocation(parent.getContext());
                break;
            case VIDEO:
                mView = new CellVideo(parent.getContext());
                break;
            case YOUTUBE:
                mView = new CellYoutubeVideo(parent.getContext());
                break;
            case YAHOO:
                mView = new CellYahooNews(parent.getContext());
                break;
            case FIRST_TIME_STICKER_TYPE:
                mView = new CellSticker(parent.getContext());
                break;
            case NOTIFICATION_TYPE_ADDED:
                mView = new CellGroupNotification(parent.getContext());
                break;
            case NOTIFICATION_TYPE_CREATED:
                mView = new CellGroupNotification(parent.getContext());
                break;
            case NOTIFICATION_TYPE_LEFT:
                mView = new CellGroupNotification(parent.getContext());
                break;
            case NOTIFICATION_TYPE_REMOVED:
                mView = new CellGroupNotification(parent.getContext());
                break;
        }

        return new Holder(mView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (isPositionFooter(position)) {
            ((Cell) holder.itemView).setUpClickListener(clickListener);
            ((Cell) holder.itemView).setUpView(false, mListChats.get(position - 1));
        } else {
            if (position < mListChats.size()) {

                if (holder.itemView instanceof CellChat) {
                    if (originalTextShowingPositionSet.contains(mListChats.get(position).getMessageid())) {
                        ((CellChat) holder.itemView).setShowOriginalTextFirst(true);
                    } else {
                        ((CellChat) holder.itemView).setShowOriginalTextFirst(false);
                    }
                    ((CellChat) holder.itemView).setTranslationClickCallback(position, new CellChat.TranslationClickCallback() {
                        @Override
                        public void onClick(DataTextChat mDataTextChat) {
                            if (originalTextShowingPositionSet.contains(mDataTextChat.getMessageid())) {
                                originalTextShowingPositionSet.remove(mDataTextChat.getMessageid());
                            } else {
                                originalTextShowingPositionSet.add(mDataTextChat.getMessageid());
                            }
                        }
                    });
                }
                ((Cell) holder.itemView).setUpClickListener(clickListener);
                ((Cell) holder.itemView).setUpLongPressListener(longClickListener);
                if (!mListChats.get(position).getSenderChatID().equalsIgnoreCase(myChatId)) {
                    ((Cell) holder.itemView).setUpView(false, mListChats.get(position));

                } else {
                    ((Cell) holder.itemView).setUpView(true, mListChats.get(position));
                }
            }
        }
    }

    private boolean isPositionFooter(int position) {
        if (showAcceptRejectFooter) {
            return position == mListChats.size();
        } else {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        if (showAcceptRejectFooter) {
            return mListChats.size() + 1;
        } else {
            return mListChats.size();
        }
    }

    public ArrayList<DataTextChat> getData() {
        return mListChats;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    public void refreshChatList(boolean shouldShowAcceptRejectFooter) {
        this.showAcceptRejectFooter = shouldShowAcceptRejectFooter;
        if (chatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mListChats = new TableChat(mActivity).getAllChat(friendOrGroupChatID);
        } else if (chatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            mListChats = new TableChat(mActivity).getAllGroupChat(friendOrGroupChatID);
        }
        this.notifyDataSetChanged();

    }

    public void refreshChatList() {
        if (chatType.equalsIgnoreCase(ConstantChat.TYPE_SINGLECHAT)) {
            mListChats = new TableChat(mActivity).getAllChat(friendOrGroupChatID);
        } else if (chatType.equalsIgnoreCase(ConstantChat.TYPE_GROUPCHAT)) {
            mListChats = new TableChat(mActivity).getAllGroupChat(friendOrGroupChatID);
        }
        this.notifyDataSetChanged();

    }

    public void AddNewMessageInUI(DataTextChat mDataTextChat) {
        mListChats.add(mDataTextChat);

        this.notifyDataSetChanged();

    }


    class FooterViewHolder extends Holder {
        TextView txtTitleFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}