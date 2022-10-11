package io.bytechat.demo.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataInviteSwitch {

    @SerializedName("switch")
    @Expose
    private Integer _switch;

    public DataInviteSwitch(Integer _switch) {
        this._switch = _switch;
    }

    @Override
    public String toString() {
        return "DataInviteSwitch{" +
            "_switch=" + _switch +
            '}';
    }

    public Integer getSwitch() {
        return _switch;
    }

    public void setSwitch(Integer _switch) {
        this._switch = _switch;
    }

}
