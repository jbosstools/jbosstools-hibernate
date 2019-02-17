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

@Entity
@Table(name = "ManyToMany22")
@NamedQuery(name = "my_query", query = "select all from my_table")
public class ManyToMany2 implements Serializable {
	
	@Id
	@Column(name = "id")
	@GenericGenerator(name = "my_generator", strategy = "hilo")
	private int id2;
	
	@Column(name = "justData")
	private String justData2;

	@ManyToMany
	private Set<ManyToMany1> mtm1;
}