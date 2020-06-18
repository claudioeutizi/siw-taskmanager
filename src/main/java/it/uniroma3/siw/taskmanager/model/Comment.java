package it.uniroma3.siw.taskmanager.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;


@Entity
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, length = 1000)
	private String text;
	
	@ManyToOne
	private User writer;
	
	@ManyToOne
	private Task task;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime creationTimestamp;
	
	

	
	/*Constructor*/
	
	public Comment() {
		
	}
	
	public Comment(String text, User writerOfComment, Task task) {
		this.text = text;
		this.writer = writerOfComment;
		this.task = task;
	}
	
	/*qualsiasi metodo marcato con questa annotazione
	 * in una @Entity viene eseguito sempre quando una 
	 * nuova istanza viene salvata in DB*/
	@PrePersist
	protected void prePersist() { 
		this.creationTimestamp= LocalDateTime.now();
	}

	/*Getter and Setter*/
	
	/***
	 * Get Id
	 * @return Long
	 */
	public Long getId() {
		return id;
	}

	/***
	 * Set id
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/***
	 * Get comment's text
	 * @return String
	 */
	public String getText() {
		return text;
	}

	/***
	 * Set comment's text
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	
	/***
	 * Get who wrote the comment
	 * @return User
	 */
	public User getWriter() {
		return writer;
	}

	/***
	 * Set the writer of comment
	 * @param writerOfComment
	 */
	public void setWriter(User writerOfComment) {
		this.writer = writerOfComment;
	}

	
	/***
	 * Get the task to which the comment refers
	 * @return
	 */
	public Task getTask() {
		return task;
	}
	
	 

	public LocalDateTime getCreationTimestamp() {
		return creationTimestamp;
	}


	/***
	 * Set the task to which the comment refers
	 * @return
	 */
	public void setTask(Task task) {
		this.task = task;
	}
	
	@Override
	public int hashCode() {
		return this.text.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		Comment that= (Comment)obj;
		return this.text.equals(that.getText());
	}
	
}
