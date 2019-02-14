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
import org.eclipse.jpt.jpa.core.context.BasicMapping;
import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.IdMapping;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.internal.details.java.GenericJavaUiFactory;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMapping;
import org.jboss.tools.hibernate.jpt.ui.internal.details.java.HibernateJavaEntityComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateBasicMappingComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateIdMappingComposite;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernateJavaUiFactory extends GenericJavaUiFactory {


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
			Composite parent, 
			WidgetFactory widgetFactory,
			ResourceManager resourceManager) {
		return new HibernateJavaEntityComposite((PropertyValueModel<? extends HibernateJavaEntity>) subjectHolder, parent, widgetFactory,resourceManager);
	}

	
	// FIXME: find out when this methos was used and where was it moved after refactoring
//	@SuppressWarnings("unchecked")
//	@Override
//	public JpaComposite createOrmEntityComposite(
//			PropertyValueModel<? extends Entity> subjectHolder, Composite parent,
//			WidgetFactory widgetFactory, ResourceManager resourceManager) {
//		return new HibernateOrmEntityComposite((PropertyValueModel<? extends Entity>) subjectHolder, parent, widgetFactory, resourceManager);
//	}
	@Override
	public JpaComposite createBasicMappingComposite(
			PropertyValueModel<? extends BasicMapping> mappingModel,
			PropertyValueModel<Boolean> enabledModel,
			Composite parentComposite,
			WidgetFactory widgetFactory,
			ResourceManager resourceManager) {
		return new HibernateBasicMappingComposite(
				mappingModel, 
				enabledModel,
				parentComposite, 
				widgetFactory,resourceManager);
	}
	
	

}
