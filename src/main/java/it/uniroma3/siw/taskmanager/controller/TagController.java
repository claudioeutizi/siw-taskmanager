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
import it.uniroma3.siw.taskmanager.controller.validation.TagValidator;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TaskService;

@Controller
public class TagController {

	@Autowired
	private TagValidator tagValidator;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private SessionData sessionData;

	@Autowired
	private TaskService taskService;

	@RequestMapping(value ="/projects/{projectId}/tags/add", method = RequestMethod.GET)
	public String addTagForm(@PathVariable("projectId") Long projectId, Model model) {
		model.addAttribute("projectId", this.projectService.getProject(projectId));
		model.addAttribute("tagForm",new Tag());
		return "addTag";
	}

	@RequestMapping(value = "/projects/{projectId}/tags/add", method = RequestMethod.POST)
	public String addTag(@PathVariable("projectId") Long projectId, 
			@Valid @ModelAttribute("tagForm") Tag tag, 
			BindingResult tagBindingResult, Model model) {

		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);
		//only the project's owner can add a tag to the project
		if(project.getOwner().equals(loggedUser)) {
			this.tagValidator.validate(tag,tagBindingResult);
			if(!tagBindingResult.hasErrors()) {
				project.addTag(tag);
				this.projectService.saveProject(project);
				return "redirect:/projects/{projectId}";
			}
			model.addAttribute("loggedUser", loggedUser);
			model.addAttribute("project",project);
			return "/projects/{projectId}/tags/add";
		}
		model.addAttribute("loggedUser", loggedUser);
		return "/projects/{projectId}";
	}


	@RequestMapping(value="/projects/{projectId}/tasks/{taskId}/tags/add", method = RequestMethod.GET)
	public String addTagToATaskForm(@PathVariable("projectId") Long projectId, 
			@PathVariable("taskId") Long taskId,
			@ModelAttribute("tagForm") Tag tag, 
			Model model) {
		User loggedUser = sessionData.getLoggedUser();
		model.addAttribute("loggedUser", loggedUser);
		Project project = this.projectService.getProject(projectId);
		model.addAttribute("project", project);
		model.addAttribute("task", this.taskService.getTask(taskId));
		if(loggedUser.equals(project.getOwner())) { 
			model.addAttribute("tagForm", tag);
			return "addTagToATask";
		}
		else return "redirect:/projects/{projectId}/tasks/{taskId}";
	}
	@RequestMapping(value="/projects/{projectId}/tasks/{taskId}/tags/add", method = RequestMethod.POST)
	public String addTagToATask(@PathVariable("projectId") Long projectId, 
			@PathVariable("taskId") Long taskId,
			@ModelAttribute("tag") Tag tag, 
			Model model) {
		User loggedUser = sessionData.getLoggedUser();
		Project project = projectService.getProject(projectId);
		Task task = taskService.getTask(taskId);
		if(project.getOwner().equals(loggedUser)) {
			task.addTag(tag);
			this.taskService.saveTask(task);
			model.addAttribute("task", task);
			model.addAttribute("project", project);
			return "redirect:/projects/{projectId}/tasks/{taskId}";
		}
		model.addAttribute("task", task);
		model.addAttribute("project", project);
		return "redirect:/projects/{projectId}/tasks/{taskId}";
	}
}
