package org.hibernate.ui.test.model;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
	private String street1;

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

}