package io.openim.android.ouicore.entity;

import android.util.Log;

import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import io.openim.android.sdk.models.ConversationInfo;
import io.openim.android.sdk.models.Message;

//解析最后的消息
public class MsgConversation {
    public Message lastMsg;
    public ConversationInfo conversationInfo;

    public MsgConversation(Message lastMsg, ConversationInfo conversationInfo) {


        if(conversationInfo.getConversationType() == 4){
            try {
                JSONObject json = new JSONObject(lastMsg.getContent());
                JSONObject json1 = new JSONObject(json.getString("jsonDetail"));
                //JSONObject json2=new JSONObject(json1.getString("group"));
                lastMsg.setContent(json1.getString("text"));
            } catch (JSONException e) {
                // your output is not in json format
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.lastMsg = lastMsg;
        this.conversationInfo = conversationInfo;
    }
}
