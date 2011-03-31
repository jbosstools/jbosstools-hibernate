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

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.orm.OrmBasicMapping;
import org.eclipse.jpt.jpa.core.context.orm.OrmEntity;
import org.eclipse.jpt.jpa.core.context.orm.OrmIdMapping;
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
	public JpaComposite createOrmBasicMappingComposite(
			PropertyValueModel<OrmBasicMapping> subjectHolder,
			Composite parent, WidgetFactory widgetFactory) {
		return new HibernateBasicMappingComposite(subjectHolder, parent,
				widgetFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JpaComposite createOrmIdMappingComposite(
			PropertyValueModel<OrmIdMapping> subjectHolder,
			Composite parent,
			WidgetFactory widgetFactory) {
		return new Hibernate2_0OrmIdMappingComposite((PropertyValueModel<? extends HibernateOrmIdMapping>) subjectHolder, parent, widgetFactory);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JpaComposite createOrmEntityComposite(
			PropertyValueModel<OrmEntity> subjectHolder, Composite parent,
			WidgetFactory widgetFactory) {
		return new Hibernate2_0OrmEntityComposite((PropertyValueModel<? extends HibernateOrmEntity>) subjectHolder, parent, widgetFactory);
	}


}
