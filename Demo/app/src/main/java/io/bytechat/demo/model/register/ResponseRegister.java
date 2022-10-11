package io.bytechat.demo.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseRegister {

    @SerializedName("data")
    @Expose
    private DataRegister data;
    @SerializedName("errCode")
    @Expose
    private Integer errCode;
    @SerializedName("errMsg")
    @Expose
    private String errMsg;

    public ResponseRegister(DataRegister data, Integer errCode, String errMsg) {
        this.data = data;
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "ResponseRegister{" +
            "data=" + data +
            ", errCode=" + errCode +
            ", errMsg='" + errMsg + '\'' +
            '}';
    }

    public DataRegister getData() {
        return data;
    }

    public void setData(DataRegister data) {
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
