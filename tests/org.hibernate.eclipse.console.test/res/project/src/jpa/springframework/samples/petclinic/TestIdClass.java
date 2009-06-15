package jpa.springframework.samples.petclinic;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TestIdClass {

	//@Id
    //protected Integer id;
    @EmbeddedId
    protected Object id;

	//public Integer getId() {
	public Object getId() {
		return id;
	}

	//public void setId(Integer id) {
	public void setId(Object id) {
		this.id = id;
	}
}
