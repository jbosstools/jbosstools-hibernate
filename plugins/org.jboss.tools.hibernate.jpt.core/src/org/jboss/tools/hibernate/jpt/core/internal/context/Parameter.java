/*******************************************************************************
 * Copyright (c) 2009-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.jpa.core.context.JpaContextModel;

/**
 * @author Dmitry Geraskov
 *
 */
public interface Parameter extends JpaContextModel {
	
	String getName();
	void setName(String value);
		String NAME_PROPERTY = "name"; //$NON-NLS-1$

	String getValue();
	void setValue(String value);
		String VALUE_PROPERTY = "value"; //$NON-NLS-1$

}
