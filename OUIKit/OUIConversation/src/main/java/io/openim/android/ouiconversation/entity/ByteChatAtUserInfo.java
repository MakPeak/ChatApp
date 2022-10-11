package io.openim.android.ouiconversation.entity;

import android.annotation.SuppressLint;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.linkedin.android.spyglass.mentions.Mentionable;

import io.openim.android.sdk.models.AtUserInfo;

public class ByteChatAtUserInfo extends AtUserInfo implements Mentionable {


    @NonNull
    @Override
    public String getTextForDisplayMode(@NonNull MentionDisplayMode mode) {
        switch (mode) {
            case FULL:
                return getGroupNickname();
            case PARTIAL:
            case NONE:
            default:
                return "";
        }
    }

    private String faceURL;

    public String getFaceURL() {
        return faceURL;
    }

    public void setFaceURL(String faceURL) {
        this.faceURL = faceURL;
    }

    @NonNull
    @Override
    public MentionDeleteStyle getDeleteStyle() {
        return MentionDeleteStyle.PARTIAL_NAME_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return getGroupNickname().hashCode();
    }

    @NonNull
    @Override
    public String getSuggestiblePrimaryText() {
        return getGroupNickname();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getGroupNickname());
    }

    public ByteChatAtUserInfo(Parcel in){
        setGroupNickname(in.readString());
    }

    public ByteChatAtUserInfo() {
    }

    public static final Creator<ByteChatAtUserInfo> CREATOR
        = new Creator<ByteChatAtUserInfo>() {
        public ByteChatAtUserInfo createFromParcel(Parcel in) {
            return new ByteChatAtUserInfo(in);
        }

        public ByteChatAtUserInfo[] newArray(int size) {
            return new ByteChatAtUserInfo[size];
        }
    };

}
