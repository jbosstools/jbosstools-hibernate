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
import org.eclipse.jpt.common.core.resource.java.Annotation;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.context.Converter;
import org.eclipse.jpt.jpa.core.context.java.JavaAttributeMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaConverter;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.AbstractJavaConverter;
import org.jboss.tools.hibernate.jpt.core.internal.context.TypeConverter;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.TypeAnnotation;

/**
 * 
 * @author Dmitry Geraskov
 *
 */
public class JavaTypeConverterImpl extends AbstractJavaConverter implements JavaTypeConverter {

	protected final TypeAnnotation typeAnnotation;
	
	protected String type;

	public JavaTypeConverterImpl(JavaAttributeMapping parent, TypeAnnotation typeAnnotation, JavaConverter.ParentAdapter owner) {
		super(owner);
		this.typeAnnotation = typeAnnotation;
		type = buildHibernateType();
	}
	
	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.setHibernateType_(this.buildHibernateType());
	}
	
	protected String buildHibernateType() {
		return typeAnnotation.getType();
	}

	// ********** misc **********

	public Class<? extends Converter> getType() {
		return TypeConverter.class;
	}
	
	@Override
	public Annotation getConverterAnnotation() {
		return typeAnnotation;
	}

	// ********** type **********

	@Override
	protected TextRange getAnnotationTextRange() {
		return this.typeAnnotation.getTextRange();
	}

	@Override
	public String getHibernateType() {
		return type;
	}

	@Override
	public void setHibernateType(String type) {
		this.typeAnnotation.setType(type);
		setHibernateType_(type);
	}
	
	public void setHibernateType_(String type) {
		String old = this.type;
		this.type = type;
		this.firePropertyChanged(TYPE_PROPERTY, old, type);
	}

}
