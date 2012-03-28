package org.packageinfo;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Dog
 *
 */
@Entity
public class Table implements Serializable {

	@Id
	@GeneratedValue(generator="myuuidgen")
	long id;
	
	private static final long serialVersionUID = 1L;

	public Table() {
		super();
	}
   
}
