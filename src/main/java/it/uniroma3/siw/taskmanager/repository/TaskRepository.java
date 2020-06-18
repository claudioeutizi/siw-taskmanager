package it.uniroma3.siw.taskmanager.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;

public interface TaskRepository extends CrudRepository<Task, Long> {
	
	public List<Task> findByAssignedUser(User assignedUser);

}
