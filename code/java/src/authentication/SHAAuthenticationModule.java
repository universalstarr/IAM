package authentication;

import tools.Md5ShaDemo;

import java.sql.*;

public class SHAAuthenticationModule implements AuthenticationModule {
    private Connection con = null;
    private static final int COST = 13;

    public SHAAuthenticationModule(String databaseUsername, String databasePassword) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/iam", databaseUsername, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean createAccount(String username, String password) {
        String hashed = Md5ShaDemo.getSHA256(password);
        try {
            String sql = "INSERT INTO SHAAuthentication VALUES (?, ?)";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);
            p.setString(2, hashed);
            p.executeUpdate();

        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean login(String username, String password) {
        try {
            String sql = "SELECT Password FROM SHAAuthentication WHERE UserName = ?";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            String hashedPassword = null;
            while (rs.next()) {
                hashedPassword = rs.getString(1);
            }
            rs.close();
            String ps = Md5ShaDemo.getSHA256(password);
            return (hashedPassword != null && (ps.equals(hashedPassword)));

        } catch (SQLException e) {
            return false;
        }
    }

    //user update password
    public boolean updatePassword(String username, String originalPassword, String newPassword) {
        try {
            String sql = "SELECT Password FROM SHAAuthentication WHERE UserName = ?";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            String hashedPassword = null;
            while (rs.next()) {
                hashedPassword = rs.getString(1);
            }
            rs.close();
            // if hashedPassword==null means user does not exist before.
            String ps = Md5ShaDemo.getSHA256(originalPassword);
            if (hashedPassword == null || (!ps.equals(hashedPassword))) {
                return false;
            } else {
                String sql2 = "UPDATE SHAAuthentication SET Password = ? WHERE UserName = ?";
                PreparedStatement p2 = con.prepareStatement(sql2);
                p2.setString(1, Md5ShaDemo.getSHA256(newPassword));
                p2.setString(2, username);
                p2.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        SHAAuthenticationModule b = new SHAAuthenticationModule("root", "123456");
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
