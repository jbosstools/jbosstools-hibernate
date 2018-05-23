package jpa.springframework.samples.petclinic;

import javax.persistence.Id;

public class SuperOffspring extends BaseSuperclass {

	private Integer justProperty;

	public void setJustProperty(Integer justProperty) {
		this.justProperty = justProperty;
	}

	public Integer getJustProperty() {
		return justProperty;
	}
}
