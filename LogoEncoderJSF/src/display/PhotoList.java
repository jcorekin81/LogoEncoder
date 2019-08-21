package display;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import dao.LogoEncoderCRUD;
import dao.Photos;

@ManagedBean
@RequestScoped
public class PhotoList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2141457345023605234L;
	private String baseDir = "/LogoEncoderJSF/images/";
	public static String username;
	private ArrayList<Photos> photos;
	private LogoEncoderCRUD crud = new LogoEncoderCRUD();
	

	public ArrayList<Photos> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<Photos> photos) {
		this.photos = photos;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public List<Link> links(){
		
		List<Photos> photos= crud.retrieveUserJobs(username);
		List<Link> links = new ArrayList<Link>();
		
		for(Photos photo : photos){
			Link link = new Link();
			link.setName(photo.getPhotoName());
			link.setUrl("http://71.178.49.139:8080/LogoEncoderJSF/pages/display.jsf?id="+photo.getPhotoName());
			links.add(link);
		}
		
		return links;
	}

}
