package io.bytechat.demo.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestRegister {

    @SerializedName("Platform")
    @Expose
    private Integer platform;
    @SerializedName("operationID")
    @Expose
    private String operationID;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("invite_code")
    @Expose
    private String inviteCode;
    @SerializedName("verification_id")
    @Expose
    private String verificationId;
    @SerializedName("verification_code")
    @Expose
    private String verificationCode;

    public RequestRegister(Integer platform, String operationID, String phoneNumber, String password, String inviteCode, String verificationId, String verificationCode) {
        this.platform = platform;
        this.operationID = operationID;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.inviteCode = inviteCode;
        this.verificationId = verificationId;
        this.verificationCode = verificationCode;
    }

    @Override
    public String toString() {
        return "RequestRegister{" +
            "platform=" + platform +
            ", operationID='" + operationID + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", password='" + password + '\'' +
            ", inviteCode='" + inviteCode + '\'' +
            ", verificationId='" + verificationId + '\'' +
            ", verificationCode='" + verificationCode + '\'' +
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

}
