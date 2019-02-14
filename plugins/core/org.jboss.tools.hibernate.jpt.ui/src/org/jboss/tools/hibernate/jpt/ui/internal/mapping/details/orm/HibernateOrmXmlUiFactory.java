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
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.orm;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.BasicMapping;
import org.eclipse.jpt.jpa.core.context.Entity;
import org.eclipse.jpt.jpa.core.context.IdMapping;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.internal.details.orm.GenericOrmXmlUiFactory;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmIdMapping;
import org.jboss.tools.hibernate.jpt.ui.internal.details.orm.HibernateOrmEntityComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.details.orm.HibernateOrmIdMappingComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateBasicMappingComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmXmlUiFactory extends GenericOrmXmlUiFactory {
	
	@Override
	public JpaComposite createBasicMappingComposite(
			PropertyValueModel<? extends BasicMapping> subjectHolder,
			PropertyValueModel<Boolean> enabledModel,
			Composite parent, WidgetFactory widgetFactory,
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
		return new HibernateOrmIdMappingComposite(
				(PropertyValueModel<? extends HibernateOrmIdMapping>) subjectHolder, 
				enabledModel,
				parent, 
				widgetFactory,
				resourceManager);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JpaComposite createEntityComposite(
			PropertyValueModel<? extends Entity> subjectHolder, 
			Composite parent,
			WidgetFactory widgetFactory,
			ResourceManager resourceManager) {
		return new HibernateOrmEntityComposite((PropertyValueModel<? extends HibernateOrmEntity>) subjectHolder, parent, widgetFactory,resourceManager);
	}

}
