/*******************************************************************************
 * Copyright (c) 2009-2012 Red Hat, Inc.
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

import org.eclipse.jpt.jpa.core.context.NamedNativeQuery;
import org.eclipse.jpt.jpa.core.context.java.JavaQueryContainer;
import org.eclipse.jpt.jpa.core.jpql.JpaJpqlQueryHelper;
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
	protected String fullyQualifiedResultClass;

	protected String resultSetMapping;

	private Boolean specifiedCallable;

	/**
	 * @param parent
	 */
	public HibernateNamedNativeQueryImpl(JavaQueryContainer parent, HibernateNamedNativeQueryAnnotation queryAnnotation) {
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

	@Override
	public void update() {
		super.update();
		this.setFullyQualifiedResultClass(this.buildFullyQualifiedResultClass());
	}
	// ********** metadata conversion *********
	@Override
	public void delete() {
		this.getParent().removeHibernateNamedNativeQuery(this);
	}

	// ********** result class **********
	@Override
	public String getResultClass() {
		return this.resultClass;
	}
	@Override
	public void setResultClass(String resultClass) {
		this.queryAnnotation.setResultClass(resultClass);
		this.setResultClass_(resultClass);
	}

	protected void setResultClass_(String resultClass) {
		String old = this.resultClass;
		this.resultClass = resultClass;
		this.firePropertyChanged(RESULT_CLASS_PROPERTY, old, resultClass);
	}
	@Override
	public char getResultClassEnclosingTypeSeparator() {
		return '.';
	}
	@Override
	public String getFullyQualifiedResultClass() {
		return this.fullyQualifiedResultClass;
	}
	
	protected void setFullyQualifiedResultClass(String resultClass) {
		String old = this.fullyQualifiedResultClass;
		this.fullyQualifiedResultClass = resultClass;
		this.firePropertyChanged(FULLY_QUALIFIED_RESULT_CLASS_PROPERTY, old, resultClass);
	}

	protected String buildFullyQualifiedResultClass() {
		return this.queryAnnotation.getFullyQualifiedResultClassName();
	}

	// ********** result set mapping **********
	@Override
	public String getResultSetMapping() {
		return this.resultSetMapping;
	}
	@Override
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
	@Override
	public boolean isCallable(){
		return (getSpecifiedCallable() == null ? isDefaultCallable()
				: getSpecifiedCallable().booleanValue());
	}
	@Override
	public Boolean getSpecifiedCallable(){
		return this.specifiedCallable;
	}
	@Override
	public void setSpecifiedCallable(Boolean newSpecifiedCallable){
		Boolean oldSpecifiedCallable = this.specifiedCallable;
		this.specifiedCallable = newSpecifiedCallable;
		this.getQueryAnnotation().setCallable(newSpecifiedCallable);
		firePropertyChanged(SPECIFIED_CALLABLE_PROPERTY, oldSpecifiedCallable, newSpecifiedCallable);
	}

	protected void setSpecifiedCallable_(Boolean callable){
		Boolean oldSpecifiedCallable = this.specifiedCallable;
		this.specifiedCallable = callable;
		firePropertyChanged(SPECIFIED_CALLABLE_PROPERTY, oldSpecifiedCallable, callable);
	}

	@Override
	public boolean isDefaultCallable(){
		return HibernateNamedNativeQuery.DEFAULT_CALLABLE;
	}
	
	// ********** validation **********

	@Override
	public void validate(JpaJpqlQueryHelper queryHelper, List<IMessage> messages, IReporter reporter) {
		// nothing yet
	}
	
	// ********** misc **********
	@Override
	public Class<NamedNativeQuery> getQueryType() {
//		return HibernateNamedNativeQuery.class;
		return NamedNativeQuery.class;
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setQuery(String query) {
		// TODO Auto-generated method stub
		
	}

}
