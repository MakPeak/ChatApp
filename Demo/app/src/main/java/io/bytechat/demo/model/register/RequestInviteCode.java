package io.bytechat.demo.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestInviteCode {

    @SerializedName("operationID")
    @Expose
    private String operationID;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("os")
    @Expose
    private String os;
    @SerializedName("Version")
    @Expose
    private String version;

    @SerializedName("screen_width")
    @Expose
    private Integer screenWidth;
    @SerializedName("language")
    @Expose
    private String language;


    public RequestInviteCode( String operationID , String timezone, String mobile, String os, String version, Integer screenWidth, String language) {
        this.operationID = operationID ;
        this.timezone = timezone;
        this.mobile = mobile;
        this.os = os;
        this.version = version;
        this.screenWidth = screenWidth;
        this.language = language;
    }

    public String getOperationID() {
        return operationID;
    }

    public void setOperationID(String operationID) {
        this.operationID = operationID;
    }

    @Override
    public String toString() {
        return "RequestInviteCode{" +
            ", code='" + code + '\'' +
            ", timezone='" + timezone + '\'' +
            ", mobile='" + mobile + '\'' +
            ", os='" + os + '\'' +
            ", version='" + version + '\'' +
            ", screenWidth=" + screenWidth +
            ", language='" + language + '\'' +
            '}';
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(Integer screenWidth) {
        this.screenWidth = screenWidth;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
