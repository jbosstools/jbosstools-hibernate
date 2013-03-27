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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.context.JpaContextModel;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public interface JavaType extends JpaContextModel {
	
	String getType();
	void setType(String name);
		String TYPE_TYPE = "type"; //$NON-NLS-1$
	
	public TextRange getTypeTextRange();
	TypeAnnotation getTypeAnnotation();

}
