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
import it.uniroma3.siw.taskmanager.controller.validation.UserValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	SessionData sessionData;
	
	@Autowired
	CredentialsService credentialsService;
	
	@Autowired
	UserService userService;
	
	 @Autowired
	 UserValidator userValidator;
	 
	/**
	 * this method is called when a GET request is sent by the user to URL "/home".
	 * this method prepares and dispatches the User registration view
	 * @param model the Request model
	 * @return the name of the target view, that in this case is "home"
	 */
	@RequestMapping(value = { "/home" }, method = RequestMethod.GET)
	public String home(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		model.addAttribute("loggedUser", loggedUser);
		return "home";
	}
	
	
	
	/**
	 * this method is called when a GET request is sent by the user to URL "/admin".
	 * this method prepares and dispatches the admin view
	 * @param model the Request model
	 * @return the name of the target view, that in this case is "admin"
	 */
	@RequestMapping(value = { "/admin" }, method = RequestMethod.GET)
	public String admin(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		model.addAttribute("loggedUser", loggedUser);
		return "admin";
	}
	
	
	/**
	 * this method is called when a GET request is sent by the url "/users/me"
	 * this method prepares and dispatches the user's information view
	 * @param model the request model
	 * @return the name of the target view, which in this case is "/users/me"
	 */
	@RequestMapping(value = {"/users/me"}, method = RequestMethod.GET)
	public String me(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		Credentials credentials = sessionData.getLoggedCredentials();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("credentials", credentials);
		return "userProfile";
	}
	
	
	/**
	 * This method is called when a GET request is sent by the user to URL "/admin/users".
	 * This method prepares and dispatches the view eith the list of all users for admin usage.
	 * 
	 * @param model the Request Model
	 * @return the name of the target view, that in this case is "allUsers"
	 */
	@RequestMapping(value = { "/admin/users"}, method = RequestMethod.GET)
	public String usersList(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Credentials> allCredentials = this.credentialsService.getAllCredentials();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("allCredentials", allCredentials);
		return "allUsers";
	}
	
	
	/**
	 * This method is called when a POST request is sent by the user to url "/admin/users/{username}/delete
	 * This method deletes the user whose credentials are identified by the passed username {username}
	 * @param model the request model
	 * @param username the username of the Credentials to delete
	 * @return
	 */
	@RequestMapping(value = {"admin/users/{username}/delete"}, method=RequestMethod.POST)
	public String removeUser(Model model, @PathVariable String username) {
		this.credentialsService.deleteCredentials(username);
		return "redirect:/admin/users"; //with this redirect the method usersList will be recalled, 
										//in order to have the credentialsList in the model
	}
	
	/**
	 * This method is called when a POST request is sent by the user to url "/admin/users/{username}/setAdmin
	 * This method sets the user whose credentials are identified by the passed username {username} as an ADMIN
	 * @param model the request model
	 * @param username the username of the Credentials to set as an ADMIN
	 * @return
	 */
	@RequestMapping(value = {"admin/users/{username}/setAdmin"}, method=RequestMethod.POST)
	public String setAdmin(Model model, @PathVariable("username") String username) {
		Credentials newAdmin = this.credentialsService.getCredentials(username);
		newAdmin.setRole("ADMIN");
		model.addAttribute("newAdmin", newAdmin);
		return "setAdminSuccessful"; //with this redirect the method usersList will be recalled, 
										//in order to have the credentialsList in the model
	}
	
	@RequestMapping(value = { "/user/me/update" }, method = RequestMethod.GET)
    public String showUpdateForm(Model model) {
    	User loggedUser = sessionData.getLoggedUser();
    	Credentials loggedCredentials = sessionData.getLoggedCredentials();
        model.addAttribute("updateUserForm", new User());
        model.addAttribute("loggedUser",loggedUser);
        model.addAttribute("credentials", loggedCredentials);
        return "updateUser";
    }

    /**
     * This method is called when a GET request is sent by the user to URL "/register".
     * This method prepares and dispatches the User registration view.
     *
     * @param model the Request model
     * @return the name of the target view, that in this case is "register"
     */
    
    @RequestMapping(value = { "/user/me/update" }, method = RequestMethod.POST)
    public String updateUser(@Valid @ModelAttribute("updateUserForm") User updateUserForm,
    						BindingResult updateUserBindingResult,
    						Model model) {
    	User loggedUser = this.sessionData.getLoggedUser();
    	Credentials loggedCredentials = this.sessionData.getLoggedCredentials();
        this.userValidator.validate(updateUserForm, updateUserBindingResult);
        if(!updateUserBindingResult.hasErrors()) {
        	loggedUser.setFirstName(updateUserForm.getFirstName());
        	loggedUser.setLastName(updateUserForm.getLastName());
        	loggedUser = userService.saveUser(loggedUser);
            model.addAttribute("loggedUser",loggedUser);
            model.addAttribute("credentials", loggedCredentials);
    		System.out.println(loggedCredentials.getUserName());
        	this.sessionData.update();
            return "userProfile";
        }
        
        return "updateUser";
    }

}
