package org.validation;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * Entity implementation class for Entity: GeneratorValidationEntity
 *
 */
@GenericGenerator(name="mygen", strategy = "uuid")

@Entity
public class GeneratorValidationEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "mygen")
	long id;
	
	private static final long serialVersionUID = 1L;
	
	public GeneratorValidationEntity() {
		super();
	}
   
}
