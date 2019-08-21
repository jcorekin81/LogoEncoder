package login;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

public interface UserLoginService {
    /**
     * Heavyweight method to get logged authentication.
     * Remember that this method may be touching the database, ergo it's heavy.
     * Use getLoggedUserDetails for fast and light access to logged authentication data.
     */
    User getLoggedUser();
 
    /**
     * Lightweight method to get currently logged authentication details
     */
    Authentication getLoggedUserDetails();
 
    /**
     * This method should be used only to login right after registration
     */
    boolean login(Long userId);
 
    boolean login(String login, String password);
    void logout();
    boolean isLoggedIn();
}