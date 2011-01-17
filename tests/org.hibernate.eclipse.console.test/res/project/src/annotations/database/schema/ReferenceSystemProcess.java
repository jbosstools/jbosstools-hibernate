package annotations.database.schema;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="ReferenceSystemProcess")
public class ReferenceSystemProcess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private ReferenceSystemProcessType type;
	private String description;
	private Long tempToVerify;
	
	public ReferenceSystemProcess() { }

	@Id @GeneratedValue
	@Column(name="ID", nullable=false, unique=true, precision=11)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="name", length=255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="type")
	@Enumerated(EnumType.STRING)
	public ReferenceSystemProcessType getType() {
		return type;
	}

	public void setType(ReferenceSystemProcessType type) {
		this.type = type;
	}

	@Type(type="text")
	@Column(name="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name="tempToVerify", precision=11)
	public Long getTempToVerify() {
		return tempToVerify;
	}

	public void setTempToVerify(Long tempToVerify) {
		this.tempToVerify = tempToVerify;
	}
}
