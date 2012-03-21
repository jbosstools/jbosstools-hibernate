package org.validation;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 * Entity implementation class for Entity: Owner
 *
 */
@Entity
@Table(name = "OWNER")
public class Owner implements Serializable {

	@Id
	@Generated(GenerationTime.ALWAYS)	
	long id;
	
	private static final long serialVersionUID = 1L;
	
	private List<Dog> dogs;
	
	@OneToMany(mappedBy="Owner")
	@OrderBy("number")
	public List<Dog> getDogs() { return dogs; }

	public Owner() {
		super();
	}
   
}
