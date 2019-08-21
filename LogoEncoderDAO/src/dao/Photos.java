package dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PHOTOS")
public class Photos {

	@Id
	@Column(name="PHOTOID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long photoID;
	private String username;
	private String photoName;
	private java.sql.Timestamp time;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPhotoName() {
		return photoName;
	}
	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}
	public Long getPhotoID() {
		return photoID;
	}
	public void setPhotoID(Long photoID) {
		this.photoID = photoID;
	}
	
	public java.sql.Timestamp getTime() {
		return time;
	}
	public void setTime(java.sql.Timestamp time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "Photos [photoID=" + photoID + ", username=" + username
				+ ", photoName=" + photoName + ", time=" + time + "]";
	}
	
	
	
}
