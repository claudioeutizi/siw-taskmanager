package it.uniroma3.siw.taskmanager.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.taskmanager.model.Credentials;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

	/**
	 * Retrieve Credentials by its username
	 * @param username the username of the Credentials to retrieve
	 * @return an Optional for the Credentials with the passed username
	 */
	public Optional<Credentials> findByUserName(String userName);
}
