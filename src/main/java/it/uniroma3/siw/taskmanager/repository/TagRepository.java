package it.uniroma3.siw.taskmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.model.Task;

public interface TagRepository extends CrudRepository<Tag, Long>, JpaRepository<Tag, Long> {

	/***
	 * 
	 * Retrieve a tag from DB based on its name
	 * 
	 * @param name
	 * @return Tag
	 */
	public Optional<Tag> findByNameAndColor(String name, String color);
	
	@Query(value = "SELECT * FROM public.tag WHERE project_id = ?1", nativeQuery = true)
	  public List<Tag> findByProjectId(Long projectId);
	
	/**
	 * Ritorna un lista di tag dal DB in base al task
	 * @param task
	 * @return List di Tag
	 */
	public List<Tag> findByTasks(Task task);
	
	
}
