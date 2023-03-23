package authentication;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class BcryptAuthenticationModule implements AuthenticationModule {
    private Connection con = null;
    private static final int COST = 13;

    public BcryptAuthenticationModule(String databaseUsername, String databasePassword) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/iam", databaseUsername, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean createAccount(String username, String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(COST));
        try {
            String sql = "INSERT INTO Authentication VALUES (?, ?)";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);
            p.setString(2, hashed);
            p.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean login(String username, String password) {
        try {
            String sql = "SELECT Password FROM Authentication WHERE UserName = ?";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            String hashedPassword = null;
            while (rs.next()) {
                hashedPassword = rs.getString(1);
            }
            rs.close();
            if (hashedPassword != null && (BCrypt.checkpw(password, hashedPassword))) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    //user update password
    @Override
    public boolean updatePassword(String username, String originalPassword, String newPassword) {
        try {
            String sql = "SELECT Password FROM Authentication WHERE UserName = ?";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            String hashedPassword = null;
            while (rs.next()) {
                hashedPassword = rs.getString(1);
            }
            rs.close();
            // if hashedPassword==null means user does not exist before.
            if (hashedPassword == null || (!BCrypt.checkpw(originalPassword, hashedPassword))) {
                return false;
            } else {
                String sql2 = "UPDATE Authentication SET Password = ? WHERE UserName = ?";
                PreparedStatement p2 = con.prepareStatement(sql2);
                p2.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt(COST)));
                p2.setString(2, username);
                p2.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        BcryptAuthenticationModule b = new BcryptAuthenticationModule("root", "123456");
        b.createAccount("manager1", "m1");
        b.createAccount("doctor1", "d1");
        b.createAccount("doctor2", "d2");
        b.createAccount("patient1", "p1");
        b.createAccount("patient2", "p2");
//        System.out.println(b.login("user4", "00000"));
//        System.out.println(b.login("user3", "00000"));
//        System.out.println(b.login("user4", "11111"));
//        System.out.println(b.updatePassword("user4", "11111", "22222"));
//        System.out.println(b.login("user4", "22222"));
//        System.out.println(b.updatePassword("user3", "11111", "22222"));
//        System.out.println(b.updatePassword("user4", "00000", "22222"));
//        System.out.println(b.updatePassword("user4", "22222", "11111"));
    }

}
