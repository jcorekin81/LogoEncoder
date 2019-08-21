package service;
import javax.ws.rs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
 
public class FileUploadForm {
	
	public FileUploadForm(){}
 
	@FormParam("uploadedFileOrig")
	@PartType("application/octet-stream")
	private byte[] dataOrig;
 
	@FormParam("uploadedFileLogo")
	@PartType("application/octet-stream")
	private byte[] dataLogo;
	
	@FormParam("name")
    @PartType("text/plain")
    private String name;
	
	@FormParam("maxChange")
    @PartType("text/plain")
    private String maxChange;
	
	@FormParam("encode")
    @PartType("text/plain")
    private String encode;
	
	@FormParam("username")
    @PartType("text/plain")
	private String username;
	
	@FormParam("password")
    @PartType("text/plain")
	private String password;
	
	public byte[] getDataOrig() {
		return dataOrig;
	}
 
	public void setDataOrig(byte[] dataOrig) {
		this.dataOrig = dataOrig;
	}

	public byte[] getDataLogo() {
		return dataLogo;
	}

	public void setDataLogo(byte[] dataLogo) {
		this.dataLogo = dataLogo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getEncode() {
		return Boolean.parseBoolean(encode);
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public int getMaxChange() {
		return Integer.parseInt(maxChange);
	}

	public void setMaxChange(String maxChange) {
		this.maxChange = maxChange;
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

    
    
}