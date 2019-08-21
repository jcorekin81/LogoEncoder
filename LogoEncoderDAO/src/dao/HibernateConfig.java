package dao;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@Configuration
@EnableTransactionManagement
public class HibernateConfig extends WebMvcConfigurerAdapter  {

	@Bean
	public SessionFactory buildSessionFactory() {
		return new LocalSessionFactoryBuilder(datasource())
		.addAnnotatedClasses(Photos.class,Users.class)
		.buildSessionFactory();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new HibernateTransactionManager(buildSessionFactory());
	}

	//Enable accessing entityManager from view scripts. Required when using lazy loading 
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	 OpenSessionInViewInterceptor interceptor = new OpenSessionInViewInterceptor();
	 interceptor.setSessionFactory(buildSessionFactory());
	 registry.addWebRequestInterceptor(interceptor);
	}
	
	@Bean
	public DataSource datasource() {
		
		MysqlDataSource source = new MysqlDataSource();
		source.setUrl("jdbc:mysql://localhost:3306/logoencoder");
		source.setUser("root");
		source.setPassword("@WSxcDE#");
		 
		return source;
	}


}