package entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Column;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQuery;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import static javax.persistence.FetchType.EAGER;

public class ManyToMany2 implements Serializable {
	
	private int id2;
	
	private String simpleData2;

	private Set<ManyToMany1> mtm1;
}