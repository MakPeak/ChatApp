package io.bytechat.demo.model.register;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataRegister {

    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("expiredTime")
    @Expose
    private Integer expiredTime;

    public DataRegister(String userID, String token, Integer expiredTime) {
        this.userID = userID;
        this.token = token;
        this.expiredTime = expiredTime;
    }

    @Override
    public String toString() {
        return "DataRegister{" +
            "userID='" + userID + '\'' +
            ", token='" + token + '\'' +
            ", expiredTime=" + expiredTime +
            '}';
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Integer expiredTime) {
        this.expiredTime = expiredTime;
    }

}
