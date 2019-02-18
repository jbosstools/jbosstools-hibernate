//$Id$
package core.ops;

import java.util.Collection;
import java.io.Serializable;


/**
 * Employee in an Employer-Employee relationship
 * 
 * @author Emmanuel Bernard
 */

public class Employee implements Serializable {
	private Integer id;
	private Collection employers;


	public Integer getId() {
		return id;
	}

	public void setId(Integer integer) {
		id = integer;
	}
	
	
	public Collection getEmployers() {
		return employers;
	}
	
	public void setEmployers(Collection employers) {
		this.employers = employers;
	}
}
