/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core;

import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;

/**
 * @author alex
 *
 * Base abstract class for Hibernate property mapping types
 */
public abstract class AbstractFieldMapping extends AbstractOrmElement implements
		IPersistentFieldMapping {

	private IPersistentField field;
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentFieldMapping#getPersistentField()
	 */
	public IPersistentField getPersistentField() {
		
		return field;
	}

	/**
	 * @param field The field to set.
	 */
	public void setPersistentField(IPersistentField field) {
		this.field = field;
	}
	public void clear(){
		field.setMapping(null);
	}
}
