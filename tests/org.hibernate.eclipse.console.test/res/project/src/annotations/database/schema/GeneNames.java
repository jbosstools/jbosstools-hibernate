package annotations.database.schema;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="GeneNames")
public class GeneNames implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GeneNamesId id;
	private ReferencePhysicalGene referenceGene;
	private boolean pragmaDisplay;
	
	public GeneNames() { }

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="referenceGeneId", column = @Column(name="referenceGeneID", nullable=false)),
		@AttributeOverride(name="name", column= @Column(name="name", nullable=false, length=255))
	})
	public GeneNamesId getId() {
		return id;
	}

	public void setId(GeneNamesId id) {
		this.id = id;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="referenceGeneID", nullable=false, updatable=false, insertable=false)
	public ReferencePhysicalGene getReferenceGene() {
		return referenceGene;
	}

	public void setReferenceGene(ReferencePhysicalGene referenceGene) {
		this.referenceGene = referenceGene;
	}

	@Column(name="pragmaDisplay", nullable=false)
	public boolean isPragmaDisplay() {
		return pragmaDisplay;
	}


	public void setPragmaDisplay(boolean pragmaDisplay) {
		this.pragmaDisplay = pragmaDisplay;
	}
	
	
}
