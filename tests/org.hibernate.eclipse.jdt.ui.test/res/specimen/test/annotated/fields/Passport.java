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

import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity
public class Passport extends Document {

	protected Long fakeId;
	@OneToMany
	private Map<Visa, String> visas;
	
	public Passport() {
		setDocType((byte)1);
	}
	
	public void setVisas(Map<Visa, String> visas){
		this.visas = visas;
	}
	
	public Map<Visa, String> getVisas(){
		return visas;
	}

	@Version 
	protected Integer version;
	
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}

}
