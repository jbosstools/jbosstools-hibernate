/*******************************************************************************
 * Copyright (c) 2008-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.java;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.java.JavaBasicMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaEntity;
import org.eclipse.jpt.jpa.core.context.java.JavaIdMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmEntity;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaElementCollectionMapping2_0;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.java.Generic2_0JavaUiFactory;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmEntity;
import org.jboss.tools.hibernate.jpt.ui.internal.details.java.HibernateJavaEntityComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.jpa2.details.HibernateElementCollectionMapping2_0Composite;
import org.jboss.tools.hibernate.jpt.ui.internal.jpa2.details.orm.Hibernate2_0OrmEntityComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateBasicMappingComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateIdMappingComposite;

/**
 * @author Dmitry Geraskov
 * 
 */
public class Hibernate2_0JavaUiFactory extends Generic2_0JavaUiFactory {


	@SuppressWarnings("unchecked")
	@Override
	public JpaComposite createJavaIdMappingComposite(
			PropertyValueModel<JavaIdMapping> subjectHolder,
			Composite parent,
			WidgetFactory widgetFactory) {
		return new HibernateIdMappingComposite((PropertyValueModel<? extends HibernateJavaIdMapping>) subjectHolder, parent, widgetFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JpaComposite createJavaEntityComposite(PropertyValueModel<JavaEntity> subjectHolder,
			Composite parent, WidgetFactory widgetFactory) {
		return new HibernateJavaEntityComposite((PropertyValueModel<? extends HibernateJavaEntity>) subjectHolder, parent, widgetFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JpaComposite createOrmEntityComposite(
			PropertyValueModel<OrmEntity> subjectHolder, Composite parent,
			WidgetFactory widgetFactory) {
		return new Hibernate2_0OrmEntityComposite((PropertyValueModel<? extends HibernateOrmEntity>) subjectHolder, parent, widgetFactory);
	}

	public JpaComposite createJavaBasicMappingComposite(
			PropertyValueModel<JavaBasicMapping> subjectHolder,
			Composite parent,
			WidgetFactory widgetFactory) {
		return new HibernateBasicMappingComposite(subjectHolder, parent, widgetFactory);
	}
	
	public JpaComposite createJavaElementCollectionMapping2_0Composite(
			PropertyValueModel<JavaElementCollectionMapping2_0> subjectHolder,
			Composite parent,
			WidgetFactory widgetFactory) {
		return new HibernateElementCollectionMapping2_0Composite(subjectHolder, parent, widgetFactory);
	}

}
