package it.uniroma3.siw.taskmanager;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.ProjectRepository;
import it.uniroma3.siw.taskmanager.repository.TaskRepository;
import it.uniroma3.siw.taskmanager.repository.UserRepository;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
//import it.uniroma3.siw.taskmanager.service.TaskService;
import it.uniroma3.siw.taskmanager.service.UserService;

@SpringBootTest
@RunWith(SpringRunner.class)
class TaskmanagerApplicationTests {

	@Autowired
	private UserService userService;
	
//	@Autowired
//	private TaskService taskService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private CredentialsService credentialsService;
	
	
	@Before
	public void deleteAll() {
		System.out.println("Deleting all data in DB...");
		userRepository.deleteAll();
		taskRepository.deleteAll();
		projectRepository.deleteAll();
		System.out.println("Done");
	}
	
	@Test
	void testUpdateCredentialsAndUser() {
		//save first user and its credentials in the DB
		Credentials credentials1 = new Credentials("mariorossi", "password");
		User user1 = new User("Mario", "Rossi");
		credentials1.setUser(user1); //thanks to Cascade.ALL
		credentials1 = credentialsService.saveCredentials(credentials1);
		assertEquals(credentials1.getUserName(), "mariorossi");
		user1 = credentials1.getUser();
		assertEquals(user1.getFirstName(), "Mario");
		assertEquals(user1.getLastName(), "Rossi");
		
		//update first user in the DB
		User user1updated = new User("Maria", "Rossi");
		Credentials credentials1updated = new Credentials("mariarossi", "password");
		credentials1updated.setId(credentials1.getId()); //overwriting 
		credentials1updated.setUser(user1updated);
		user1updated.setId(user1.getId()); //setto l'id di mario rossi
		credentials1updated = credentialsService.saveCredentials(credentials1updated);
		credentials1updated = credentialsService.getCredentials(credentials1.getId());
		assertEquals(credentials1updated.getId().longValue(), 1L);
		assertEquals(credentials1updated.getUserName(), "mariarossi");
		assertEquals(user1updated.getId().longValue(), credentials1updated.getUser().getId().longValue());
		assertEquals(user1updated.getFirstName(), "Maria");
		assertEquals(user1updated.getLastName(), "Rossi");
		
		//save second user in the DB
		User user2 = new User("Luca", "Bianchi");
		user2 = userService.saveUser(user2);
		assertEquals(user2.getFirstName(), "Luca");
		assertEquals(user2.getLastName(), "Bianchi");
		
		//save first project in the DB
		Project project1 = new Project("TestProject1", "Just a little test Project");
		project1.setOwner(user1);
		project1 = projectService.saveProject(project1);
		assertEquals(project1.getOwner(), user1);
		assertEquals(project1.getName(), "TestProject1");
		assertEquals(project1.getDescription(), "Just a little test Project");
		
		//save second project in the DB
		Project project2 = new Project("TestProject2", "Just another little test Project");
		project2.setOwner(user1);
		project2 = projectService.saveProject(project2);
		assertEquals(project2.getOwner(), user1);
		assertEquals(project2.getName(), "TestProject2");
		assertEquals(project2.getDescription(), "Just another little test Project");
		
		//give visibility over project 1 to user2
		project1 = projectService.shareProjectWithUser(project1, user2);
		
		//test projects owned by user 1
		List<Project> projects = projectRepository.findByOwner(user1);
		assertEquals(projects.size(), 2);
		assertEquals(projects.get(0), project1);
		assertEquals(projects.get(1), project2);
		
		//test projects visible by user 2
		List<User> project1members = userRepository.findByVisibleProjects(project1); //trova gli utenti che sono membri di project1
		assertEquals(project1members.size(), 1);
		assertEquals(project1members.get(0), user2);
		
		List<Project> projectsVisibleByUser2 = projectRepository.findByMembers(user2); //trova i projects di cui user2 è membro		
		assertEquals(projectsVisibleByUser2.size(), 1);
		assertEquals(projectsVisibleByUser2.get(0), project1);
	}

}
