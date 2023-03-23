package authorization;


public class CompositeAuthorizationModule implements AuthorizationModule {
    private AuthorizationModule rbac = null;
    private AuthorizationModule abac = null;
    private AuthorizationModule hyac = null;

    public CompositeAuthorizationModule(String databaseUsername, String databasePassword) {
        rbac = new RoleBasedAuthorizationModule(databaseUsername, databasePassword);
        abac = new AttributeBasedAuthorizationModule(databaseUsername, databasePassword);
        hyac = new HybridAuthorizationModule(databaseUsername, databasePassword);
    }

    @Override
    public boolean checkAccess(String subject, String action, String object) {
        if (rbac.checkAccess(subject, action, object)) return true;
        if (hyac.checkAccess(subject, action, object)) return true;
        if (abac.checkAccess(subject, action, object)) return true;
        return false;
    }
}
