package it.uniroma3.siw.taskmanager.controller;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.CredentialsValidator;
import it.uniroma3.siw.taskmanager.controller.validation.TaskValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TaskService;

@Controller
public class TaskController {

	@Autowired
	TaskService taskService;

	@Autowired
	ProjectService projectService;

	@Autowired
	SessionData sessionData;

	@Autowired
	TaskValidator taskValidator;

	@Autowired
	CredentialsValidator credentialsValidator;

	@Autowired
	CredentialsService credentialsService;

	/**
	 * this method is called when a GET request is sent by the user to URL "/tasks/add
	 * this method prepares and dispatches a view containing the form to add a new Task
	 * @param model the Request model
	 * @return the name of the target view, that in this case is "addTask"
	 */
	@RequestMapping(value = {"/projects/{projectId}/tasks/add"}, method = RequestMethod.GET)
	public String createTaskForm(Model model, @PathVariable("projectId") Long projectId) {
		User loggedUser = sessionData.getLoggedUser();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("project", projectService.getProject(projectId));
		model.addAttribute("taskForm", new Task());
		return "addTask";
	}

	@RequestMapping(value = {"/projects/{projectId}/tasks/add"}, method = RequestMethod.POST)
	public String createTask(@Valid @ModelAttribute("taskForm") Task task,
			@PathVariable("projectId") Long projectId,
			BindingResult taskBindingResult,
			Model model) {
		User loggedUser = sessionData.getLoggedUser();
		Project project = this.projectService.getProject(projectId);
		taskValidator.validate(task, taskBindingResult);
		if(project != null) {
			if(!taskBindingResult.hasErrors()) {
				this.taskService.saveTask(task);
				project.addTask(task);
				projectService.saveProject(project);
				return "redirect:/projects/" + projectId.toString();
			}
			model.addAttribute("loggedUser", loggedUser);
			model.addAttribute("project", project);
			return "addTask";
		}

		return "redirect:/projects/"+projectId.toString();
	}

	/**
	 * this method is called when a GET request is sent by the user to URL "/assignedTasksToMe"
	 * this method retrieve the view of the Tasks assigned to the loggedUser
	 * @param model the Request model
	 * @return the name of the target view, that in this case is "assignedTasksToMe"
	 */

	@RequestMapping(value = {"/assignedTasksToMe"}, method = RequestMethod.GET)
	public String assignedTasksToMe(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Task> assignedTasksList = taskService.retrieveTasksAssignedTo(loggedUser);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("assignedTasksList", assignedTasksList);
		return "assignedTasksToMe";
	}

	@RequestMapping(value = {"/{projectId}/tasks/{taskId}"}, method = RequestMethod.GET)
	public String task(Model model , @PathVariable("projectId") Long projectId,
			@PathVariable("taskId") Long taskId) {//the variable part of the URL 

		User loggedUser = sessionData.getLoggedUser();
		//if no project with the passed ID exists
		//redirect to the view with the list of my projects
		Project project = projectService.getProject(projectId);
		Task task = taskService.getTask(taskId);
		if(project == null) return "redirect:/projects";
		if(task == null) return "redirect:/projects/{projectId}";

		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("project", project);
		model.addAttribute("task", task);
		model.addAttribute("credentialsForm", new Credentials());

		return "task";
	}

	/**
	 * This method is called when a POST request is sent by the user to url "/home/projects/{projectId}/delete
	 * This method deletes the user whose credentials are identified by the passed id {projectId}
	 * @param model the request model
	 * @param projectId the id of the Project to delete
	 * @return the target view, which in this case is "/projects/{projectId}"
	 */
	@RequestMapping(value = {"/{projectId}/tasks/{taskId}/delete"}, method=RequestMethod.POST)
	public String removeTask(Model model, @PathVariable("projectId") Long projectId,
			@PathVariable("taskId") Long taskId) {
		this.taskService.deleteTask(taskId);
		return "redirect:/projects/" + projectId.toString(); //with this redirect the method myOwnedProjects will be recalled, 
		//in order to have the projectsList in the model
	}

	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/update"}, method = RequestMethod.GET)
	public String updateTaskForm(Model model, 
			@PathVariable("taskId") Long taskId,
			@PathVariable("projectId") Long ProjectId) {

		User user = sessionData.getLoggedUser();
		model.addAttribute("loggedUser", user);
		model.addAttribute("project", this.projectService.getProject(ProjectId));
		model.addAttribute("task", this.taskService.getTask(taskId));
		model.addAttribute("taskForm", new Task());
		return "updateTask";
	}


	@RequestMapping(value = {"/projects/{projectId}/tasks/{taskId}/update"}, method = RequestMethod.POST)
	public String updateTask(Model model,
			@Valid @ModelAttribute("taskForm") Task taskForm,
			BindingResult taskBindingResult,
			@PathVariable("taskId") Long taskId,
			@PathVariable("projectId") Long projectId) {

		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);
		taskValidator.validate(taskForm, taskBindingResult);
		if(taskForm != null) {
			if(!taskBindingResult.hasErrors()) {
				taskForm.setId(taskId);
				this.taskService.saveTask(taskForm);
				model.addAttribute("task", taskForm);
				return "redirect:/projects/"+projectId.toString();
			}
			model.addAttribute("loggedUser", loggedUser);
			model.addAttribute("project", project);
			model.addAttribute("task", this.taskService.getTask(taskId));
			return "updateTask";
		}
		return "redirect:/projects/"+projectId.toString();
	}

	@RequestMapping(value={ "/projects/{projectId}/tasks/{taskId}/assign" }, method = RequestMethod.POST) 
	public String assignTask(@Valid @ModelAttribute("credentialsForm") Credentials assignCredentialsForm, 
			BindingResult assignCredentialsFormBindingResult,
			@PathVariable Long projectId, @PathVariable Long taskId, 
			Model model) {
		Credentials loggedCredentials = sessionData.getLoggedCredentials();
		Project project = this.projectService.getProject(projectId);
		Task task = this.taskService.getTask(taskId);

		//Only project owner can assign a task to a project member
		if(project.getOwner().equals(loggedCredentials.getUser())){
			this.credentialsValidator.validateAssignment(assignCredentialsForm, project, assignCredentialsFormBindingResult);

			if(!assignCredentialsFormBindingResult.hasErrors()) {
				//retrieve the user by its username and set it into the Credentials form object
				User user2Assign2Task = credentialsService.getUserByUserName(assignCredentialsForm.getUserName());
				assignCredentialsForm.setUser(user2Assign2Task);
				this.taskService.assignTaskToUser(task, user2Assign2Task);
				return "redirect:/{projectId}/tasks/"+taskId.toString();
			}
		}
		model.addAttribute("project", project);
		model.addAttribute("task", task);
		model.addAttribute("loggedCredentials", loggedCredentials);
		model.addAttribute("shareCredentialsForm", assignCredentialsForm);
		return "redirect:/{projectId}/tasks/"+taskId.toString();

	}
}
