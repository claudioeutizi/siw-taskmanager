package it.uniroma3.siw.taskmanager.controller.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.taskmanager.model.Tag;

@Component
public class TagValidator implements Validator{

	final Integer MAX_NAME_LENGTH= 80;
    final Integer MIN_NAME_LENGTH= 5;
    final Integer MAX_DESC_LENGTH= 500;
    final Integer MIN_DESC_LENGTH= 10;
    
    @Override
    public boolean supports(Class<?> clazz) {
        return Tag.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Tag tag= (Tag)target;
        String name= tag.getName().trim();
        String description= tag.getDescription().trim();
        
        /*name validation*/
        if(name.isEmpty())
            errors.rejectValue("name", "required");
        else if(name.length()< MIN_NAME_LENGTH || name.length()>MAX_NAME_LENGTH){
            errors.rejectValue("name", "size");
        }
        
        /*description validation*/
        if(description.isEmpty())
            errors.rejectValue("description", "required");
        else if(description.length()< MIN_DESC_LENGTH || description.length()>MAX_DESC_LENGTH){
            errors.rejectValue("description", "size");
        } 
    }
}
