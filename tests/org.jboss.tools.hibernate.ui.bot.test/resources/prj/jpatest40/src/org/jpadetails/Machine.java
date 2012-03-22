package org.jpadetails;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Entity implementation class for Entity: Dog
 *
 */
@Entity
public class Machine implements Serializable {

	@Id
	long id;
		
	private static final long serialVersionUID = 1L;
   
}
