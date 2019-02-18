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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Table(name="ReferenceOrganism")
public class ReferenceOrganism implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;	
	private Long ncbiId;
	private COTILEDON_TYPE cotiledonType;
	private String systematicName;
	private String commonName;
	private String tempName;
	private String strain;
	private Set<ReferencePhysicalGene> referenceGenes;
	
	public enum COTILEDON_TYPE {
		monocotiledon,
		dicotiledon
	}
	
	public ReferenceOrganism() { }

	@Id
	@GeneratedValue
	@Column(name="ID", nullable=false, unique=true, precision=11)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="NCBI_Taxonomy_ID", nullable=false, unique=true, precision=11)
	@Index(name = "nvbiTax")
	public Long getNcbiId() {
		return ncbiId;
	}

	public void setNcbiId(Long ncbiId) {
		this.ncbiId = ncbiId;
	}

	@Column(name="cotiledonType")
	@Enumerated(EnumType.STRING)
	public COTILEDON_TYPE getCotiledonType() {
		return cotiledonType;
	}

	public void setCotiledonType(COTILEDON_TYPE cotiledonType) {
		this.cotiledonType = cotiledonType;
	}

	@Column(name="systematicName", length=255)
	public String getSystematicName() {
		return systematicName;
	}

	public void setSystematicName(String systematicName) {
		this.systematicName = systematicName;
	}

	@Column(name="commonName", length=255)
	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	@Column(name="tempName", nullable=false, length=255)
	public String getTempName() {
		return tempName;
	}

	public void setTempName(String tempName) {
		this.tempName = tempName;
	}

	@Column(name="strain", length=255)
	public String getStrain() {
		return strain;
	}

	public void setStrain(String strain) {
		this.strain = strain;
	}

	@OneToMany(mappedBy="referenceOrganism", fetch=FetchType.LAZY)
	public Set<ReferencePhysicalGene> getReferenceGenes() {
		return referenceGenes;
	}

	public void setReferenceGenes(Set<ReferencePhysicalGene> refGenes) {
		this.referenceGenes = refGenes;
	}
}
