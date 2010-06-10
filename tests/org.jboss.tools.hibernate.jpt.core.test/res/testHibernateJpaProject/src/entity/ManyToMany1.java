package entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class ManyToMany1 implements Serializable {
	
	@Id
	private int id1;	
	
	private String justData1;

	@ManyToMany(mappedBy = "mtm1")
	private Set<ManyToMany2> mtm2;
}