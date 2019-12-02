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

public class SQLCommands {
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

    public static UserInfo GetUserInfo(int userId) {
        String query = "select * from users where id = ?;";
        UserInfo user = new UserInfo(userId, UserStatus.NOT_EXISTS, "");

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                user.status = UserStatus.valueOf(rs.getString(3));
                user.groupId = rs.getInt(2);
            }
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return user;
    }

    public static void AddUserToSQL(UserInfo user) {
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

    public static void UpdateUserStatus(UserInfo user) {
        String query = "UPDATE users SET status = ? WHERE id = ?";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, user.status.toString());
            pst.setInt(2, user.userId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static String GetGroupName(int groupId) {
        String query = "select * from groups where groupid = ?";
        String result = "null";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, groupId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                result = rs.getString(3);
            }
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return result;
    }

    public static void UpdateUserGroup(UserInfo user) {
        String query = "UPDATE users SET groupId = ? WHERE id = ?";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.groupId);
            pst.setInt(2, user.userId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            SQLLogger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static ArrayList<String> GetLessonList(UserInfo user, String dayOfWeek) {
        ArrayList<String> result = new ArrayList<>();
        String query = "select * from timetable where groupid = ? and weekday = ?";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.groupId);
            pst.setString(2, dayOfWeek);
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
}
