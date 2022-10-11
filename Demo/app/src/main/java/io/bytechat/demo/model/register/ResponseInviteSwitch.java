package io.bytechat.demo.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseInviteSwitch {

    @SerializedName("data")
    @Expose
    private DataInviteSwitch data;
    @SerializedName("errCode")
    @Expose
    private Integer errCode;
    @SerializedName("errMsg")
    @Expose
    private String errMsg;

    public ResponseInviteSwitch(DataInviteSwitch data, Integer errCode, String errMsg) {
        this.data = data;
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "ResponseInviteSwitch{" +
            "data=" + data +
            ", errCode=" + errCode +
            ", errMsg='" + errMsg + '\'' +
            '}';
    }

    public DataInviteSwitch getData() {
        return data;
    }

    public void setData(DataInviteSwitch data) {
        this.data = data;
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

}
