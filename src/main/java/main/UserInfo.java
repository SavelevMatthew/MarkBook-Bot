package main;

class UserInfo {
    int userId;
    int groupId;
    UserStatus status;
    String msg_text;
    String properties;
    String groupCode;
    boolean isAdmin;

    UserInfo(int id, UserStatus status, String msg_text, boolean isAdmin, String groupCode)
    {
        userId = id;
        int schoolId = -1;
        groupId = -1;
        this.status = status;
        this.msg_text = msg_text;
        properties = "";
        this.isAdmin = isAdmin;
        this.groupCode = groupCode;
    }


}
