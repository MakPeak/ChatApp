package io.bytechat.demo.model.register;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataRegisterType {

    @SerializedName("type")
    @Expose
    private List<Integer> type = null;

    public DataRegisterType(List<Integer> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DataRegisterType{" +
            "type=" + type +
            '}';
    }

    public List<Integer> getType() {
        return type;
    }

    public void setType(List<Integer> type) {
        this.type = type;
    }

}
