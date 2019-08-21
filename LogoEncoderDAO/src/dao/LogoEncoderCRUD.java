package dao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


import dao.HibernateConfig;

@SuppressWarnings("unused")
@Transactional
public class LogoEncoderCRUD {
	
	private HibernateConfig hbf = new HibernateConfig();
	private SessionFactory sessionFactory = hbf.buildSessionFactory();

	public List<Photos> retrieveUserJobs(String username) {
		Session session = sessionFactory.openSession(); // not part of a transaction, so we need to open a session manually
		Query query = session.createQuery("from Photos photo where photo.username=:username").setString("username", username);
		@SuppressWarnings("unchecked")
		List<Photos> photos = query.list();
		session.close();
		
		return photos;
	}
	
	@Transactional
	public void submitCurrJob(String username,String photoName) {
		Session session = sessionFactory.openSession();
		java.sql.Timestamp time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		Photos photo = new Photos();
		photo.setUsername(username);
		photo.setPhotoName(photoName);
		photo.setTime(time);
		session.save(photo);
		session.close();
	}
	
	public String retrievePassword(String username){
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Users user where user.username=:username").setString("username", username);
		Users user= null;
		try{
		user= (Users) query.uniqueResult();
		}
		catch(Exception e){
			return null;
		}
		return user.getPassword();
	}
}
