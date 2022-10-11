package io.bytechat.demo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datum {

    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("detailPlatformStatus")
    @Expose
    private List<DetailPlatformstatus> detailPlatformStatus = null;

    public Datum(String userID, String status, List<DetailPlatformstatus> detailPlatformStatus) {
        this.userID = userID;
        this.status = status;
        this.detailPlatformStatus = detailPlatformStatus;
    }

    @Override
    public String toString() {
        return "Datum{" +
            "userID='" + userID + '\'' +
            ", status='" + status + '\'' +
            ", detailPlatformStatus=" + detailPlatformStatus +
            '}';
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DetailPlatformstatus> getDetailPlatformStatus() {
        return detailPlatformStatus;
    }

    public void setDetailPlatformStatus(List<DetailPlatformstatus> detailPlatformStatus) {
        this.detailPlatformStatus = detailPlatformStatus;
    }

}
