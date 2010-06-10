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

import org.eclipse.jpt.core.context.orm.OrmBasicMapping;
import org.eclipse.jpt.core.context.orm.OrmIdMapping;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.details.JpaComposite;
import org.eclipse.jpt.ui.internal.details.orm.BaseOrmXmlUiFactory;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmIdMapping;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateBasicMappingComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateIdMappingComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateOrmXmlUiFactory extends BaseOrmXmlUiFactory {
	
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
		return new HibernateIdMappingComposite((PropertyValueModel<? extends HibernateOrmIdMapping>) subjectHolder, parent, widgetFactory);
	}

}
