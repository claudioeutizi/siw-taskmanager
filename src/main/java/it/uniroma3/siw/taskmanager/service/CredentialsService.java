package it.uniroma3.siw.taskmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.CredentialsRepository;

@Service
public class CredentialsService {


	@Autowired
	protected CredentialsRepository credentialsRepository;

	@Autowired
	protected PasswordEncoder passwordEncoder;
	/**
	 * this method retrieves a Credentials from the DB based on its id
	 * @param id the id of the Credentials to retrieve from the DB
	 * @return the retrieved Credentials, or null if no Credentials with the passed ID could be found
	 */
	@Transactional
	public Credentials getCredentials(long id) {
		Optional<Credentials> result = this.credentialsRepository.findById(id);
		return result.orElse(null);
	}

	/**
	 * this method retrieves a Credentials from the DB based on its id
	 * @param username the username of the Credentials to retrieve from the DB
	 * @return the retrieved Credentials, or null if no Credentials with the passed username could be found
	 */
	@Transactional
	public Credentials getCredentials(String username) {
		Optional<Credentials> result = this.credentialsRepository.findByUserName(username);
		return result.orElse(null);
	}

	/**
	 * This method saves a Credentials in the DB
	 * Before saving it, it sets the Credentials role to DEFAULT, and encrypts the password
	 * the starting role after the registration is DEFAULT, it could become ADMIN after a promotion
	 * @param credentials the credentials to save into the DB
	 * @return the saved Credentials
	 * @throws DataIntegrityViolationException if an Credentials with the same username 
	 * 											as the passed Credentials already exists in the DB
	 */
	@Transactional
	public Credentials saveCredentials(Credentials credentials) {
		credentials.setRole(Credentials.DEFAULT_ROLE);
		/* this also saves any user associated to the Credentials, thanks to CascadeType.All */
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		return this.credentialsRepository.save(credentials);
	}

	@Transactional
	public List<Credentials> getAllCredentials() {
		List<Credentials> result = new ArrayList<>();
		Iterable<Credentials> credentials = this.credentialsRepository.findAll();
		for(Credentials c : credentials)
			result.add(c);
		return result;
	}

	@Transactional
	public void deleteCredentials(String username) {
		this.credentialsRepository.delete(this.getCredentials(username));
	}

	@Transactional
	public User getUserByUserName(String username) {
		return this.getCredentials(username).getUser();
	}
}
