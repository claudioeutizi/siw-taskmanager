package it.uniroma3.siw.taskmanager.controller.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.service.CredentialsService;


@Component
public class CredentialsValidator implements Validator {

	@Autowired
	CredentialsService credentialsService;
	final Integer MAX_USERNAME_LENGTH = 20;
	final Integer MIN_USERNAME_LENGTH = 4;
	final Integer MAX_PASSWORD_LENGTH = 20;
	final Integer MIN_PASSWORD_LENGTH = 6;
	@Override
	public boolean supports(Class<?> clazz) {
		return Credentials.class.equals(clazz);
	}

	@Override
	public void validate(Object o, Errors errors) {
		Credentials credentials = (Credentials) o;
		String userName = credentials.getUserName().trim();
		String password = credentials.getPassword().trim();
		if(userName.isBlank())
			errors.rejectValue("userName", "required");
		else if(userName.length() < MIN_USERNAME_LENGTH || userName.length() > MAX_USERNAME_LENGTH)
			errors.rejectValue("userName", "size");
		else if(this.credentialsService.getCredentials(userName) != null)
			errors.rejectValue("userName", "duplicate");

		if(userName.isBlank())
			errors.rejectValue("password", "required");
		else if(password.length() < MIN_USERNAME_LENGTH || password.length() > MAX_USERNAME_LENGTH)
			errors.rejectValue("password", "size");
	}

	public void validateSharing(Credentials sharerCredentials, Credentials credentials2ShareWith, Errors errors) {
		String userName = credentials2ShareWith.getUserName();
		if(userName.trim().isBlank()) 
			errors.rejectValue("userName", "required");
		else if(userName.trim().length() < MIN_USERNAME_LENGTH || userName.trim().length() > MAX_USERNAME_LENGTH)
			errors.rejectValue("userName", "size");
		else if(credentialsService.getCredentials(userName) == null)
			errors.rejectValue("userName", "notExists");
		else if(credentialsService.getUserByUserName(userName).equals(sharerCredentials.getUser()))
			errors.rejectValue("userName", "sameAsOwner");
	}
	
	public void validateAssignment(Credentials credentials2Assign2Task, Project project, Errors errors) {
		String userName = credentials2Assign2Task.getUserName();
		if(userName.trim().isBlank()) 
			errors.rejectValue("userName", "required");
		else if(userName.trim().length() < MIN_USERNAME_LENGTH || userName.trim().length() > MAX_USERNAME_LENGTH)
			errors.rejectValue("userName", "size");
		else if(credentialsService.getCredentials(userName) == null)
			errors.rejectValue("userName", "notExists");
		//you can be assigned to a task if you are the owner of task's project or if you are a member of the task's project
		else if(!(project.getOwner().equals(credentialsService.getUserByUserName(userName)))
				&& !(project.getMembers().contains(credentialsService.getUserByUserName(userName))))
			errors.rejectValue("userName", "notTheOwnerOrAMember");
	}

}
