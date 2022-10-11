package io.openim.android.ouigroup.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestOnlineStatus {

    @SerializedName("operationID")
    @Expose
    private String operationID;
    @SerializedName("userIDList")
    @Expose
    private List<String> userIDList = null;

    public RequestOnlineStatus(String operationID, List<String> userIDList) {
        this.operationID = operationID;
        this.userIDList = userIDList;
    }

    @Override
    public String toString() {
        return "RequestOnlineStatus{" +
            "operationID='" + operationID + '\'' +
            ", userIDList=" + userIDList +
            '}';
    }

    public String getOperationID() {
        return operationID;
    }

    public void setOperationID(String operationID) {
        this.operationID = operationID;
    }

    public List<String> getUserIDList() {
        return userIDList;
    }

    public void setUserIDList(List<String> userIDList) {
        this.userIDList = userIDList;
    }

}
