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
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.internal.details.java.BaseJavaUiFactory;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmEntity;
import org.jboss.tools.hibernate.jpt.ui.internal.details.java.HibernateJavaEntityComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.details.orm.HibernateOrmEntityComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateBasicMappingComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateIdMappingComposite;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernateJavaUiFactory extends BaseJavaUiFactory {


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
		return new HibernateOrmEntityComposite((PropertyValueModel<? extends HibernateOrmEntity>) subjectHolder, parent, widgetFactory);
	}

	public JpaComposite createJavaBasicMappingComposite(
			PropertyValueModel<JavaBasicMapping> subjectHolder,
			Composite parent,
			WidgetFactory widgetFactory) {
		return new HibernateBasicMappingComposite(subjectHolder, parent, widgetFactory);
	}
	
	

}
