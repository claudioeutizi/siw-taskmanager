package it.uniroma3.siw.taskmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.taskmanager.model.Comment;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.CommentRepository;

@Service
public class CommentService {

	
	@Autowired
	private CommentRepository commentRepository;
	
	/***
	 * 
	 * Save a comment in DB 
	 * 
	 * @param comment
	 * @return Comment
	 */
	@Transactional
	public Comment saveComment(Comment comment) {
		return this.commentRepository.save(comment);
	}
	
	/***
	 * 
	 * Retrieve a comment from DB based on id 
	 * 
	 * @param id
	 * @return Comment
	 */
	@Transactional
	public Comment findById(Long id) {
		Optional<Comment> result = this.commentRepository.findById(id);
		return result.orElse(null);
	}
	

	
	/***
	 * 
	 * Retrieve all comments form DB based on task
	 * 
	 * @param task
	 * @return List of comments
	 */
	@Transactional
	public List<Comment> findCommentsByTask(Task task){
		return (List<Comment>)this.commentRepository.findByTask(task);
	}
	
	/***
	 * 
	 * Delete a specific comment from DB 
	 * 
	 * @param comment
	 */
	@Transactional
	public void deleteComment(Comment comment) {
		this.commentRepository.delete(comment);
	}
	
	/***
	 * 
	 * Delete a specific comment from DB based on id
	 * 
	 * @param comment
	 */
	@Transactional
	public void deleteCommentById(Long id) {
		this.commentRepository.deleteById(id);;
	}
	
}
