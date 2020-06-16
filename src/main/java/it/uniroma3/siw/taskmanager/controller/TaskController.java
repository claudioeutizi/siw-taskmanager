package it.uniroma3.siw.taskmanager.controller;

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
import it.uniroma3.siw.taskmanager.controller.validation.TaskValidator;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
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
		
		return "redirect:/projects"+projectId.toString();
	}
}
