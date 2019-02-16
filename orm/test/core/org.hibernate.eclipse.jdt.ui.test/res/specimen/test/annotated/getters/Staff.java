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

import javax.persistence.Entity;
import javax.persistence.Version;

@Entity
public class Staff extends Document {
	
	private Long code;
	
	protected Staff() {
	}
	
	public Staff(long code) {
		this.code = code;
		setDocType((byte)2);
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
