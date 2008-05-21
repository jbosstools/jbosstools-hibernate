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
package org.jboss.tools.hibernate.core;

/**
 * @author alex
 *
 * An abstract view on a field mapping.
 */
public interface IPersistentFieldMapping extends IOrmElement {
	public IPersistentField getPersistentField();
	public void setPersistentField(IPersistentField field);
	public IPersistentValueMapping getPersistentValueMapping();
	//akuzmin 21.05.2005
	public void setPersistentValueMapping(IPersistentValueMapping valueMapping);
	public void clear();

}
