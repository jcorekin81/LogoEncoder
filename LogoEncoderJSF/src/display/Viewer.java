package display;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import dao.LogoEncoderCRUD;
import dao.Photos;

@ManagedBean
@RequestScoped
public class Viewer {
	private String baseDir = "/images/";

	@ManagedProperty(value="#{param.id}")
	private String id;
	private String orig;
	private String enc;
	private String dec;
	private String logo;
	
	@ManagedProperty(value = "#{loginBean.username}")
	private String username;
	
	private LogoEncoderCRUD crud = new LogoEncoderCRUD();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrig() {
		return orig;
	}

	public void setOrig(String orig) {
		this.orig = orig;
	}

	public String getEnc() {
		return enc;
	}

	public void setEnc(String enc) {
		this.enc = enc;
	}

	public String getDec() {
		return dec;
	}

	public void setDec(String dec) {
		this.dec = dec;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String go(){
		SavedRequest savedRequest;
		String[] idArray = null;

		if(id==null){
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			HttpServletRequest request = (HttpServletRequest)externalContext.getRequest();
			HttpServletResponse response = (HttpServletResponse)externalContext.getResponse();
			HttpSession session = request.getSession(false);
			if(session != null) {
				savedRequest =  new HttpSessionRequestCache().getRequest(request, response);
				if(savedRequest!=null){
					Map<String, String[]> map=savedRequest.getParameterMap();
					if(map!=null){
						idArray = map.get("id");
						if(idArray!=null){
							id = idArray[0];
						}
					}
				}
			}
		}
		
		if(id==null){
			List<Photos> photos= crud.retrieveUserJobs(username);
			id=photos.get(photos.size()-1).getPhotoName();
		}

		baseDir=baseDir+username+"/";
		
		orig=baseDir+id+"_orig.jpg";
		enc=baseDir+id+"_encoded.jpg";
		dec=baseDir+id+"_decoded.jpg";
		logo=baseDir+id+"_logo.jpg";

		return null;

	}
}
