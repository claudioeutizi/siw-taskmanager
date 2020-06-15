package it.uniroma3.siw.taskmanager.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
	
	/**
	 * Retrieve all Users that have visibility over the passed project
	 * @param project the Project to find the members of
	 * @return the List of Users that have visibility over the passed Project
	 */
	public List<User> findByVisibleProjects(Project project);
}
