package io.bytechat.demo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatestVersionResponse {

    @SerializedName("errCode")
    @Expose
    private Integer errCode;
    @SerializedName("errMsg")
    @Expose
    private String errMsg;
    @SerializedName("data")
    @Expose
    private LatestVersionData data;

    public LatestVersionResponse(Integer errCode, String errMsg, LatestVersionData data) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }

    @Override
    public String toString() {
        return "DiscoverResponse{" +
            "errCode=" + errCode +
            ", errMsg='" + errMsg + '\'' +
            ", data=" + data +
            '}';
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public LatestVersionData getData() {
        return data;
    }

    public void setData(LatestVersionData data) {
        this.data = data;
    }

}
