package main;

public enum UserStatus {
    //public static String HELLO = "hello"; // юзер впервые отправил боту /start
    //public  static String GETGROUPID = "get_group_id"; // ждем от юзера код группы
    //public static String DEFAULT = "default"; //главное меню

    HELLO("HELLO"),
    GET_GROUPID("GET_GROUPID"),
    DEFAULT("DEFAULT"),
    NOT_EXISTS("NOT_EXISTS"),
    GET_LESSON1("GET_LESSON1"),
    GET_LESSON2("GET_LESSON2"),
    GET_LESSON3("GET_LESSON3"),
    GET_LESSON4("GET_LESSON4"),
    GET_LESSON5("GET_LESSON5"),
    GET_LESSON6("GET_LESSON6"),
    GET_LESSON7("GET_LESSON7"),
    GET_LESSON_NAME("GET_LESSON_NAME"),
    GET_HOMETASK("GET_HOMETASK"),
    GET_GROUPNAME("GET_GROUPNAME"),
    GET_FIRSTLESSON_NUMBER("GET_FIRSTLESSON_NUMBER"),
    GET_SUPPORT_TEXT("GET_SUPPORT_TEXT");

    private final String code;

    UserStatus(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
