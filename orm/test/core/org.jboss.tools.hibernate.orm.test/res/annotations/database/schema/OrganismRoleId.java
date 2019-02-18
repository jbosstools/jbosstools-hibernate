package annotations.database.schema;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import annotations.database.schema.OrganismRoleInReferenceSystemProcess.ROLE_TYPE;

@Embeddable
public class OrganismRoleId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long referenceOrganismId;
	private Long referenceSystemProcessId;
	private ROLE_TYPE roleType;
	
	public OrganismRoleId() { }

	public Long getReferenceOrganismId() {
		return referenceOrganismId;
	}

	public void setReferenceOrganismId(Long referenceOrganismId) {
		this.referenceOrganismId = referenceOrganismId;
	}

	public Long getReferenceSystemProcessId() {
		return referenceSystemProcessId;
	}

	public void setReferenceSystemProcessId(Long referenceSystemProcessId) {
		this.referenceSystemProcessId = referenceSystemProcessId;
	}

	@Enumerated(EnumType.STRING)
	public ROLE_TYPE getRoleType() {
		return roleType;
	}

	public void setRoleType(ROLE_TYPE roleType) {
		this.roleType = roleType;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj==null)
			return false;
		
		// check if tested object is the same class as the original one
		if (obj.getClass() != OrganismRoleId.class)
			return false;
		
		OrganismRoleId tested = (OrganismRoleId) obj;
		// check nulls
		if (this.referenceOrganismId == null | this.referenceSystemProcessId == null | this.roleType == null)
			return false;
		if (tested.getReferenceOrganismId() == null | 
				tested.getReferenceSystemProcessId() == null | 
					tested.getRoleType() == null)
			return false;
		
		// check values
		if (this.referenceOrganismId.equals(tested.getReferenceOrganismId()) & 
				tested.getReferenceOrganismId().equals(this.referenceOrganismId))
			return true;
		if (this.referenceSystemProcessId.equals(tested.getReferenceSystemProcessId()) &
				tested.getReferenceSystemProcessId().equals(this.referenceSystemProcessId))
			return true;
		if (this.roleType.equals(tested.getRoleType()) & tested.getRoleType().equals(this.roleType))
			return true;
		
		
		return false;
	}
	
	@Override
	public int hashCode() {
		
		int hash = referenceOrganismId.hashCode();
		hash ^= 41*hash+referenceSystemProcessId.hashCode();
		hash ^= 41*hash+roleType.hashCode();
		
		return hash;
	}
}
