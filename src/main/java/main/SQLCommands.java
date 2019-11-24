package main;

import org.telegram.telegrambots.api.objects.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLCommands {
    public static Connection GetSQLConnection() throws java.sql.SQLException {
        Logger log = Logger.getLogger(Bot.class.getName());

        String url = "";
        String user = "";
        String password = "";

        try
        {
            Scanner sc = new Scanner(new File("C:\\Users\\user\\IdeaProjects\\Markbook-Bot\\src\\main\\java\\main\\sql_config"));
            String [] splitted;
            while(sc.hasNext()) {
                splitted = sc.nextLine().split(" ");
                url = splitted[0];
                user = splitted[1];
                password = splitted[2];
            }
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }

        return DriverManager.getConnection(url, user, password);
    }

    public static UserInfo GetUserInfo(int userId) {
        String query = "select * from users where id = ?;";
        UserInfo user = new UserInfo(userId, "not_exists", "");

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                user.status = rs.getString(3);
                user.groupId = rs.getInt(2);
            }
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(Main.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return user;
    }

    public static void AddUserToSQL(UserInfo user) {
        String query = "INSERT INTO users(id, status) VALUES(?, ?)";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, user.userId);
            pst.setString(2, user.status);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Main.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static void UpdateUserStatus(UserInfo user) {
        String query = "UPDATE users SET status = ? WHERE id = ?";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, user.status);
            pst.setInt(2, user.userId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Main.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static String GetGroupName(int groupId) {
        String query = "select * from groups where groupid = ?";
        String result = "null";

        try (Connection con = SQLCommands.GetSQLConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, groupId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                result = rs.getString(3);
            }
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(Main.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
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
            Logger lgr = Logger.getLogger(Main.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
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

            Logger lgr = Logger.getLogger(Main.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }
}
