package it.uniroma3.siw.taskmanager.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.taskmanager.model.Comment;
import it.uniroma3.siw.taskmanager.model.Task;

public interface CommentRepository extends CrudRepository<Comment, Long> {

	
	/***
	 * 
	 * Retrieve all comments form DB based on task
	 * 
	 * @param task
	 * @return List of comments
	 */
	public List<Comment> findByTask(Task task);	
}
