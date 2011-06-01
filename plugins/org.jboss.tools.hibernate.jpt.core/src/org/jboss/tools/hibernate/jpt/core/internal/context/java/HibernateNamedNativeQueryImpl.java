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
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.jpa.core.context.java.JavaJpaContextNode;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueryAnnotation;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateNamedNativeQueryImpl extends AbstractHibernateNamedQueryImpl<HibernateNamedNativeQueryAnnotation>
	implements HibernateJavaNamedNativeQuery {

	protected String resultClass;

	protected String resultSetMapping;

	private Boolean specifiedCallable;

	/**
	 * @param parent
	 */
	public HibernateNamedNativeQueryImpl(JavaJpaContextNode parent, HibernateNamedNativeQueryAnnotation queryAnnotation) {
		super(parent, queryAnnotation);
		this.resultClass = queryAnnotation.getResultClass();
		this.resultSetMapping = queryAnnotation.getResultSetMapping();
		this.specifiedCallable = queryAnnotation.isCallable();
	}

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.setResultClass_(this.queryAnnotation.getResultClass());
		this.setResultSetMapping_(this.queryAnnotation.getResultSetMapping());
		this.setSpecifiedReadOnly_(this.queryAnnotation.isCallable());
	}

	// ********** result class **********

	public String getResultClass() {
		return this.resultClass;
	}

	public void setResultClass(String resultClass) {
		this.queryAnnotation.setResultClass(resultClass);
		this.setResultClass_(resultClass);
	}

	protected void setResultClass_(String resultClass) {
		String old = this.resultClass;
		this.resultClass = resultClass;
		this.firePropertyChanged(RESULT_CLASS_PROPERTY, old, resultClass);
	}

	public char getResultClassEnclosingTypeSeparator() {
		return '.';
	}


	// ********** result set mapping **********

	public String getResultSetMapping() {
		return this.resultSetMapping;
	}

	public void setResultSetMapping(String resultSetMapping) {
		this.queryAnnotation.setResultSetMapping(resultSetMapping);
		this.setResultSetMapping_(resultSetMapping);
	}

	protected void setResultSetMapping_(String resultSetMapping) {
		String old = this.resultSetMapping;
		this.resultSetMapping = resultSetMapping;
		this.firePropertyChanged(RESULT_SET_MAPPING_PROPERTY, old, resultSetMapping);
	}

	//************************ callable *********************************
	public boolean isCallable(){
		return (getSpecifiedCallable() == null ? isDefaultCallable()
				: getSpecifiedCallable().booleanValue());
	}

	public Boolean getSpecifiedCallable(){
		return this.specifiedCallable;
	}

	public void setSpecifiedCallable(Boolean newSpecifiedCallable){
		Boolean oldSpecifiedCallable = this.specifiedCallable;
		this.specifiedCallable = newSpecifiedCallable;
		this.getQueryAnnotation().setCallable(newSpecifiedCallable);
		firePropertyChanged(SPECIFIED_CALLABLE_PROPERTY, oldSpecifiedCallable, newSpecifiedCallable);
	}

	public void setSpecifiedCallable_(Boolean callable){
		Boolean oldSpecifiedCallable = this.specifiedCallable;
		this.specifiedCallable = callable;
		firePropertyChanged(SPECIFIED_CALLABLE_PROPERTY, oldSpecifiedCallable, callable);
	}

	public boolean isDefaultCallable(){
		return HibernateNamedNativeQuery.DEFAULT_CALLABLE;
	}
	
	// ********** validation **********

	@Override
	protected void validateQuery_(List<IMessage> messages, IReporter reporter, CompilationUnit astRoot) {
		// nothing yet
	}

}
