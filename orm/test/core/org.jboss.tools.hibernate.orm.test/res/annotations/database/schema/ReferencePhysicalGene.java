package annotations.database.schema;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;




@Entity
@Table(name="ReferencePhysicalGene")
public class ReferencePhysicalGene implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String genericId;
	private PhysicalGeneGenericIdType idType;
	private String nucleotideSequence;
	private ReferenceOrganism referenceOrganism;
	private String tempGeneName;
	private Set<GeneNames> geneNames;
	private Set<ReferenceGeneProduct> geneProducts;
	
	public ReferencePhysicalGene() { }

	@Id @GeneratedValue
	@Column(name="ID", nullable=false, unique=true, precision=11)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="GenericID", length=255)
	public String getGenericId() {
		return genericId;
	}

	public void setGenericId(String genericId) {
		this.genericId = genericId;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="IDType", nullable=false, updatable=false, insertable=false)
	public PhysicalGeneGenericIdType getIdType() {
		return idType;
	}

	public void setIdType(PhysicalGeneGenericIdType idType) {
		this.idType = idType;
	}

	@Type(type="text")
	@Column(name="nucleotideSequence")
	public String getNucleotideSequence() {
		return nucleotideSequence;
	}

	public void setNucleotideSequence(String nucleotideSequence) {
		this.nucleotideSequence = nucleotideSequence;
	}

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="referenceOrganismID", nullable=false)
	public ReferenceOrganism getReferenceOrganism() {
		return referenceOrganism;
	}

	public void setReferenceOrganism(ReferenceOrganism referenceOrganism) {
		this.referenceOrganism = referenceOrganism;
	}

	@Column(name="tempGeneName", nullable=false, length=50)
	public String getTempGeneName() {
		return tempGeneName;
	}
	public void setTempGeneName(String tempGeneName) {
		this.tempGeneName = tempGeneName;
	}

	@OneToMany(fetch=FetchType.LAZY, mappedBy="referenceGene")
	public Set<GeneNames> getGeneNames() {
		return geneNames;
	}

	public void setGeneNames(Set<GeneNames> geneNames) {
		this.geneNames = geneNames;
	}

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="ReferencePhysicalGeneEncodesReferenceGeneProduct",
			joinColumns= @JoinColumn(name="referenceGeneID", referencedColumnName="ID"),
			inverseJoinColumns= @JoinColumn(name="referenceGeneProductID", referencedColumnName="ID")
	)
	public Set<ReferenceGeneProduct> getGeneProducts() {
		return geneProducts;
	}

	public void setGeneProducts(Set<ReferenceGeneProduct> geneProducts) {
		this.geneProducts = geneProducts;
	}
}
