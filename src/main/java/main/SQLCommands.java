package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class SQLCommands {
    final private static Logger SQLLogger = Logger.getLogger(Bot.class.getName());

    private static Connection GetSQLConnection() throws java.sql.SQLException {


        String url = "";
        String user = "";
        String password = "";

        try
        {
            Path path = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "main", "resources", "sql_config");
            Scanner sc = new Scanner(new File(path.toString()));
            String [] splitted;
            if (sc.hasNext()) {
                splitted = sc.nextLine().split(" ");
                url = splitted[0];
                user = splitted[1];
                password = splitted[2];
            }
        } catch (FileNotFoundException e) {
            SQLLogger.log(Level.SEVERE, "Exception: ", e.toString());
        }

        return DriverManager.getConnection(url, user, password);
    }

    static UserInfo GetUserInfo(int userId) {
        String query = "select * from users where id = ?;";
        UserInfo user = new UserInfo(userId, UserStatus.NOT_EXISTS, "", false, "");

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                user.status = UserStatus.valueOf(rs.getString(3));
                user.groupId = rs.getInt(2);
                user.isAdmin = rs.getBoolean(4);
                user.properties = rs.getString(5);
                user.groupCode = rs.getString(6);
            }
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return user;
    }

    static void AddUserToSQL(UserInfo user) {
        String query = "INSERT INTO users(id, status) VALUES(?, ?)";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.userId);
            pst.setString(2, user.status.toString());
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    static void UpdateUserInfo(UserInfo user) {
        String query = "UPDATE users SET groupid = ?, status = ?, properties = ?, isadmin = ?, groupcode = ?  WHERE id = ?";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.groupId);
            pst.setString(2, user.status.toString());
            pst.setString(3, user.properties);
            pst.setBoolean(4, user.isAdmin);
            pst.setString(5, user.groupCode);
            pst.setInt(6, user.userId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    static String GetGroupName(String groupCode) {
        String query = "select * from groups where groupcode = ?";
        String groupName = "";
        int groupId = -1;

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, groupCode);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                groupName = rs.getString(3);
                groupId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return Integer.toString(groupId) + '!' + groupName;
    }

    static ArrayList<String> GetLessonListByWeekDay(UserInfo user, String dayOfWeek, boolean iseven) {
        ArrayList<String> result = new ArrayList<>();
        String query = "select * from timetable where groupid = ? and weekday = ? and iseven = ?";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.groupId);
            pst.setString(2, dayOfWeek);
            pst.setBoolean(3, iseven);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                for (int x = 4; x < 11; x++) {
                    result.add(rs.getString(x));
                }
            }

        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

    static ArrayList<String> GetLessonList(UserInfo user) {
        ArrayList<String> result = new ArrayList<>();
        String query = "select * from hometasks where groupid = ?";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.groupId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(2));
            }

        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

    static void UpdateHometask(UserInfo user, String lesson, String hometask) {
        String query = "UPDATE hometasks SET hometask_text = ? WHERE groupid = ? AND lesson = ?";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, hometask);
            pst.setInt(2, user.groupId);
            pst.setString(3, lesson);
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    static void AddLesson(UserInfo user, String lesson) {
        String query = "INSERT INTO hometasks(groupid, lesson, hometask_text) VALUES(?, ?, ?)";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.groupId);
            pst.setString(2, lesson);
            pst.setString(3, "Ничего не задано");
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    static String GetHometask(UserInfo user, String lesson) {
        String query = "select * from hometasks where groupid = ? and lesson = ?;";
        String hometask_text = "";
        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.groupId);
            pst.setString(2, lesson);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                hometask_text = rs.getString(3);
            }
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return hometask_text;
    }

    static void InitTimetableDay(UserInfo user, String firstlesson) {
        String query = "INSERT INTO timetable(groupid, weekday, firstlesson, iseven) VALUES(?, ?, ?, ?)";
        String[] splitted = user.properties.split(", ");
        String weekDay = splitted[0];
        Boolean iseven = Boolean.valueOf(splitted[1]);

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.groupId);
            pst.setString(2, weekDay);
            pst.setString(3, firstlesson);
            pst.setBoolean(4, iseven);
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    static void EditTimetableDayFirstLesson(UserInfo user, String firstlesson) {
        String query = "update timetable set firstlesson = ? where groupid = ? and weekday = ? and iseven = ?";
        String[] splitted = user.properties.split(", ");
        String weekDay = splitted[0];
        Boolean iseven = Boolean.valueOf(splitted[1]);

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, firstlesson);
            pst.setInt(2, user.groupId);
            pst.setString(3, weekDay);
            pst.setBoolean(4, iseven);
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    static void UpdateTimetable(UserInfo user, int numLesson, String lesson) {
        String query = String.format("UPDATE timetable SET lesson%d = ? WHERE groupid = ? and weekday = ? and iseven = ?", numLesson);
        String[] splitted = user.properties.split(", ");
        String weekDay = splitted[0];
        Boolean iseven = Boolean.valueOf(splitted[1]);

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, lesson);
            pst.setString(3, weekDay);
            pst.setInt(2, user.groupId);
            pst.setBoolean(4, iseven);
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    static void CreateNewGroup(UserInfo user, String groupName) {
        user.groupCode = CodeGenerator.generateCode(10);
        String query = "INSERT INTO groups(groupid, groupname, groupcode) VALUES(?, ?, ?)";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(2, groupName);
            pst.setInt(1, user.groupId);
            pst.setString(3, user.groupCode);
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        user.isAdmin = true;
        UpdateUserInfo(user);
    }

    static String GetFirstLessonNumber(UserInfo user, String weekDay) {
        ArrayList<String> result = new ArrayList<>();
        String query = "select * from timetable where groupid = ? and weekday = ?";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.groupId);
            pst.setString(2, weekDay);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString(3);
            }

        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return "0";
    }

    static void CopyTimetable(UserInfo user, String weekDay) {
        String query = "INSERT INTO timetable (groupid, weekday, firstlesson, lesson1, lesson2, lesson3, lesson4, lesson5, lesson6, lesson7)\n" +
                "  SELECT groupid, weekday, firstlesson, lesson1, lesson2, lesson3, lesson4, lesson5, lesson6, lesson7 FROM timetable\n" +
                "    WHERE iseven=true and weekday= ? and groupid = ?;\n" +
                "update timetable set iseven=false where iseven is null;";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, weekDay);
            pst.setInt(2, user.groupId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
