package it.uniroma3.siw.taskmanager.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.User;

public interface ProjectRepository extends CrudRepository<Project, Long> {
	
	
/**
 * Retrieve all Projects that are visible by the passed user
 * @param member the User to retrieve the visible projects of
 * @return the List of projects visible by the passed user
 */
	public List<Project> findByMembers(User member);
	
	/**
	 * Retrieve all Projects that are owned by the passed user
	 * @param owner the owner to retrieve the owned Projects of
	 * @return the List of projects owned by the passed user
	 */
	
	public List<Project> findByOwner(User owner);
}
