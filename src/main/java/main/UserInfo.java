package main;

public class UserInfo {
    public int userId;
    public int schoolId;
    public int groupId;
    public UserStatus status;
    public String msg_text;
    public String properties;
    public boolean isAdmin;

    UserInfo(int id, UserStatus status, String msg_text, boolean isAdmin)
    {
        userId = id;
        schoolId = -1;
        groupId = -1;
        this.status = status;
        this.msg_text = msg_text;
        properties = "";
        this.isAdmin = isAdmin;
    }


}
