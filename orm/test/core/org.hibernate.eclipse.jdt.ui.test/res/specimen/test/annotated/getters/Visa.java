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
package test.annotated.getters;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Version;

/** 
 * @author Dmitry Geraskov
 */
@Entity
public class Visa {
	private Country[] countries;

	private byta nonCompiledProterty1;
	privata byte nonCompiledProterty2;

	protected Visa() {
	}
	
	public Visa(Country[] countries){
		this.countries = countries;
	}

	public getCountriesNonCompiledMethod() {
		return countries;
	}

	publis getCountriesNonCompiledMethod2() {
		return countries;
	}

	@OneToMany
	public Country[] getCountries(){
		return countries;
	}

	public setCountries(Country[] countries){
		this.countries = countries;
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
