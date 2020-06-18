package it.uniroma3.siw.taskmanager.service;

import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.ProjectRepository;

/**
 * 
 * The ProjectService handles logic for Project
 *
 */

@Service
public class ProjectService {

	@Autowired
	protected ProjectRepository projectRepository;

	/**
	 * retrieves a Project from the DB based on its id
	 * @param id the id of the Project to retrieve from the db
	 * @return the retrieved Project, or null if there's no Project in the db with the param id
	 */
	@Transactional
	public Project getProject(Long id) {
		Optional<Project> result = this.projectRepository.findById(id);
		return result.orElse(null);
	}


	/**
	 * this method saves a Project in the db
	 * @param project the Project to save into the db
	 * @return the saved Project
	 */
	@Transactional
	public Project saveProject(Project project) {
		return this.projectRepository.save(project);
	}

	/**
	 * this method delete a Project in the db
	 * @param project the Project to save into the db
	 */
	@Transactional
	public void deleteProject(Project project) {
		this.projectRepository.delete(project);
	}

	@Transactional
	public void deleteProject(Long id) {
		this.projectRepository.delete(this.getProject(id));
	}	

	@Transactional
	public Project shareProjectWithUser(Project project, User user) {
		project.addMember(user);
		return this.projectRepository.save(project);
	}

	@Transactional
	public List<Project> retrieveProjectsOwnedBy(User user) {
		return projectRepository.findByOwner(user);
	}

	@Transactional
	public List<Project> retrieveProjectsSharedWith(User user){
		return projectRepository.findByMembers(user);
	}
}
