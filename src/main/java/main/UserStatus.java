package main;

public enum UserStatus {
    //public static String HELLO = "hello"; // юзер впервые отправил боту /start
    //public  static String GETGROUPID = "get_group_id"; // ждем от юзера код группы
    //public static String DEFAULT = "default"; //главное меню

    HELLO("HELLO"), GET_GROUPID("GET_GROUPID"), DEFAULT("DEFAULT"), NOT_EXISTS("NOT_EXISTS");

    private final String code;

    UserStatus(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
