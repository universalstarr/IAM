package authorization;

import java.sql.*;
import java.util.*;

public class AttributeBasedAuthorizationModule implements AuthorizationModule {
    private Connection con = null;

    public AttributeBasedAuthorizationModule(String databaseUsername, String databasePassword) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/iam", databaseUsername, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> getSubjectAttributes(String subject, Set<String> subjectAttrKeys) {
        Map<String, String> res = new HashMap<>();
        for (String key : subjectAttrKeys) {
            res.put(key, null);
        }
        res.put("subject.ID",subject);
        try {
            String sql = "SELECT AttrKey, AttrValue FROM subjectAttributes WHERE UserID= ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, subject);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String key = rs.getString(1);
                if (res.containsKey("subject." + key)) {
                    res.put("subject." + key, rs.getString(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private Map<String, String> getObjectAttributes(String object, Set<String> objectAttrKeys) {
        Map<String, String> res = new HashMap<>();
        for (String key : objectAttrKeys) {
            res.put(key, null);
        }
        res.put("object.ID",object);
        try {
            String sql = "SELECT AttrKey, AttrValue FROM objectAttributes WHERE ObjectID = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, object);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String key = rs.getString(1);
                if (res.containsKey("object." + key)) {
                    res.put("object." + key, rs.getString(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean checkAccess(String subject, String action, String object) {
        try {
            String sql = "SELECT Policy  FROM Authorization WHERE Action = ? AND Role IS NULL";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, action);
            ResultSet rs = p.executeQuery();
            List<List<String>> policies = new ArrayList<>();
//            <policy><rule> subject.name = object.name</rule></policy>
            while (rs.next()) {
                String policy = rs.getString(1);
                List<String> parsedPolicy = AbacUtils.parsePolicy(policy);
                if (!parsedPolicy.isEmpty()) {
                    policies.add(AbacUtils.parsePolicy(policy));
                }
            }
            rs.close();
            Set<String> subjectAttrKeys = AbacUtils.extractSubjectAttrKeys(policies);
            Map<String, String> subjectAttributes = getSubjectAttributes(subject, subjectAttrKeys);
            Set<String> objectAttrKeys = AbacUtils.extractObjectAttrKeys(policies);
            Map<String, String> objectAttributes = getObjectAttributes(object, objectAttrKeys);
            return AbacUtils.evaluatePolicies(policies, subjectAttributes, objectAttributes);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        AttributeBasedAuthorizationModule abac = new AttributeBasedAuthorizationModule("root", "123456");
        System.out.println(abac.checkAccess("user1", "read", "object1"));

    }
}
