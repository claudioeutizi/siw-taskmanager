package it.uniroma3.siw.taskmanager.authentication;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.uniroma3.siw.taskmanager.model.Credentials;

@Configuration
@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter {

	/**
	 * The datasource is automatically injected into the AuthConfiguration (using its getters and setters)
	 * and is used to access the DB to get the Credentials to perform authentication and authorization
	 */
	
	@Autowired
	DataSource datasource; //DB access point
	
	
	/**
	 * this method provides the SQL queries to get username and password
	 * NOTE:field denoted in Java by camelCase convention
	 * 		are denoted in Postgres by snake_case convention by default
	 * 		(e.g. "userName" field in Java class results in "user_name" DB column)
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception { //where to find username & password to compare with the user's ones
		auth.jdbcAuthentication()
			//use the autowired datasource to access the saved credentials
			.dataSource(this.datasource)
			//retrieve username and role
			.authoritiesByUsernameQuery("SELECT user_name, role FROM credentials WHERE user_name=?") //?:query parameter
			//retrieve username, password and boolean flag specifying whether the user is enabled or not (always enabled)
			.usersByUsernameQuery("SELECT user_name, password, 1 as enabled FROM credentials WHERE user_name=?");
	}
	
	/**
	 * this method's structure: concatenate invocation series to the http object
	 * http's methods generally return the same updated http object in order to concatenate the calls.
	 * this method provides the whole autentication and authorization configuration to use
	 */
	
	@Override
	protected void configure(HttpSecurity http) throws Exception { 
		http			
			//authorization paragraph: here we define WHO can access WHICH pages
			.authorizeRequests()
			//anyone (authenticated or not) can access the welcome page, the login page, and the registration page
			.antMatchers(HttpMethod.GET, "/", "/index", "/login", "/user/register").permitAll()
			//anyone (authenticated or not) can send POST requests to the login endpoint and the register endpoint
			.antMatchers(HttpMethod.POST, "/login", "/user/register").permitAll()
			// only authenticated users with ADMIN authority can access the admin page
			.antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(Credentials.ADMIN_ROLE)
			.antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(Credentials.ADMIN_ROLE)
			// all authenticated users can access all the remaining other pages
			.anyRequest().authenticated()
			
			//login paragraph: here we define how to login
			//use formLogin protocol to perform login
			.and().formLogin()
			//after login is successful, redirecct to the logged user homepage
			.defaultSuccessUrl("/home")
			
			//logout paragraph: we are going to define here how to logout
			.and().logout()
			.logoutUrl("/logout") 			//logout is performed when sending a GET to "/logout"
			.logoutSuccessUrl("/index")		//after logout is successful, redirect to /index page
			.invalidateHttpSession(true)
			.clearAuthentication(true).permitAll(); //stop session when logout is done
	}
	
	@Bean //a @Bean annotated object remains in the application context (@Component is a particular type of @Bean)
	PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
	
	
	
	
}
