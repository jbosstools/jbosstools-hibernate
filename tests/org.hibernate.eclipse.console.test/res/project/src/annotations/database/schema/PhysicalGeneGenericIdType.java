package annotations.database.schema;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PhysicalGeneGenericIDType")
public class PhysicalGeneGenericIdType implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;
	private String description;
	
	public PhysicalGeneGenericIdType() { }
	
	
	@Id
	@GeneratedValue
	@Column(name="ID", nullable=false, unique=true, precision=11)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	@Column(name="name", nullable=false, length=45)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="description", length=45)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
