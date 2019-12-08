package main;

class UserInfo {
    int userId;
    int groupId;
    UserStatus status;
    String msg_text;
    String properties;
    boolean isAdmin;

    UserInfo(int id, UserStatus status, String msg_text, boolean isAdmin)
    {
        userId = id;
        int schoolId = -1;
        groupId = -1;
        this.status = status;
        this.msg_text = msg_text;
        properties = "";
        this.isAdmin = isAdmin;
    }


}
