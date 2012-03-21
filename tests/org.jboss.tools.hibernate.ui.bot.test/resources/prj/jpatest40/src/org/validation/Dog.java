package org.validation;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Dog
 *
 */
@Entity
public class Dog implements Serializable {

	@Id
	long id;
	
	private Owner owner;
	
	private static final long serialVersionUID = 1L;

	@ManyToOne()
	public Owner getCustomer() { return owner; }
	
	public Dog() {
		super();
	}
   
}
