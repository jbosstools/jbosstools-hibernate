package jpa.springframework.samples.petclinic;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseSuperclass {

    @EmbeddedId
    protected Object embeddedId;
    //@Id
    //protected Integer simpleId;

	public Object getTestId() {
		return embeddedId;
	}

	public void setTestId(Object embeddedId) {
		this.embeddedId = embeddedId;
	}

	//public Integer getSimpleId() {
	//	return simpleId;
	//}

	//public void setSimpleId(Integer simpleId) {
	//	this.simpleId = simpleId;
	//}
}
