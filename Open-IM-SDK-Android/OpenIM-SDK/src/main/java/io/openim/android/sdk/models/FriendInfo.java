package io.openim.android.sdk.models;

/**
 * 好友信息
 */
public class FriendInfo {
    /**
     * 好友id
     */
    private String userID;
    /**
     * 好友昵称
     */
    private String nickname;
    /**
     * 好友头像
     */
    private String faceURL;
    /**
     * 性别
     */
    private int gender;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 出生日期
     */
    private String birth;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 好友备注名
     */
    private String remark;
    /**
     * 扩展字段
     */
    private String ex;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 添加方式
     */
    private int addSource;
    /**
     * 操作者id
     */
    private String operatorUserID;

    public FriendInfo(String userID, String nickname, String faceURL, int gender, String phoneNumber, String birth, String email, String remark, String ex, long createTime, int addSource, String operatorUserID) {
        this.userID = userID;
        this.nickname = nickname;
        this.faceURL = faceURL;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
        this.email = email;
        this.remark = remark;
        this.ex = ex;
        this.createTime = createTime;
        this.addSource = addSource;
        this.operatorUserID = operatorUserID;
    }

    public FriendInfo() {
    }

    @Override
    public String toString() {
        return "FriendInfo{" +
            "userID='" + userID + '\'' +
            ", nickname='" + nickname + '\'' +
            ", faceURL='" + faceURL + '\'' +
            ", gender=" + gender +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", birth='" + birth + '\'' +
            ", email='" + email + '\'' +
            ", remark='" + remark + '\'' +
            ", ex='" + ex + '\'' +
            ", createTime=" + createTime +
            ", addSource=" + addSource +
            ", operatorUserID='" + operatorUserID + '\'' +
            '}';
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFaceURL() {
        return faceURL;
    }

    public void setFaceURL(String faceURL) {
        this.faceURL = faceURL;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEx() {
        return ex;
    }

    public void setEx(String ex) {
        this.ex = ex;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getAddSource() {
        return addSource;
    }

    public void setAddSource(int addSource) {
        this.addSource = addSource;
    }

    public String getOperatorUserID() {
        return operatorUserID;
    }

    public void setOperatorUserID(String operatorUserID) {
        this.operatorUserID = operatorUserID;
    }
}
