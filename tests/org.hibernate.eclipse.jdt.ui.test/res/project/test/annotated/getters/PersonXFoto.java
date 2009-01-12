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

public class PersonXFoto {

	protected Integer ppersonId;

	protected FotoXPerson foto;
	
	protected Set<FotoXPerson> fotos = new HashSet<FotoXPerson>(0);

	public Integer getPpersonId() {
		return this.ppersonId;
	}

	public void setPpersonId(Integer ppersonId) {
		this.ppersonId = ppersonId;
	}

	public FotoXPerson getFoto() {
		return this.foto;
	}

	public void setFoto(FotoXPerson foto) {
		this.foto = foto;
	}

	public Set<FotoXPerson> getFotos() {
		return fotos;
	}

	public void setFotos(Set<FotoXPerson> fotos) {
		this.fotos = fotos;
	}
}
