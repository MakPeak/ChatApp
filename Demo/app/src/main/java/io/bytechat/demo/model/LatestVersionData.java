package io.bytechat.demo.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatestVersionData {

    @SerializedName("Content")
    @Expose
    private String content;
    @SerializedName("DownloadUrl")
    @Expose
    private String downloadUrl;
    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("Isforce")
    @Expose
    private Integer isforce;
    @SerializedName("Status")
    @Expose
    private Integer status;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Type")
    @Expose
    private Integer type;
    @SerializedName("Version")
    @Expose
    private String version;

    public LatestVersionData(String content, String downloadUrl, String id, Integer isforce, Integer status, String title, Integer type, String version) {
        this.content = content;
        this.downloadUrl = downloadUrl;
        this.id = id;
        this.isforce = isforce;
        this.status = status;
        this.title = title;
        this.type = type;
        this.version = version;
    }

    @Override
    public String toString() {
        return "LatestVersionData{" +
            "content='" + content + '\'' +
            ", downloadUrl='" + downloadUrl + '\'' +
            ", id='" + id + '\'' +
            ", isforce=" + isforce +
            ", status=" + status +
            ", title='" + title + '\'' +
            ", type=" + type +
            ", version='" + version + '\'' +
            '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIsforce() {
        return isforce;
    }

    public void setIsforce(Integer isforce) {
        this.isforce = isforce;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
