package io.bytechat.demo.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataImageVerificationCode {

    @SerializedName("invite_code")
    @Expose
    private String inviteCode;
    @SerializedName("image")
    @Expose
    private String image;

    public DataImageVerificationCode(String inviteCode, String image) {
        this.inviteCode = inviteCode;
        this.image = image;
    }

    @Override
    public String toString() {
        return "DataImageVerificationCode{" +
            "inviteCode='" + inviteCode + '\'' +
            ", image='" + image + '\'' +
            '}';
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
