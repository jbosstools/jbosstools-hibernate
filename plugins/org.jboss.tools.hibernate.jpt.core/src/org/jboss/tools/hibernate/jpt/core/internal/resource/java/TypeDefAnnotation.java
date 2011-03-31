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
package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import java.util.ListIterator;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.resource.java.NestableAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;

/**
 * @author Dmitry Geraskov
 *
 */
public interface TypeDefAnnotation extends NestableAnnotation {
	
	String ANNOTATION_NAME = Hibernate.TYPE_DEF;
	
	/**
	 * Corresponds to the 'name' element of the *TypeDef annotation.
	 * Return null if the element does not exist in Java.
	 */
	String getName();
		String NAME_PROPERTY = "name"; //$NON-NLS-1$

	/**
	 * Corresponds to the 'name' element of the *TypeDef annotation.
	 * Set to null to remove the element. If no other elements exist
	 * the *Generator annotation will be removed as well.
	 */
	void setName(String name);

	/**
	 * Return the {@link TextRange} for the 'name' element. If the element 
	 * does not exist return the {@link TextRange} for the *TypeDef annotation.
	 */
	TextRange getNameTextRange(CompilationUnit astRoot);

	/**
	 * Corresponds to the 'defaultForType' element of the *TypeDef annotation.
	 * Return null if the element does not exist in Java.
	 * Return the portion of the value preceding ".class".
	 * <pre>
	 *     &#64;TypeDef(defaultForType=Employee.class)
	 * </pre>
	 * will return "Employee"
	 */
	String getDefaultForType();	
		String DEF_FOR_TYPE_PROPERTY = "defaultForType"; //$NON-NLS-1$
	
	/**
	 * Corresponds to the 'defaultForType' element of the *TypeDef annotation.
	 * Set to null to remove the element.
	 */
	void setDefaultForType(String defaultForType);
	
	/**
	 * Return the {@link TextRange} for the 'defaultForType' element. If the element 
	 * does not exist return the {@link TextRange} for the element collection annotation.
	 */
	TextRange getDefaultForTypeTextRange(CompilationUnit astRoot);
	
	/**
	 * Return the fully-qualified default for type class name as resolved by the AST's bindings.
	 * <pre>
	 *     &#64;TypeDef(targetClass=Employee.class)
	 * </pre>
	 * will return "model.Employee" if there is an import for model.Employee.
	 * @return
	 */
	String getFullyQualifiedDefaultForTypeClassName();
		String FULLY_QUALIFIED_DEFAULT_FOR_TYPE_CLASS_NAME_PROPERTY = "fullyQualifiedDefaultForTypeClassName"; //$NON-NLS-1$

	/**
	 * Corresponds to the 'typeClass' element of the *TypeDef annotation.
	 * Return null if the element does not exist in Java.
	 * Return the portion of the value preceding ".class".
	 * <pre>
	 *     &#64;TypeDef(typeClass=Employee.class)
	 * </pre>
	 * will return "Employee"
	 */
	String getTypeClass();	
		String TYPE_CLASS_PROPERTY = "typeClass"; //$NON-NLS-1$
	
	/**
	 * Corresponds to the 'typeClass' element of the *TypeDef annotation.
	 * Set to null to remove the element.
	 */
	void setTypeClass(String typeClass);
	
	/**
	 * Return the {@link TextRange} for the 'typeClass' element. If the element 
	 * does not exist return the {@link TextRange} for the element collection annotation.
	 */
	TextRange getTypeClassTextRange(CompilationUnit astRoot);
	

	/**
	 * Return the fully-qualified type class name as resolved by the AST's bindings.
	 * <pre>
	 *     &#64;TypeDef(targetClass=Employee.class)
	 * </pre>
	 * will return "model.Employee" if there is an import for model.Employee.
	 * @return
	 */
	String getFullyQualifiedTypeClassName();
		String FULLY_QUALIFIED_TYPE_CLASS_NAME_PROPERTY = "fullyQualifiedTypeClassName"; //$NON-NLS-1$

	
	/**
	 * Return an empty iterator if the element does not exist in Java.
	 */
	ListIterator<ParameterAnnotation> parameters();
		String PARAMETERS_LIST = "parameters"; //$NON-NLS-1$
	
	/**
	 * Corresponds to the 'parameters' element of the *TypeDef annotation.
	 */
	int parametersSize();

	/**
	 * Corresponds to the 'parameters' element of the *TypeDef annotation.
	 */
	ParameterAnnotation parameterAt(int index);
	
	/**
	 * Corresponds to the 'parameters' element of the *TypeDef annotation.
	 */
	int indexOfParameter(ParameterAnnotation parameter);
	
	/**
	 * Corresponds to the 'parameters' element of the *TypeDef annotation.
	 */
	ParameterAnnotation addParameter(int index);
	
	/**
	 * Corresponds to the 'parameters' element of the *TypeDef annotation.
	 */
	void moveParameter(int targetIndex, int sourceIndex);

	/**
	 * Corresponds to the 'parameters' element of the *TypeDef annotation.
	 */
	void removeParameter(int index);	

}
