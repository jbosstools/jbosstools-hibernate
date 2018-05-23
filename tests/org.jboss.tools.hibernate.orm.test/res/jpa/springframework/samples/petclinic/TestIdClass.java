package jpa.springframework.samples.petclinic;

import java.io.Serializable;

import javax.persistence.*;

@Embeddable
public class TestIdClass implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column
    protected Integer id;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
