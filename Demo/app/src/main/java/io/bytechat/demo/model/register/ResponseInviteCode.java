package io.bytechat.demo.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseInviteCode {

    @SerializedName("errCode")
    @Expose
    private Integer errCode;
    @SerializedName("errMsg")
    @Expose
    private String errMsg;
    @SerializedName("data")
    @Expose
    private DataInviteCode data;

    public ResponseInviteCode(Integer errCode, String errMsg, DataInviteCode data) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseInviteCode{" +
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

    public DataInviteCode getData() {
        return data;
    }

    public void setData(DataInviteCode data) {
        this.data = data;
    }

}
