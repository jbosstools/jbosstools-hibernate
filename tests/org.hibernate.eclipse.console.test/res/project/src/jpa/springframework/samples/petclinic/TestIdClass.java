package jpa.springframework.samples.petclinic;

import javax.persistence.*;

@Embeddable
public class TestIdClass {

	@Column
    protected Integer id;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
