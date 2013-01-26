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
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.ui.details.JpaComposite;
import org.eclipse.jpt.jpa.ui.internal.details.AbstractIdMappingComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateColumn;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateIdMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.IndexHolder;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMapping;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateIdMappingComposite extends AbstractIdMappingComposite<HibernateIdMapping>
implements JpaComposite{
	/**
	 * Creates a new <code>HibernateIdMappingComposite</code>.
	 *
	 * @param subjectHolder The holder of the subject <code>IdMapping</code>
	 * @param parent The parent container
	 * @param widgetFactory The factory used to create various common widgets
	 */
	public HibernateIdMappingComposite(
			PropertyValueModel<? extends HibernateIdMapping> subjectHolder,
			PropertyValueModel<Boolean> enabledModel,
	        Composite parent,
	        WidgetFactory widgetFactory) {

		super(subjectHolder, enabledModel, parent, widgetFactory);
	}
	
	@Override
	protected void initializeLayout(Composite container) {
		super.initializeLayout(container);
		this.initializeIndexCollapsibleSection(container);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Control initializeIdSection(Composite container) {
		return new HibernateColumnComposite(
				this, 
				(PropertyValueModel<? extends HibernateColumn>) buildColumnHolder(), 
				container).getControl();
	}
	
	private PropertyValueModel<HibernateGeneratorContainer> buildGeneratorContainer() {
		return new PropertyAspectAdapter<HibernateIdMapping, HibernateGeneratorContainer>(getSubjectHolder()) {
			@Override
			protected HibernateGeneratorContainer buildValue_() {
				return (HibernateGeneratorContainer) this.subject.getGeneratorContainer();
			}
		};
	}
	
	@Override
	protected void initializeGenerationCollapsibleSection(Composite container) {
		if (getSubject() instanceof HibernateJavaIdMapping) {
			// Generic Generator required only for Java.
			new HibernateGenerationComposite(this, buildGeneratorContainer(), addSubPane(container, 10));
		} else {
			super.initializeGenerationCollapsibleSection(container);
		}
	}
	
	protected void initializeIndexCollapsibleSection(Composite container) {
		if (getSubject() instanceof IndexHolder) {
			container = addSection(
					container,
					HibernateUIMappingMessages.Index_section_index,""
				);
			((GridLayout) container.getLayout()).numColumns = 2;
			this.initializeIndexSection(container);			
		}
		
	}

	@SuppressWarnings("unchecked")
	protected void initializeIndexSection(Composite container) {
		new IndexHolderComposite((Pane<? extends IndexHolder>) this, container);	
	}


}
