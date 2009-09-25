/*******************************************************************************
 * Copyright (c) 2008-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jpt.core.context.java.JavaBasicMapping;
import org.eclipse.jpt.core.context.java.JavaEntity;
import org.eclipse.jpt.core.context.java.JavaIdMapping;
import org.eclipse.jpt.core.context.orm.OrmBasicMapping;
import org.eclipse.jpt.core.context.orm.OrmEntity;
import org.eclipse.jpt.core.context.orm.OrmIdMapping;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.details.JpaComposite;
import org.eclipse.jpt.ui.details.JpaPageComposite;
import org.eclipse.jpt.ui.internal.BaseJpaUiFactory;
import org.eclipse.jpt.ui.internal.persistence.details.GenericPersistenceUnitGeneralComposite;
import org.eclipse.jpt.ui.internal.persistence.details.PersistenceUnitConnectionComposite;
import org.eclipse.jpt.ui.internal.persistence.details.PersistenceUnitPropertiesComposite;
import org.eclipse.jpt.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmIdMapping;
import org.jboss.tools.hibernate.jpt.ui.internal.java.details.HibernateJavaEntityComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateBasicMappingComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateIdMappingComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.orm.details.HibernateOrmEntityComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.persistence.details.HibernatePropertiesComposite;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernateJpaUiFactory extends BaseJpaUiFactory {

	@SuppressWarnings("unchecked")
	public ListIterator createPersistenceUnitComposites(
			PropertyValueModel<PersistenceUnit> subjectHolder, Composite parent, WidgetFactory widgetFactory) {

		List<JpaPageComposite> pages = new ArrayList<JpaPageComposite>(1);

		pages.add(new GenericPersistenceUnitGeneralComposite(subjectHolder, parent, widgetFactory));
		pages.add(new PersistenceUnitConnectionComposite(subjectHolder, parent, widgetFactory));
		pages.add(new PersistenceUnitPropertiesComposite(subjectHolder, parent, widgetFactory));
		
		// ************Hibernate pages***************
		PropertyValueModel<HibernatePersistenceUnit> hibernatePersistenceUnitHolder = this
			.buildHibernatePersistenceUnitHolder(subjectHolder);

		PropertyValueModel<BasicHibernateProperties> basicHolder = this.buildBasicHolder(hibernatePersistenceUnitHolder);
		pages.add(new HibernatePropertiesComposite(basicHolder, parent, widgetFactory));

		return pages.listIterator();
	}
	
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

	private PropertyValueModel<BasicHibernateProperties> buildBasicHolder(
			PropertyValueModel<HibernatePersistenceUnit> subjectHolder) {
		return new TransformationPropertyValueModel<HibernatePersistenceUnit, BasicHibernateProperties>(subjectHolder) {
			@Override
			protected BasicHibernateProperties transform_(HibernatePersistenceUnit value) {
				return value.getBasicProperties();
			}
		};
	}

	private PropertyValueModel<HibernatePersistenceUnit> buildHibernatePersistenceUnitHolder(
			PropertyValueModel<PersistenceUnit> subjectHolder) {
		return new TransformationPropertyValueModel<PersistenceUnit, HibernatePersistenceUnit>(subjectHolder) {
			@Override
			protected HibernatePersistenceUnit transform_(PersistenceUnit value) {
				return (HibernatePersistenceUnit) value;
			}
		};
	}
	
	public JpaComposite createJavaBasicMappingComposite(
			PropertyValueModel<JavaBasicMapping> subjectHolder,
			Composite parent,
			WidgetFactory widgetFactory) {
		return new HibernateBasicMappingComposite(subjectHolder, parent, widgetFactory);
	}
	
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