package org.jpadetails;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.NamedNativeQuery;

/**
 * Entity implementation class for Entity: Dog
 *
 */
@Entity
@NamedNativeQuery(name = "query ", query = "SELECT * FROM MACHINE")
public class Machine implements Serializable {

	@Id
	long id;
		
	private static final long serialVersionUID = 1L;
   
}
