/*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package test.annotated.fields;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
public class FotoXPerson {

	protected Integer ffotoId;

	protected PersonXFoto person;

	protected Set<PersonXFoto> persons = new HashSet<PersonXFoto>(0);

	@Id @GeneratedValue
	public Integer getFfotoId() {
		return this.ffotoId;
	}

	public void setFfotoId(Integer ffotoId) {
		this.ffotoId = ffotoId;
	}

	@ManyToOne
	public PersonXFoto getPerson() {
		return person;
	}

	public void setPerson(PersonXFoto person) {
		this.person = person;
	}

	@OneToMany(mappedBy = "foto")
	public Set<PersonXFoto> getPersons() {
		return this.persons;
	}

	public void setPersons(Set<PersonXFoto> persons) {
		this.persons = persons;
	}

	protected Integer version;
	
	@Version 
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
}
