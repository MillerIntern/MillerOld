package projectObjects;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 * This class represents a person. A person can be either a manager or a supervisor of a project.
 * @author Alex Campbell
 */

@Entity
public class Person extends ProjectObject
{
	/**
	 * The name of a person
	 */
	String name;
	
	public Person(String name)
	{
		this.name = name;
	}
	
	public Person()
	{
		this.name = "";
	}

	/**
	 * This method gets the name of the person
	 * @return the person's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method sets the name of the person.
	 * @param name the new name of the person
	 */
	public void setName(String name) {
		this.name = name;
	}
}
