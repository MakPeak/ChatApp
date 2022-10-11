package io.bytechat.demo.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestImageVerificationCode {

    @SerializedName("Platform")
    @Expose
    private Integer platform;
    @SerializedName("operationID")
    @Expose
    private String operationID;

    public RequestImageVerificationCode(Integer platform, String operationID) {
        this.platform = platform;
        this.operationID = operationID;
    }

    @Override
    public String toString() {
        return "RequestImageVerificationCode{" +
            "platform=" + platform +
            ", operationID='" + operationID + '\'' +
            '}';
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getOperationID() {
        return operationID;
    }

    public void setOperationID(String operationID) {
        this.operationID = operationID;
    }

}
