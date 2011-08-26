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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.jboss.tools.hibernate.jpt.core.internal.context.ParametrizedElement;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public interface JavaTypeDef extends JavaJpaContextNode, ParametrizedElement {
	
	String USER_TYPE_INTERFACE = "org.hibernate.usertype.UserType";
	String TYPE_INTERFACE = "org.hibernate.type.Type";
	String COMPOSITE_USER_TYPE_INTERFACE = "org.hibernate.usertype.CompositeUserType";
	String USER_COLLECTION_USER_TYPE_INTERFACE = "org.hibernate.usertype.UserCollectionType";
	
	String[] POSSIBLE_INTERFACES = new String[]{
			TYPE_INTERFACE,
			USER_TYPE_INTERFACE,
			COMPOSITE_USER_TYPE_INTERFACE,
			USER_COLLECTION_USER_TYPE_INTERFACE
	};
	
	String getName();
	void setName(String name);
		String NAME_PROPERTY = "name"; //$NON-NLS-1$
	
	// **************** defaultForType class **************************************
	
	String getDefaultForTypeClass();

	void setDefaultForTypeClass(String value);
		String DEF_FOR_TYPE_PROPERTY = "defaultForTypeClass"; //$NON-NLS-1$

	// **************** type class **************************************
	
	String getTypeClass();

	void setTypeClass(String value);
		String TYPE_CLASS_PROPERTY = "specifiedTypeClass"; //$NON-NLS-1$
		
	char getTypeClassEnclosingTypeSeparator();
		

	// **************** validation *********************************************
	
	TextRange getNameTextRange(CompilationUnit astRoot);
	
	TypeDefAnnotation getTypeDefAnnotation();

}
