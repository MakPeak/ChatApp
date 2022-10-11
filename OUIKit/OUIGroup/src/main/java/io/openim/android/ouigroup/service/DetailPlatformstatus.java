package io.openim.android.ouigroup.service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetailPlatformstatus {

    @SerializedName("platform")
    @Expose
    private String platform;
    @SerializedName("status")
    @Expose
    private String status;

    public DetailPlatformstatus(String platform, String status) {
        this.platform = platform;
        this.status = status;
    }

    @Override
    public String toString() {
        return "DetailPlatformstatus{" +
            "platform='" + platform + '\'' +
            ", status='" + status + '\'' +
            '}';
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
