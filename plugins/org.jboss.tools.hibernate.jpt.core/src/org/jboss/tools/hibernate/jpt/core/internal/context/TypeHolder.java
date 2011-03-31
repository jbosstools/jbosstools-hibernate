/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.jpa.core.context.JpaContextNode;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaType;

/**
 * @author Dmitry Geraskov
 *
 */
public interface TypeHolder extends JpaContextNode {
	
	//******************** index *****************

	String TYPE_PROPERTY = "type"; //$NON-NLS-1$
	
	JavaType getType();
	
	JavaType addType();
	
	void removeType();

}
