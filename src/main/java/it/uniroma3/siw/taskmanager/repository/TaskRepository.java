package it.uniroma3.siw.taskmanager.repository;


import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.taskmanager.model.Task;

public interface TaskRepository extends CrudRepository<Task, Long> {

}
