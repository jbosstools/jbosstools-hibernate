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

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Version;

/** 
 * @author Dmitry Geraskov
 */
@Entity
public class Visa {
	@OneToMany(mappedBy="visa")
	private Country[] countries;

	protected Visa() {
	}
	
	public Visa(Country[] countries) {
		this.countries = countries;
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
