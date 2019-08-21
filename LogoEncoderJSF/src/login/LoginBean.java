package login;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import display.PhotoList;
import display.Viewer;

@ManagedBean
@SessionScoped
public class LoginBean extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2005164706640083739L;


	@ManagedProperty("#{authenticationManager}")
	AuthenticationManager authenticationManager;


	private String username;
	private String password;
	private boolean loggedIn;
	private String id;

	public AuthenticationManager getAuthenticationService() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String login() {

		SavedRequest savedRequest;
		String id =null;
		String[] idArray = null;

		PhotoList.username=username;

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest)externalContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)externalContext.getResponse();
		HttpSession session = request.getSession(false);
		if(session != null) {
			System.out.println("Retrieving savedRequest");
			savedRequest =  new HttpSessionRequestCache().getRequest(request, response);
			if(savedRequest!=null){
				System.out.println("Accessing savedRequest");
				Map<String, String[]> map=savedRequest.getParameterMap();
				System.out.println("Retrieving param map");
				if(map!=null){
					System.out.println("Retrieving id array");
					idArray = map.get("id");
					if(idArray!=null){
						System.out.println("Retrieving id");
						id = idArray[0];
					}
				}
			}
		} 


		System.out.println("id is: "+id);

		try{
			Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			if(authenticate.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(authenticate);
				loggedIn=true;
				if(id!=null){
					FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("id", id);
					return "/pages/display";
				}
				else{
					return "/pages/uploader";
				}
			}
		}
		catch (AuthenticationException e) {
			System.out.println("Failed login");
		}
		return "login";
	}

	public void logout(){
		((HttpServletRequest)FacesUtils.getExternalContext().getRequest()).getSession(false).invalidate();
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}