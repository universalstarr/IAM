package authorization;

public interface AuthorizationModule {
    public boolean checkAccess(String subject, String action, String object);
}
