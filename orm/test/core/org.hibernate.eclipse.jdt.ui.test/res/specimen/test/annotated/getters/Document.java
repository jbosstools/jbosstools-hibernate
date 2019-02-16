/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package test.annotated.getters;

import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class Document extends DocumentBase {

	private Long documentId;

	private Person documentOwner;

	private Set<Person> persons;
	
	private Byte docType = 0;

	@GeneratedValue @javax.persistence.Id
	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	@ManyToOne
	public Person getDocumentOwner() {
		return documentOwner;
	}

	public void setDocumentOwner(Person documentOwner) {
		this.documentOwner = documentOwner;
	}

	@javax.persistence.OneToMany
	public Set<Person> getPersons() {
		return persons;
	}

	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}

	public Byte getDocType() {
		return docType;
	}

	protected void setDocType(Byte docType) {
		this.docType = docType;
	}

}
