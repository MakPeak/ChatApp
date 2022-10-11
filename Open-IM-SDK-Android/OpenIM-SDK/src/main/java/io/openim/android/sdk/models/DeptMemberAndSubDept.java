package io.openim.android.sdk.models;

import java.util.List;

public class DeptMemberAndSubDept {
    List<DeptInfo> departmentList;
    List<DeptMemberInfo> departmentMemberList;

    public List<DeptInfo> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<DeptInfo> departmentList) {
        this.departmentList = departmentList;
    }

    public List<DeptMemberInfo> getDepartmentMemberList() {
        return departmentMemberList;
    }

    public void setDepartmentMemberList(List<DeptMemberInfo> departmentMemberList) {
        this.departmentMemberList = departmentMemberList;
    }
}
