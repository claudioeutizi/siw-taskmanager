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
import it.uniroma3.siw.taskmanager.controller.validation.ProjectValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.UserService;

@Controller
public class ProjectController {

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	CredentialsService credentialsService;

	@Autowired
	ProjectValidator projectValidator;

	@Autowired
	CredentialsValidator credentialsValidator;

	@Autowired
	SessionData sessionData;

	@RequestMapping(value = {"/projects"}, method = RequestMethod.GET)
	public String myOwnedProjects(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> ownedProjectsList = projectService.retrieveProjectsOwnedBy(loggedUser);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("ownedProjectsList", ownedProjectsList);
		return "myOwnedProjects";
	}

	/**
	 * this method is called when a GET request is sent by the user to URL "/sharedProjectsWithMe"
	 * this method retrieve the view of the Projects shared with the loggedUser
	 * @param model the Request model
	 * @return the name of the target view, that in this case is "sharedProjectWithMe"
	 */

	@RequestMapping(value = {"/sharedProjectsWithMe"}, method = RequestMethod.GET)
	public String sharedProjectsWithMe(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> sharedProjectsList = projectService.retrieveProjectsSharedWith(loggedUser);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("sharedProjectsList", sharedProjectsList);
		return "sharedProjectsWithMe";
	}
	/**
	 * this method is called when a GET request is sent by the user to URL "/projects/{projectId} with parametric projectId
	 * this method retrieve the view of the Project identified by projectId
	 * @param model the Request model and the projectId of the Project
	 * @return the name of the target view, that in this case is "project"
	 */
	@RequestMapping(value = {"/projects/{projectId}"}, method = RequestMethod.GET)
	public String project(Model model , @PathVariable("projectId") Long projectId) {//the variable part of the URL 
		Credentials loggedCredentials = sessionData.getLoggedCredentials();
		User loggedUser = sessionData.getLoggedUser();
		//if no project with the passed ID exists
		//redirect to the view with the list of my projects
		Project project = projectService.getProject(projectId);
		if(project == null) return "redirect:/projects";

		//if I do not have access to any project with the passed ID,
		//redirect to the view with my projects' list

		List<User> members = userService.getMembers(project); //we need members from the service, Project --> User membership relation is Lazy
		if(!project.getOwner().equals(loggedUser) && !members.contains(loggedUser))//loggedUser do not have access to the project
			return "redirect:/projects";

		model.addAttribute("loggedCredentials", loggedCredentials);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("project", project);
		model.addAttribute("members", members);
		model.addAttribute("shareCredentialsForm", new Credentials()); //for the sharing form

		return "project";
	}

	/**
	 * this method is called when a GET request is sent by the user to URL "/projects/add
	 * this method prepares and dispatches a view containing the form to add a new Project
	 * @param model the Request model
	 * @return the name of the target view, that in this case is "addProject"
	 */
	@RequestMapping(value = {"/projects/add"}, method = RequestMethod.GET)
	public String createProjectForm(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectForm", new Project());
		return "addProject";
	}

	@RequestMapping(value = {"/projects/add"}, method = RequestMethod.POST)
	public String createProject(@Valid @ModelAttribute("projectForm") Project project,
			BindingResult projectBindingResult,
			Model model) {
		User loggedUser = sessionData.getLoggedUser();
		projectValidator.validate(project, projectBindingResult);
		if(!projectBindingResult.hasErrors()) {
			project.setOwner(loggedUser);
			this.projectService.saveProject(project);
			return "redirect:/projects/" + project.getId();
		}

		model.addAttribute("loggedUser", loggedUser);
		return "addProject";

	}

	/**
	 * This method is called when a POST request is sent by the user to url "/home/projects/{projectId}/delete
	 * This method deletes the user whose credentials are identified by the passed id {projectId}
	 * @param model the request model
	 * @param projectId the id of the Project to delete
	 * @return
	 */
	@RequestMapping(value = {"/projects/{projectId}/delete"}, method=RequestMethod.POST)
	public String removeProject(Model model, @PathVariable("projectId") Long projectId) {
		this.projectService.deleteProject(projectId);
		return "redirect:/projects"; //with this redirect the method myOwnedProjects will be recalled, 
		//in order to have the projectsList in the model
	}

	@RequestMapping(value = {"projects/{projectId}/share"}, method = RequestMethod.POST)
	public String shareProject(@Valid @ModelAttribute("shareCredentialsForm") Credentials shareCredentialsForm,
			BindingResult shareCredentialsFormBindingResult,
			@PathVariable("projectId") Long projectId,
			Model model) {
		Project project = this.projectService.getProject(projectId);
		Credentials sharerCredentials = sessionData.getLoggedCredentials();
		//only owner of the project can share it to other users
		if(project.getOwner().equals(sharerCredentials.getUser()) || sharerCredentials.getRole().equals("ADMIN")) {
			this.credentialsValidator.validateSharing(sharerCredentials, shareCredentialsForm, shareCredentialsFormBindingResult);
			if(!shareCredentialsFormBindingResult.hasErrors()) {
				User user2ShareWith = credentialsService.getUserByUserName(shareCredentialsForm.getUserName());
					this.projectService.shareProjectWithUser(project, user2ShareWith);
					return "redirect:/projects/"+projectId.toString();
			}
		}
		model.addAttribute("loggedCredentials", sharerCredentials);
		model.addAttribute("shareCredentialsForm", shareCredentialsForm);
		return "redirect:/projects/"+project.getId().toString();
	}
	
	@RequestMapping(value = {"/projects/{projectId}/update"}, method=RequestMethod.GET)
	public String updateProjectForm(Model model, @PathVariable("projectId") Long projectId) {
		User loggedUser = sessionData.getLoggedUser();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("updateProjectForm", new Project());
		model.addAttribute("project", this.projectService.getProject(projectId));
		return "updateProject";
	}
	
	
	@RequestMapping(value = {"/projects/{projectId}/update"}, method = RequestMethod.POST)
	public String updateProject(@Valid @ModelAttribute("updateProjectForm") Project project,
								BindingResult projectBindingResult,
								Model model, @PathVariable("projectId") Long projectId) {
		User loggedUser = sessionData.getLoggedUser();
		projectValidator.validate(project, projectBindingResult);
		if(!projectBindingResult.hasErrors()) {
			project.setId(projectId);
			project.setOwner(loggedUser);
			this.projectService.saveProject(project);
			return "redirect:/projects/"+project.getId().toString();
		}
		model.addAttribute("loggedUser", loggedUser);
		return "redirect:/projects/" + project.getId().toString();
		
	}
}
