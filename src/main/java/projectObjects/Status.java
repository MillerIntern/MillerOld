package projectObjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Status extends ProjectObject
{
	String name;
	
	public Status()
	{
		name = "";
	}
	
	public Status(String name)
	{
		this.name = name;
	}
	
	public void setName(String s)
	{
		this.name = s;
	}
	
	public String getName()
	{
		return name;
	}
}
