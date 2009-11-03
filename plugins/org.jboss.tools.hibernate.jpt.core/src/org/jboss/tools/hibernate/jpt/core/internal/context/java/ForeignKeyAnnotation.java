/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public interface ForeignKeyAnnotation extends Annotation {
	
	String ANNOTATION_NAME = Hibernate.FOREIGN_KEY;
	
	String getName();
	void setName(String name);
		String NAME_PROPERTY = Hibernate.FOREIGN_KEY__NAME;
		
	String getInverseName();
	void setInverseName(String name);
		String INVERSE_NAME_PROPERTY = Hibernate.FOREIGN_KEY__INVERSE_NAME;
		
	TextRange getNameTextRange(CompilationUnit astRoot);	
	TextRange getInverseNameTextRange(CompilationUnit astRoot);

}