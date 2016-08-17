package projectObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ProjectType extends ProjectObject {

		String name;
		
		public ProjectType(String name)
		{
			this.name = name;
		}
		
		public ProjectType()
		{
			name = null;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public void setName(String name)
		{
			this.name = name;
		}
		

		
}
