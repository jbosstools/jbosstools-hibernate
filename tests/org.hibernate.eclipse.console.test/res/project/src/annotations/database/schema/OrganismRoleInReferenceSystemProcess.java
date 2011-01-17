package annotations.database.schema;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="OrganismRoleInReferenceSystemProcess")
public class OrganismRoleInReferenceSystemProcess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private OrganismRoleId id;
	private ROLE_TYPE roleType;
	private String description;
	private ReferenceOrganism referenceOrganism;
	private ReferenceSystemProcess referenecSystemProcess;
	
	public enum ROLE_TYPE {
		host,
		pathogen,
		participant
	}

	public OrganismRoleInReferenceSystemProcess() { }

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="referenceOrganism", column=@Column(name="referenceOrganism", nullable=false)),
		@AttributeOverride(name="referenceSystemProcess", column=@Column(name="referenceSystemProcess", nullable=false)),
		@AttributeOverride(name="roleType", column=@Column(name="roleType", nullable=false))
	})
	public OrganismRoleId getId() {
		return id;
	}


	public void setId(OrganismRoleId id) {
		this.id = id;
	}
	
	@Column(name="roleType", nullable=false, updatable=false, insertable=false)
	@Enumerated(EnumType.STRING)
	public ROLE_TYPE getRoleType() {
		return roleType;
	}

	public void setRoleType(ROLE_TYPE roleType) {
		this.roleType = roleType;
	}

	@Column(name="description")
	@Type(type="text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="referenceOrganism", nullable=false, updatable=false, insertable=false)
	public ReferenceOrganism getReferenceOrganism() {
		return referenceOrganism;
	}

	public void setReferenceOrganism(ReferenceOrganism referenceOrganism) {
		this.referenceOrganism = referenceOrganism;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="referenceSystemProcess", nullable=false, updatable=false, insertable=false)
	public ReferenceSystemProcess getReferenecSystemProcess() {
		return referenecSystemProcess;
	}

	public void setReferenecSystemProcess(
			ReferenceSystemProcess referenecSystemProcess) {
		this.referenecSystemProcess = referenecSystemProcess;
	}
}
