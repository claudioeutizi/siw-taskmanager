package it.uniroma3.siw.taskmanager.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Entity(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(updatable = false, nullable = false)
	private LocalDateTime creationTimestamp;

	@Column(nullable = false)
	private LocalDateTime lastUpdateTimestamp;

	/* Lista dei progetti posseduti dall'user */
	@OneToMany(cascade=CascadeType.REMOVE,mappedBy = "owner",fetch=FetchType.LAZY)
	private List<Project> ownedProjects;


	@ManyToMany(mappedBy="members",fetch=FetchType.LAZY,cascade = CascadeType.REMOVE)
	private List<Project> visibleProjects;

	@OneToMany(mappedBy = "assignedUser")
	private List<Task> assignedTasks;
	
	@OneToMany(mappedBy = "writer")
	private List<Comment> writtenComments; 

	@PrePersist
	protected void onPersist() {
		this.creationTimestamp = LocalDateTime.now();
		this.lastUpdateTimestamp = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.lastUpdateTimestamp = LocalDateTime.now();
	}

	public User() {
		this.ownedProjects = new ArrayList<>();
		this.visibleProjects = new ArrayList<>();
		this.assignedTasks = new ArrayList<>();
		this.writtenComments = new ArrayList<>();
	}


	public User(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}


	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName.toUpperCase();
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName.toUpperCase();
	}

	/**
	 * @return the creationTimestamp
	 */
	public LocalDateTime getCreationTimestamp() {
		return creationTimestamp;
	}

	/**
	 * @param creationTimestamp the creationTimestamp to set
	 */
	public void setCreationTimestamp(LocalDateTime creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	/**
	 * @return the lastUpdateTimestamp
	 */
	public LocalDateTime getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	/**
	 * @param lastUpdateTimestamp the lastUpdateTimestamp to set
	 */
	public void setLastUpdateTimestamp(LocalDateTime lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	public List<Project> getOwnedProjects() {
		return ownedProjects;
	}

	public void setOwnedProjects(List<Project> ownedProjects) {
		this.ownedProjects = ownedProjects;
	}

	public List<Project> getVisibleProjects() {
		return visibleProjects;
	}

	public void setVisibleProjects(List<Project> visibleProjects) {
		this.visibleProjects = visibleProjects;
	}

	public List<Task> getAssignedTasks() {
		return assignedTasks;
	}

	public void setAssignedTasks(List<Task> assignedTasks) {
		this.assignedTasks = assignedTasks;
	}
	
	public List<Comment> getWrittenComments() {
		return writtenComments;
	}

	public void setWrittenComments(List<Comment> writtenComments) {
		this.writtenComments = writtenComments;
	}
	
	public void addComment(Comment comment) {
		this.writtenComments.add(comment);
	}

	// EQUALS AND HASHCODE

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;
		User user = (User) o;
		return  Objects.equals(this.firstName, user.firstName) &&
				Objects.equals(this.lastName, user.lastName) &&
				Objects.equals(this.creationTimestamp, user.creationTimestamp) &&
				Objects.equals(this.lastUpdateTimestamp, user.lastUpdateTimestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName);
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", creationTimestamp=" + creationTimestamp +
				", lastUpdateTimestamp=" + lastUpdateTimestamp +
				'}';
	}





}
