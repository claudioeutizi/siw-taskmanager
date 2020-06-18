package it.uniroma3.siw.taskmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.TaskRepository;

/**
 * 
 * The taskRepository handles logic for Task
 *
 */

@Service
public class TaskService {

	@Autowired
	protected TaskRepository taskRepository;


	/**
	 * this method retrieves a Task from the db based on its id
	 * @param id the id of the Task to retrieve
	 * @return the retrieved task or null if there's no Task in the db based on the param id
	 */

	@Transactional
	public Task getTask(Long id) {
		Optional<Task> result = this.taskRepository.findById(id);
		return result.orElse(null);
	}



	/**
	 * this method saves a Task into the db
	 * @param task the Task to save into the DB
	 * @return the saved Task
	 */
	@Transactional
	public Task saveTask(Task task) { return this.taskRepository.save(task); }



	/**
	 * this method sets the Task completed and saves the updated Task in the db
	 * @param task the task to set completed
	 * @return the updated task
	 */
	@Transactional
	public Task setCompleted(Task task) {
		task.setCompleted(true);
		return this.taskRepository.save(task);
	}


	/**
	 * this method deletes a Task from the DB
	 * @param task the task to delete from the db
	 */
	@Transactional
	public void deleteTask(Task task) {
		this.taskRepository.delete(task);
	}

	@Transactional
	public List<Task> retrieveTasksAssignedTo(User user){
		return this.taskRepository.findByAssignedUser(user);
	}

	@Transactional
	public Task assignTaskToUser(Task task, User user) {
		task.setAssignedUser(user);
		return this.taskRepository.save(task);
	}

	@Transactional
	public void deleteTask(Long id) {
		this.taskRepository.deleteById(id);
	}


}
