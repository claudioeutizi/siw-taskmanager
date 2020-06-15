package it.uniroma3.siw.taskmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.UserRepository;

/**
 * 
 * The UserService handles logic for Users
 *
 */
@Service 

/* rende la classe un componente della nostra app spring boot, sarà spring boot a darci la sua sola istanza
 * diventerà un oggetto singleton della nostra applicazione. Non dobbiamo preoccuparci di istanziarlo noi
 */

public class UserService {

	@Autowired //gestisce lo spring boot la creazione e istanziazione di questa entità
	protected UserRepository userRepository;

	/**
	 * retrieves a User from the db based on its ID
	 * @param id the user's id needed to retrieve the user from the db
	 * 
	 * @return the retrieved User based on the id or null if there's no User in the DB with the param id
	 */

	@Transactional //ci consente di evitare la scrittura di transazioni(begin, commit...)
	//PATTERN REPOSITORY: Le transazioni non vanno gestite dal repository ma dalla classe che chiama quei repository
	public User getUser(long id) {
		Optional<User> result = this.userRepository.findById(id);
		return result.orElse(null);
	}



	/**
	 * retrieves a user from the db based on its username
	 * @param username the username of the User to retrieve from the db 
	 * @return the retrieved User or null if there's no User in the db with the param username
	 */


	/**
	 * this method saves a User into the DB
	 * @param user the User to save into the DB
	 * @return the saved User
	 * @throws DataIntegrityViolationException if a user with the same username 
	 * 										as the passed User already exists in the db
	 */

	@Transactional
	public User saveUser(User user) { return this.userRepository.save(user); }



	/**
	 * this method retrieves all the Users from the DB
	 * @return a List with all retrieved Users
	 */

	@Transactional
	public List<User> getAllUsers(){
		List<User> result = new ArrayList<>();
		Iterable<User> iterable = this.userRepository.findAll(); //ci restituisce una lista di iterable ma noi vogliamo
		// una lista di User
		for(User user : iterable) result.add(user);
		return result;
	}



	public List<User> getMembers(Project project) {
		return project.getMembers();
	}
}
