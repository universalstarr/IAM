package authentication;

import java.sql.*;

public class ClearTextAuthenticationModule implements AuthenticationModule {
    private Connection con = null;
    private static final int COST = 13;

    public ClearTextAuthenticationModule(String databaseUsername, String databasePassword) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/iam", databaseUsername, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean createAccount(String username, String password) {
        try {
            String sql = "INSERT INTO ClearTextAuthentication VALUES (?, ?)";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);
            p.setString(2, password);
            p.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean login(String username, String password) {
        try {
            String sql = "SELECT Password FROM ClearTextAuthentication WHERE UserName = ?";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            String originalPassword = null;
            while (rs.next()) {
                originalPassword = rs.getString(1);
            }
            rs.close();
            if (originalPassword != null && (password.equals(originalPassword))) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean updatePassword(String username, String originalPassword, String newPassword) {
        try {
            String sql = "SELECT Password FROM ClearTextAuthentication WHERE UserName = ?";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            String storedPassword = null;
            while (rs.next()) {
                storedPassword = rs.getString(1);
            }
            rs.close();
            // if hashedPassword==null means user does not exist before.
            if (storedPassword == null || (!originalPassword.equals(storedPassword))) {
                return false;
            } else {
                String sql2 = "UPDATE ClearTextAuthentication SET Password = ? WHERE UserName = ?";
                PreparedStatement p2 = con.prepareStatement(sql2);
                p2.setString(1, newPassword);
                p2.setString(2, username);
                p2.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        ClearTextAuthenticationModule b = new ClearTextAuthenticationModule("root", "123456");
        b.createAccount("user4", "11111");
        b.createAccount("user1", "11111");
        b.createAccount("user2", "11111");
        System.out.println(b.login("user4", "00000"));
        System.out.println(b.login("user3", "00000"));
        System.out.println(b.login("user4", "11111"));
        System.out.println(b.updatePassword("user4", "11111", "22222"));
        System.out.println(b.login("user4", "22222"));
        System.out.println(b.updatePassword("user3", "11111", "22222"));
        System.out.println(b.updatePassword("user4", "00000", "22222"));
        System.out.println(b.updatePassword("user4", "22222", "11111"));
    }
}
