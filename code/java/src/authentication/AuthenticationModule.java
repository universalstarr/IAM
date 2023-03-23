package authentication;

public interface AuthenticationModule {
    public boolean createAccount(String username, String password);
    public boolean login(String username, String password);
    //user update password
    public boolean updatePassword(String username, String originalPassword,String newPassword);
}
