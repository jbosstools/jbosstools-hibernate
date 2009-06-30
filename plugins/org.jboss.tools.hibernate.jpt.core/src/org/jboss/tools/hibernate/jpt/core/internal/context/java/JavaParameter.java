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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.jboss.tools.hibernate.jpt.core.internal.context.Parameter;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.ParameterAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public interface JavaParameter extends Parameter, JavaJpaContextNode {

	void initialize(ParameterAnnotation parameterResource);
	
	/**
	 * Update the JavaParameter context model object to match the ParameterAnnotation 
	 * resource model object. see {@link org.eclipse.jpt.core.JpaProject#update()}
	 */
	void update(ParameterAnnotation parameterResource);
	
}
