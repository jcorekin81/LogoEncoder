package login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {

	//@Resource(name = "authenticationService")
	//AuthenticationManager authenticationManager;

	@Override
	@Autowired
	public boolean login(String username, String password) {
		/*try{
			Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			if(authenticate.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(authenticate);
				return true;
			}
		}
		catch (AuthenticationException e) {
			System.out.println("Failed login");
		}
		return false;*/
		return true;
	}
}