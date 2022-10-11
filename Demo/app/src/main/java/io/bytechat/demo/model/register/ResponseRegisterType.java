package io.bytechat.demo.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.bytechat.demo.model.Data;

public class ResponseRegisterType {

    @SerializedName("data")
    @Expose
    private DataRegisterType data;
    @SerializedName("errCode")
    @Expose
    private Integer errCode;
    @SerializedName("errMsg")
    @Expose
    private String errMsg;

    public ResponseRegisterType(DataRegisterType data, Integer errCode, String errMsg) {
        this.data = data;
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "ResponseRegisterType{" +
            "data=" + data +
            ", errCode=" + errCode +
            ", errMsg='" + errMsg + '\'' +
            '}';
    }

    public DataRegisterType getData() {
        return data;
    }

    public void setData(DataRegisterType data) {
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
