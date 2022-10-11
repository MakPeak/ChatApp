package io.bytechat.demo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseOnlineStatus {

    @SerializedName("errCode")
    @Expose
    private Integer errCode;
    @SerializedName("errMsg")
    @Expose
    private String errMsg;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public ResponseOnlineStatus(Integer errCode, String errMsg, List<Datum> data) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseOnlineStatus{" +
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

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}
