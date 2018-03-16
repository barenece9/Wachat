package com.wachat.chatUtils;

import android.content.Context;

import com.wachat.data.DataTextChat;
import com.wachat.util.CommonMethods;
import com.wachat.util.DateTimeUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by Argha on 07-11-2015.
 */
public class ConversationEmailTxtComposer {
    Context mContext;

    public ConversationEmailTxtComposer(Context mContext) {
        this.mContext = mContext;
    }

    public String getOneToOneChatConversation(ArrayList<DataTextChat> mArrayList, String friendChatId) {
        ArrayList<String> emailTxt = new ArrayList<String>();
        String resultTxt = "";
        if (mArrayList.size() == 0) {
            return resultTxt;
        }

        for (int i = 0; i < mArrayList.size(); i++) {
            DataTextChat mDataTextChat = mArrayList.get(i);
            String name = "Me";
            if (StringUtils.equalsIgnoreCase(mDataTextChat.getSenderChatID(), friendChatId)) {
                name = (mDataTextChat.getAppName().equals("")) ? ((mDataTextChat.getName().equals("")) ?
                        mDataTextChat.getFriendPhoneNo() : mDataTextChat.getName()) : mDataTextChat.getAppName();
                name = CommonMethods.getUTFDecodedString(name);
            }
            resultTxt = DateTimeUtil.convertUtcToLocal(mArrayList.get(i).getTimestamp(),
                    mContext.getResources().getConfiguration().locale) + " "
                    + name +
                    ":" + CommonMethods.getUTFDecodedString(mArrayList.get(i).getBody()) + "\n";
            emailTxt.add(resultTxt);
        }


        return emailTxt.toString();
    }
}
