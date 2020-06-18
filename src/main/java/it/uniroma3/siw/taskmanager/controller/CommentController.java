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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.CommentValidator;
import it.uniroma3.siw.taskmanager.model.Comment;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.service.CommentService;
import it.uniroma3.siw.taskmanager.service.TaskService;

@Controller
public class CommentController {

	@Autowired
	private TaskService taskService;

	@Autowired
	private SessionData sessionData;

	@Autowired
	private CommentService commentService;

	@Autowired
	private CommentValidator commentValidator;

	@RequestMapping(value = "{projectId}/tasks/{taskId}/comments/add", method = RequestMethod.POST)
	public String postComment(@Valid @ModelAttribute("commentForm") Comment comment, BindingResult commentBindingResult,
			@PathVariable("taskId") Long taskId, @PathVariable("projectId") Long projectId,
			RedirectAttributes redirect, Model model) {

		Task task = this.taskService.getTask(taskId);

		this.commentValidator.validate(comment, commentBindingResult);

		if(!commentBindingResult.hasErrors()) {
			comment.setWriter(this.sessionData.getLoggedUser());
			comment.setTask(task);
			task.addComment(comment);
			this.taskService.saveTask(task);
		}

		redirect.addAttribute("taskId", taskId);
		redirect.addAttribute("projectId", projectId);
		return "redirect:/tasks/{taskId}";
	}

	@RequestMapping(value = "{projectId}/tasks/{taskId}/comments/{commentId}/delete", method = RequestMethod.POST)
	public String deleteComment(@PathVariable("commentId") Long commentId,@PathVariable("taskId") Long taskId, 
			@PathVariable("projectId") Long projectId, RedirectAttributes redirect, Model model) {

		Comment comment= this.commentService.findById(commentId);
		Task task= this.taskService.getTask(taskId);
		task.getComments().remove(comment);
		this.commentService.deleteComment(comment);
		
		redirect.addAttribute("taskId", taskId);
		redirect.addAttribute("projectId", projectId);
		return "redirect:/task/{taskId}/{projectId}";
	}

}
