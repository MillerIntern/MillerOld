package projectObjects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This class represents a city. A city is always associated with a Warehouse
 * @author Alex Campbell
 */
@Entity
public class City extends ProjectObject
{
	/**
	 * The name of the city
	 */
	private String name;
	
	public City()
	{
		id = new Long(-1);
		name = "NULL";
	}
	
	public City(String name)
	{
		this.name = name;
	}

	/**
	 * This method gets the name of the city
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * This method returns the name of a city
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
