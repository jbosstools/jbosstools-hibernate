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
@Table(name="CVgeneric")
public class CVgeneric implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private CV_TYPE type;
	private String category;
	private CV_STATUS status;
	private String term;
	private String description;
	private Long oboId;
	private String oboNs;
	private String oboPrefix;	
	
	public enum CV_TYPE {
		processPhenotype,
		pathogenPhenotype,
		chemicalPhenotype,
		dnastructurePhenotype,
		genePhenotype,
		geneproductPhenotype,
		cmode,
		processCharacterization,
		organismCharacterization,
		chemicalCharacterization,
		dnastructureCharacterization,
		geneCharacterization,
		geneproductCharacterization,
		modeOfAction,
		experimentalConditions,
		biologicalProcess,
		comparativeValue,
		disease,
		hostPhenotype
	}
	
	public enum CV_STATUS {
		accepted,proposed,toRefine
	}
	
	public CVgeneric() { }

	@Id
	@GeneratedValue
	@Column(name="ID", nullable=false, unique=true, precision=11)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="type", nullable=false)
	@Enumerated(EnumType.STRING)
	public CV_TYPE getType() {
		return type;
	}

	public void setType(CV_TYPE type) {
		this.type = type;
	}

	@Column(name="Category", length=45)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Column(name="status", nullable=false)
	@Enumerated(EnumType.STRING)
	public CV_STATUS getStatus() {
		return status;
	}

	public void setStatus(CV_STATUS status) {
		this.status = status;
	}

	@Column(name="term", length=255, nullable=false)
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	@Column(name="Description")
	@Type(type="text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name="oboID", precision=11)
	public Long getOboId() {
		return oboId;
	}

	public void setOboId(Long oboId) {
		this.oboId = oboId;
	}

	@Column(name="oboNS", length=255)
	public String getOboNs() {
		return oboNs;
	}

	public void setOboNs(String oboNs) {
		this.oboNs = oboNs;
	}

	@Column(name="oboPrefix", length=10)
	public String getOboPrefix() {
		return oboPrefix;
	}

	public void setOboPrefix(String oboPrefix) {
		this.oboPrefix = oboPrefix;
	}
}
