package annotations.database.schema;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class GeneNamesId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Long referenceGeneId;
	
	public GeneNamesId() { }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getReferenceGeneId() {
		return referenceGeneId;
	}

	public void setReferenceGeneId(Long referenceGeneId) {
		this.referenceGeneId = referenceGeneId;
	}
	
	@Override
	public boolean equals(Object obj) {
		GeneNamesId other = (GeneNamesId) obj;
		
		if (obj == null)
			return false;
		// check if tested object is the same class as the original one
		if (obj.getClass() != GeneNamesId.class)
			return false;
		
		// check nulls
		if (this.getName() == null | this.getReferenceGeneId() == null)
			return false;
		if (other.getName() == null | other.getReferenceGeneId() == null)
			return false;
		
		// compare values
		if (this.getName().equals(other.getName()) & other.getName().equals(this.getName()))
			return true;
		if (this.getReferenceGeneId().equals(other.getReferenceGeneId()) & other.getReferenceGeneId().equals(this.getReferenceGeneId()))
			return true;
		
		return false;
	}
	
	@Override
	public int hashCode() {
		
		int hash = this.name.hashCode();
		hash ^= 41*hash+this.referenceGeneId.hashCode();
		
		return hash;
	}
}
