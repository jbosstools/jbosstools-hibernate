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
package test.annotated.fields;

import java.util.Set;

@javax.persistence.Entity
public class Document extends DocumentBase {

	@javax.persistence.Id
	protected Long documentId;

	protected Person documentOwner;

	@javax.persistence.OneToMany
	protected Set<Person> persons;
	
	protected Byte docType = 0;

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public Person getDocumentOwner() {
		return documentOwner;
	}

	public void setDocumentOwner(Person documentOwner) {
		this.documentOwner = documentOwner;
	}

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
