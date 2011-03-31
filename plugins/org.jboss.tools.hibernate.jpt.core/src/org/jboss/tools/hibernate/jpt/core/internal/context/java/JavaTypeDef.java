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

import java.util.ListIterator;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.jboss.tools.hibernate.jpt.core.internal.context.Parameter;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeDefAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public interface JavaTypeDef extends JavaJpaContextNode {
	
	String getName();
	void setName(String name);
		String TYPE_DEF_NAME = "name"; //$NON-NLS-1$
	
	// **************** defaultForType class **************************************
	
	String getDefaultForType();

	String getSpecifiedDefaultForType();
	void setSpecifiedDefaultForType(String value);
		String SPECIFIED_DEF_FOR_TYPE_PROPERTY = "specifiedDefaultForTypeClass"; //$NON-NLS-1$

	String getDefaultDefaultForType();
		String DEFAULT_DEF_FOR_TYPE_PROPERTY = "defaultDefaultForTypeClass"; //$NON-NLS-1$

	/**
	 * If the DefaultForType class is specified, this will return it fully qualified. If not
	 * specified, it returns the default for type class, which is always fully qualified
	 */
	String getFullyQualifiedDefaultForType();
		String FULLY_QUALIFIED_DEF_FOR_TYPE_PROPERTY = "fullyQualifiedDefaultForTypeClass"; //$NON-NLS-1$
	
	
	// **************** type class **************************************
	
	String getTypeClass();

	String getSpecifiedTypeClass();
	void setSpecifiedTypeClass(String value);
		String SPECIFIED_TYPE_CLASS_PROPERTY = "specifiedTypeClass"; //$NON-NLS-1$

	String getDefaultTypeClass();
		String DEFAULT_TYPE_CLASS_PROPERTY = "defaultTypeClass"; //$NON-NLS-1$

	/**
	 * If the type class is specified, this will return it fully qualified. If not
	 * specified, it returns the default type class, which is always fully qualified
	 */
	String getFullyQualifiedTypeClass();
		String FULLY_QUALIFIED_TYPE_CLASS_PROPERTY = "fullyQualifiedTypeClass"; //$NON-NLS-1$
		
	//************************ parameters ***********************
	
	String PARAMETERS_LIST = "parameters"; //$NON-NLS-1$
	
	/**
	 * Return a list iterator of the parameters.  This will not be null.
	 */
	<T extends Parameter> ListIterator<T> parameters();
	
	/**
	 * Return the number of parameters.
	 */
	int parametersSize();
	
	/**
	 * Add a parameter to the generator and return the object representing it.
	 */
	Parameter addParameter(int index);
	
	/**
	 * Remove the parameter from the generator.
	 */
	void removeParameter(int index);
	
	/**
	 * Remove the parameter at the index from the query.
	 */
	void removeParameter(Parameter queryParameter);
	
	/**
	 * Move the parameter from the source index to the target index.
	 */
	void moveParameter(int targetIndex, int sourceIndex);
	
	// **************** validation *********************************************
	
	TextRange getNameTextRange(CompilationUnit astRoot);
	
	void initialize(TypeDefAnnotation generator);
	
	void update(TypeDefAnnotation generator);

}
