package authorization;

import java.sql.*;

public class RoleBasedAuthorizationModule implements AuthorizationModule {
    private Connection con = null;

    public RoleBasedAuthorizationModule(String databaseUsername, String databasePassword) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/iam", databaseUsername, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkAccess(String subject, String action, String object) {
        try {
            String sql = "SELECT Roleassignment.role FROM Authorization JOIN Roleassignment ON " +
                    "Authorization.Role = Roleassignment.Role " +
                    "WHERE Policy IS NULL AND UserName = ? AND Action = ? " +
                    "LIMIT 1";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, subject);
            p.setString(2, action);
            ResultSet rs = p.executeQuery();
            boolean hasAccess = false;
            while (rs.next()) {
                hasAccess = true;
            }
            rs.close();
            return hasAccess;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        RoleBasedAuthorizationModule rbac = new RoleBasedAuthorizationModule("root", "123456");
        System.out.println( rbac.checkAccess("user1","read","11111"));
    }
}
