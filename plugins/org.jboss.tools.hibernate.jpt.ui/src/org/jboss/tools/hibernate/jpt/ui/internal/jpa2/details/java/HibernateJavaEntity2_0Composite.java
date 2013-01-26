/*******************************************************************************
 * Copyright (c) 2009-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.jpa2.details.java;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.AccessHolder;
import org.eclipse.jpt.jpa.core.context.java.JavaEntity;
import org.eclipse.jpt.jpa.core.context.orm.OrmTypeMapping;
import org.eclipse.jpt.jpa.core.jpa2.context.Cacheable2_0;
import org.eclipse.jpt.jpa.core.jpa2.context.CacheableHolder2_0;
import org.eclipse.jpt.jpa.ui.internal.JptUiMessages;
import org.eclipse.jpt.jpa.ui.internal.details.AbstractEntityComposite;
import org.eclipse.jpt.jpa.ui.internal.details.AccessTypeComboViewer;
import org.eclipse.jpt.jpa.ui.internal.details.EntityNameCombo;
import org.eclipse.jpt.jpa.ui.internal.details.IdClassChooser;
import org.eclipse.jpt.jpa.ui.internal.details.JptUiDetailsMessages;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaSecondaryTablesComposite;
import org.eclipse.jpt.jpa.ui.internal.details.orm.JptUiDetailsOrmMessages;
import org.eclipse.jpt.jpa.ui.internal.details.orm.MetadataCompleteTriStateCheckBox;
import org.eclipse.jpt.jpa.ui.internal.details.orm.OrmJavaClassChooser;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.Cacheable2_0TriStateCheckBox;
import org.eclipse.jpt.jpa.ui.internal.jpa2.details.Entity2_0OverridesComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTypeDefContainer;
import org.jboss.tools.hibernate.jpt.ui.internal.details.HibernateTableComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.details.java.HibernateJavaInheritanceComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateGenerationComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.TypeDefsComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaEntity2_0Composite extends AbstractEntityComposite<HibernateJavaEntity> {

	/**
	 * @param subjectHolder
	 * @param parent
	 * @param widgetFactory
	 */
	public HibernateJavaEntity2_0Composite(PropertyValueModel<? extends HibernateJavaEntity> subjectHolder,
			Composite parent, WidgetFactory widgetFactory) {
		super(subjectHolder, parent, widgetFactory);
	}
	
	@Override
	protected void initializeLayout(Composite container) {
		super.initializeLayout(container);
		this.initializeTypeDefCollapsibleSection(container);
	}
	
	protected void initializeTypeDefCollapsibleSection(Composite container) {
		final Section section = this.getWidgetFactory().createSection(container, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setText("Type Definitions");
		
		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanging(ExpansionEvent e) {
				if (e.getState() && section.getClient() == null) {
					section.setClient(initializeTypeDefsSection(section, buildTypeDefContainerHolder()));
				}
			}
		});
		
		
	}
	
	protected Control initializeTypeDefsSection(
			Composite container,
			PropertyValueModel<HibernateJavaTypeDefContainer> typeDefContainerHolder) {
		return new TypeDefsComposite(this, typeDefContainerHolder, container).getControl();
	}

	private PropertyValueModel<HibernateJavaTypeDefContainer> buildTypeDefContainerHolder() {
		return new PropertyAspectAdapter<HibernateJavaEntity, HibernateJavaTypeDefContainer>(getSubjectHolder()) {
			@Override
			protected HibernateJavaTypeDefContainer buildValue_() {
				return this.subject.getTypeDefContainer();
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Control initializeQueriesSection(Composite container) {
		return new HibernateQueries2_0Composite(this, (PropertyValueModel<? extends HibernateJavaQueryContainer>) buildQueryContainerHolder(), container).getControl();
	}
	
	@SuppressWarnings("unused")
	private PropertyValueModel<HibernateGeneratorContainer> buildGeneratorContainer() {
		return new PropertyAspectAdapter<HibernateJavaEntity, HibernateGeneratorContainer>(getSubjectHolder()) {
			@Override
			protected HibernateGeneratorContainer buildValue_() {
				return this.subject.getGeneratorContainer();
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Control initializeGeneratorsSection(Composite container) {
		return new HibernateGenerationComposite(this, (PropertyValueModel<? extends HibernateGeneratorContainer>) buildGeneratorContainerHolder(), addSubPane(container, 10)).getControl();
	}
	
	
	protected Control initializeEntitySection(Composite container) {
		
//		new HibernateTableComposite(this, container);
//		new EntityNameComposite(this, container);
//		new AccessTypeComposite(this, buildAccessHolder(), container);	
//		new IdClassComposite(this, buildIdClassReferenceHolder(), container);
//		new Cacheable2_0Pane(this, buildCacheableHolder(), container);

		container = this.addSubPane(container, 2, 0, 0, 0, 0);

		// Java class widgets
		Hyperlink javaClassHyperlink = this.addHyperlink(container, JptUiDetailsOrmMessages.OrmJavaClassChooser_javaClass);
		new OrmJavaClassChooser(this, (PropertyValueModel<? extends OrmTypeMapping>) getSubjectHolder(), container, javaClassHyperlink);

		// Table widgets
		HibernateTableComposite tableComposite = new HibernateTableComposite(this, container);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		tableComposite.getControl().setLayoutData(gridData);

		// Entity name widgets
		this.addLabel(container, JptUiDetailsMessages.EntityNameComposite_name);
		new EntityNameCombo(this, container);

		// Access type widgets
		this.addLabel(container, JptUiMessages.AccessTypeComposite_access);
		new AccessTypeComboViewer(this, this.buildAccessHolder(), container);

		// Id class widgets
		Hyperlink hyperlink = this.addHyperlink(container,JptUiDetailsMessages.IdClassComposite_label);
		new IdClassChooser(this, this.buildIdClassReferenceHolder(), container, hyperlink);

		// Cacheable widgets
		Cacheable2_0TriStateCheckBox cacheableCheckBox = new Cacheable2_0TriStateCheckBox(this, buildCacheableHolder(), container);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		cacheableCheckBox.getControl().setLayoutData(gridData);

		// Metadata complete widgets
		MetadataCompleteTriStateCheckBox metadataCompleteCheckBox = new MetadataCompleteTriStateCheckBox(this, (PropertyValueModel<? extends OrmTypeMapping>) getSubjectHolder(), container);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		metadataCompleteCheckBox.getControl().setLayoutData(gridData);

		return container;
		
	
	}
	
	protected PropertyValueModel<AccessHolder> buildAccessHolder() {
		return new PropertyAspectAdapter<JavaEntity, AccessHolder>(getSubjectHolder()) {
			@Override
			protected AccessHolder buildValue_() {
				return this.subject.getPersistentType();
			}
		};
	}
	
	protected PropertyValueModel<Cacheable2_0> buildCacheableHolder() {
		return new PropertyAspectAdapter<JavaEntity, Cacheable2_0>(getSubjectHolder()) {
			@Override
			protected Cacheable2_0 buildValue_() {
				return ((CacheableHolder2_0) this.subject).getCacheable();
			}
		};
	}
	
	@Override
	protected Control initializeSecondaryTablesSection(Composite container) {
		return new JavaSecondaryTablesComposite(this, container).getControl();
	}

	@Override
	protected Control initializeInheritanceSection(Composite container) {
		return new HibernateJavaInheritanceComposite(this, container).getControl();
	}
	
	@Override
	protected Control initializeAttributeOverridesSection(Composite container) {
		return new Entity2_0OverridesComposite(this, container).getControl();
	}
}
