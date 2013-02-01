/*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.jpa2.mapping.details.orm;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.BasicMapping;
import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.IdMapping;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.internal.jpa2.GenericOrmXml2_0UiFactory;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmIdMapping;
import org.jboss.tools.hibernate.jpt.ui.internal.jpa2.details.orm.Hibernate2_0OrmEntityComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.jpa2.details.orm.Hibernate2_0OrmIdMappingComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateBasicMappingComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class Hibernate2_0OrmXmlUiFactory extends GenericOrmXml2_0UiFactory {
	
	@Override
	public JpaComposite createBasicMappingComposite(
			PropertyValueModel<? extends BasicMapping> subjectHolder,
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
	
	@SuppressWarnings("unchecked")
	@Override
	public JpaComposite createIdMappingComposite(
			PropertyValueModel<? extends IdMapping> subjectHolder,
			PropertyValueModel<Boolean> enabledModel,
			Composite parent,
			WidgetFactory widgetFactory,
			ResourceManager resourceManager) {
		return new Hibernate2_0OrmIdMappingComposite(
				(PropertyValueModel<? extends HibernateOrmIdMapping>) subjectHolder,
				enabledModel,
				parent, 
				widgetFactory,
				resourceManager);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JpaComposite createEntityComposite(
			PropertyValueModel<? extends Entity> subjectHolder, Composite parent,
			WidgetFactory widgetFactory,
			ResourceManager resourceManager) {
		return new Hibernate2_0OrmEntityComposite((PropertyValueModel<? extends HibernateOrmEntity>) subjectHolder, parent, widgetFactory, resourceManager);
	}


}
