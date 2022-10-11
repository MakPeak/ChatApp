package io.bytechat.demo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("PlatformId")
    @Expose
    private Integer platformId;
    @SerializedName("Status")
    @Expose
    private Integer status;
    @SerializedName("Url")
    @Expose
    private String url;

    public Data(Integer platformId, Integer status, String url) {
        this.platformId = platformId;
        this.status = status;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Data{" +
            "platformId=" + platformId +
            ", status=" + status +
            ", url='" + url + '\'' +
            '}';
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
