package io.bytechat.demo.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataInviteCode {

    @SerializedName("invite_code")
    @Expose
    private String channelCode;

    public DataInviteCode(String channelCode) {
        this.channelCode = channelCode;
    }

    @Override
    public String toString() {
        return "DataInviteCode{" +
            "channelCode='" + channelCode + '\'' +
            '}';
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

}
