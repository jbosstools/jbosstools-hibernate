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

package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.utility.TextRange;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public interface IndexAnnotation extends Annotation {
	
	String ANNOTATION_NAME = Hibernate.INDEX;
	
	String getName();
	void setName(String name);
		String NAME_PROPERTY = Hibernate.INDEX__NAME;
		
	String[] getColumnNames();
	void setColumnNames(String[] columnNames);
		String COLUMN_NAMES_PROPERTY = Hibernate.INDEX__COLUMN_NAMES;
		
	TextRange getNameTextRange(CompilationUnit astRoot);	
	TextRange getColumnNamesTextRange(CompilationUnit astRoot);

}
