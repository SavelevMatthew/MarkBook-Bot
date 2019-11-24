package main;

public class UserInfo {
    public int userId;
    public int schoolId;
    public int groupId;
    public String status;
    public String msg_text;
    public String[] properties;

    UserInfo(int id, String status, String msg_text)
    {
        userId = id;
        schoolId = -1;
        groupId = -1;
        this.status = status;
        this.msg_text = msg_text;
        properties = new String[] {};
    }

}
