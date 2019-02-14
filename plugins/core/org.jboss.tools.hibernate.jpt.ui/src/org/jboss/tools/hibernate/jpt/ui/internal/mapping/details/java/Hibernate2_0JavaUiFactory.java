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

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.IdMapping;
import org.eclipse.jpt.jpa.core.context.java.JavaBasicMapping;
import org.eclipse.jpt.jpa.core.jpa2.context.java.JavaElementCollectionMapping2_0;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.java.GenericJavaUiFactory2_0;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMapping;
import org.jboss.tools.hibernate.jpt.ui.internal.jpa2.details.HibernateElementCollectionMapping2_0Composite;
import org.jboss.tools.hibernate.jpt.ui.internal.jpa2.details.java.HibernateJavaEntity2_0Composite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateBasicMappingComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateIdMappingComposite;

/**
 * @author Dmitry Geraskov
 * 
 */
public class Hibernate2_0JavaUiFactory extends GenericJavaUiFactory2_0 {


	@SuppressWarnings("unchecked")
	@Override
	public JpaComposite createIdMappingComposite(
			PropertyValueModel<? extends IdMapping> subjectHolder,
			PropertyValueModel<Boolean> enabledModel,
			Composite parent,
			WidgetFactory widgetFactory,
			ResourceManager resourceManager) {
		return new HibernateIdMappingComposite(
				(PropertyValueModel<? extends HibernateJavaIdMapping>) subjectHolder,
				enabledModel,
				parent, 
				widgetFactory,
				resourceManager);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JpaComposite createEntityComposite(PropertyValueModel<? extends Entity> subjectHolder,
			Composite parent, WidgetFactory widgetFactory, ResourceManager resourceManager) {
		return new HibernateJavaEntity2_0Composite((PropertyValueModel<? extends HibernateJavaEntity>) subjectHolder, parent, widgetFactory,resourceManager);
	}
	
// FIXME: IPA Kepler M5 migration unsolved problems
//	@SuppressWarnings("unchecked")
//	@Override
//	public JpaComposite createOrmEntityComposite(
//			PropertyValueModel<OrmEntity> subjectHolder, Composite parent,
//			WidgetFactory widgetFactory) {
//		return new Hibernate2_0OrmEntityComposite((PropertyValueModel<? extends HibernateOrmEntity>) subjectHolder, parent, widgetFactory);
//	}

	public JpaComposite createJavaBasicMappingComposite(
			PropertyValueModel<JavaBasicMapping> subjectHolder,
			PropertyValueModel<Boolean> enabledModel,
			Composite parent,
			WidgetFactory widgetFactory,
			ResourceManager resourceManager) {
		return new HibernateBasicMappingComposite(
				subjectHolder, 
				enabledModel,
				parent, 
				widgetFactory,
				resourceManager);
	}
	
	public JpaComposite createJavaElementCollectionMapping2_0Composite(
			PropertyValueModel<JavaElementCollectionMapping2_0> subjectHolder,
			PropertyValueModel<Boolean> enabledModel,
			Composite parent,
			WidgetFactory widgetFactory,
			ResourceManager resourceManager) {
		return new HibernateElementCollectionMapping2_0Composite(
				subjectHolder, 
				enabledModel,
				parent, 
				widgetFactory,
				resourceManager);
	}

}
