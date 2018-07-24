package annotations.database.schema;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="ReferenceGeneProduct")
public class ReferenceGeneProduct implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String UniprotId;
	private String AASequence;
	private PRODUCT_TYPE type;
	private Set<ReferencePhysicalGene> referenceGene;
	
	public enum PRODUCT_TYPE {
		protein
	};
	
	public ReferenceGeneProduct() { }

	@Id @GeneratedValue
	@Column(name="ID", nullable=false, unique=true, precision=11)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="UniprotID", nullable=true, unique=true, length=20)
	public String getUniprotId() {
		return UniprotId;
	}

	public void setUniprotId(String uniprotId) {
		UniprotId = uniprotId;
	}

	@Column(name="amminoacidSequence")
	@Type(type="text")
	public String getAASequence() {
		return AASequence;
	}

	public void setAASequence(String aASequence) {
		AASequence = aASequence;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="type")
	public PRODUCT_TYPE getType() {
		return type;
	}

	public void setType(PRODUCT_TYPE type) {
		this.type = type;
	}

	@ManyToMany(fetch=FetchType.LAZY, mappedBy="geneProducts")
	public Set<ReferencePhysicalGene> getReferenceGene() {
		return referenceGene;
	}

	public void setReferenceGene(Set<ReferencePhysicalGene> referenceGene) {
		this.referenceGene = referenceGene;
	}
}
