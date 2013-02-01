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
package org.jboss.tools.hibernate.jpt.ui.internal.details.java;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.ui.internal.details.AbstractEntityComposite;
import org.eclipse.jpt.jpa.ui.internal.details.EntityNameCombo;
import org.eclipse.jpt.jpa.ui.internal.details.IdClassChooser;
import org.eclipse.jpt.jpa.ui.internal.details.JptUiDetailsMessages;
import org.eclipse.jpt.jpa.ui.internal.details.java.JavaSecondaryTablesComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaQueryContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTypeDefContainer;
import org.jboss.tools.hibernate.jpt.ui.internal.details.HibernateTableComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateGenerationComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.HibernateQueriesComposite;
import org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.TypeDefsComposite;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaEntityComposite extends AbstractEntityComposite<HibernateJavaEntity> {

	/**
	 * @param subjectHolder
	 * @param parent
	 * @param widgetFactory
	 */
	public HibernateJavaEntityComposite(PropertyValueModel<? extends HibernateJavaEntity> subjectHolder,
			Composite parent, WidgetFactory widgetFactory, ResourceManager resourceManager) {
		super(subjectHolder, parent, widgetFactory, resourceManager);
	}
	
	@Override
	protected void initializeLayout(Composite container) {
		super.initializeLayout(container);
		this.initializeTypeDefCollapsibleSection(container);
	}
	
	protected void initializeTypeDefCollapsibleSection(Composite container) {
		final Section section = this.getWidgetFactory().createSection(container,
				ExpandableComposite.TITLE_BAR |
				ExpandableComposite.TWISTIE |
				ExpandableComposite.EXPANDED);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setText(JptUiDetailsMessages.BasicSection_title);
		section.setClient(this.initializeTypeDefsSection(section, buildTypeDefContainerHolder()));
//		container = addCollapsibleSection(
//				container,
//				HibernateUIMappingMessages.HibernateJavaEntityComposite_TypeDefinitionsSection);
//		this.initializeTypeDefsSection(container, buildTypeDefContainerHolder());
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
		return new HibernateQueriesComposite(
				this, 
				(PropertyValueModel<? extends HibernateJavaQueryContainer>) buildGeneratorContainerModel(), 
				container).getControl();
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
		return new HibernateGenerationComposite(this, (PropertyValueModel<? extends HibernateGeneratorContainer>) buildGeneratorContainerModel(), container).getControl();
	}
	
	
	protected Control initializeEntitySection(Composite container) {
//		new HibernateTableComposite(this, container);
//		new EntityNameComposite(this, container);
//		new IdClassComposite(this, buildIdClassReferenceHolder(), container);

		container = this.addSubPane(container, 2, 0, 0, 0, 0);

		//Table widgets
		HibernateTableComposite tableComposite = new HibernateTableComposite(this, container);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		tableComposite.getControl().setLayoutData(gridData);

		//Entity name widgets
		this.addLabel(container, JptUiDetailsMessages.EntityNameComposite_name);
		new EntityNameCombo(this, container);

		//Id class widgets
		Hyperlink hyperlink = this.addHyperlink(container, JptUiDetailsMessages.IdClassComposite_label);
		new IdClassChooser(this, this.buildIdClassReferenceModel(), container, hyperlink);

		return container;
		
		
	}
	
	@Override
	protected Control initializeSecondaryTablesSection(Composite container) {
		return new JavaSecondaryTablesComposite(this, container).getControl();
	}

	@Override
	protected Control initializeInheritanceSection(Composite container) {
		return new HibernateJavaInheritanceComposite(this, container).getControl();
	}

}
