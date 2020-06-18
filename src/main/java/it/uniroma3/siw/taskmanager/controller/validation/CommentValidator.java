package it.uniroma3.siw.taskmanager.controller.validation;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.taskmanager.model.Comment;

@Component
public class CommentValidator implements Validator {

	public final Integer MAX_COMMENT_LENGTH = 1000;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Comment.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Comment comment= (Comment)target;
		String text= comment.getText().trim();
        
        /*description validation*/
		if(text.isBlank())
			errors.rejectValue("text","required");
		else if(text.length() > MAX_COMMENT_LENGTH)
        	errors.rejectValue("text", "size");   
     }   
	
}
