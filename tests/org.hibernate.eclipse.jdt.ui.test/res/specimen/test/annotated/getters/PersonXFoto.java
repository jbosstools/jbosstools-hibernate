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
public class PersonXFoto {

	protected Integer ppersonId;

	protected FotoXPerson foto;
	
	protected Set<FotoXPerson> fotos = new HashSet<FotoXPerson>(0);

	@Id @GeneratedValue
	public Integer getPpersonId() {
		return this.ppersonId;
	}

	public void setPpersonId(Integer ppersonId) {
		this.ppersonId = ppersonId;
	}

	@ManyToOne
	public FotoXPerson getFoto() {
		return this.foto;
	}

	public void setFoto(FotoXPerson foto) {
		this.foto = foto;
	}

	@OneToMany(mappedBy = "person")
	public Set<FotoXPerson> getFotos() {
		return fotos;
	}

	public void setFotos(Set<FotoXPerson> fotos) {
		this.fotos = fotos;
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
