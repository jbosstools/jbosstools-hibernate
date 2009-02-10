/*******************************************************************************
 * Copyright (c) 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *     Dmitry Geraskov, Exadel Inc. - Extracted from Dali 2.0 to protect from changes.
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.xpl;

import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.details.JpaPageComposite;
import org.eclipse.jpt.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.ui.internal.persistence.JptUiPersistenceMessages;
import org.eclipse.jpt.ui.internal.persistence.details.PersistenceUnitJarFilesComposite;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Here the layout of this pane:
 * <pre>
 * -----------------------------------------------------------------------------
 * |                                                                           |
 * | - General --------------------------------------------------------------- |
 * |                         ------------------------------------------------- |
 * |   Name:                 | I                                             | |
 * |                         ------------------------------------------------- |
 * |                         ------------------------------------------------- |
 * |   Persistence Provider: |                                             |v| |
 * |                         ------------------------------------------------- |
 * |                                                                           |
 * |                                                                           |
 * | - Mapped Classes -------------------------------------------------------- |
 * |                                                                           |
 * |   Description                                                             |
 * |                                                                           |
 * |   ----------------------------------------------------------------------- |
 * |   |                                                                     | |
 * |   | PersistenceUnitMappedClassesComposite                               | |
 * |   |                                                                     | |
 * |   ----------------------------------------------------------------------- |
 * |                                                                           |
 * |                                                                           |
 * | - XML Mapping Files ----------------------------------------------------- |
 * |                                                                           |
 * |   Description                                                             |
 * |                                                                           |
 * |   ----------------------------------------------------------------------- |
 * |   |                                                                     | |
 * |   | PersistenceUnitMappingFilesComposite                                | |
 * |   |                                                                     | |
 * |   ----------------------------------------------------------------------- |
 * -----------------------------------------------------------------------------</pre>
 *
 * @see PersistenceUnit
 * @see PersistenceUnitJarFilesComposite
 * @see PersistenceUnitMappedClassesComposite
 * @see PersistenceUnitMappingFilesComposite
 *
 * @version 2.0
 * @since 2.0
 */
public class PersistenceUnitGeneralComposite extends AbstractPane<PersistenceUnit>
                                             implements JpaPageComposite
{
	/**
	 * Creates a new <code>PersistenceUnitGeneralComposite</code>.
	 *
	 * @param subjectHolder The holder of this pane's subject
	 * @param parent The parent container
	 * @param widgetFactory The factory used to create various common widgets
	 */
	public PersistenceUnitGeneralComposite(PropertyValueModel<PersistenceUnit> subjectHolder,
	                                       Composite container,
	                                       WidgetFactory widgetFactory) {

		super(subjectHolder, container, widgetFactory);
	}

//	private void initializeJavaArchivesPane(Composite container) {
//
//		container = buildSection(
//			container,
//			JptUiPersistenceMessages.PersistenceUnitComposite_javaArchives
//		);
//
//		new PersistenceUnitJavaArchivesComposite(this, container);
//	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected Composite buildContainer(Composite parent) {

		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight    = 0;
		layout.marginWidth     = 0;
		layout.marginTop       = 0;
		layout.marginLeft      = 0;
		layout.marginBottom    = 0;
		layout.marginRight     = 0;
		layout.verticalSpacing = 15;

		Composite container = buildPane(parent, layout);
		updateGridData(container);

		return container;
	}

	private WritablePropertyValueModel<String> buildPersistenceProviderHolder() {
		return new PropertyAspectAdapter<PersistenceUnit, String>(getSubjectHolder(), PersistenceUnit.PROVIDER_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getProvider();
			}

			@Override
			protected void setValue_(String value) {
				if (value.length() == 0) {
					value = null;
				}
				subject.setProvider(value);
			}
		};
	}

	private WritablePropertyValueModel<String> buildPersistenceUnitNameHolder() {
		return new PropertyAspectAdapter<PersistenceUnit, String>(getSubjectHolder(), PersistenceUnit.NAME_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getName();
			}

			@Override
			protected void setValue_(String value) {
				subject.setName(value);
			}
		};
	}

	private WritablePropertyValueModel<String> buildPersistenceUnitDescriptionHolder() {
		return new PropertyAspectAdapter<PersistenceUnit, String>(getSubjectHolder(), PersistenceUnit.DESCRIPTION_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getDescription();
			}
			
			@Override
			protected void setValue_(String value) {
				if (value.length() == 0) {
					value = null;
				}
				subject.setDescription(value);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 */
	public String getHelpID() {
		return JpaHelpContextIds.PERSISTENCE_UNIT_GENERAL;
	}

	/*
	 * (non-Javadoc)
	 */
	public Image getPageImage() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 */
	public String getPageText() {
		return JptUiPersistenceMessages.PersistenceUnitGeneralComposite_general;
	}

	private void initializeGeneralPane(Composite container) {

		container = buildSection(
			container,
			JptUiPersistenceMessages.PersistenceUnitGeneralComposite_general
		);

		// Name widgets
		buildLabeledText(
			container,
			JptUiPersistenceMessages.PersistenceUnitGeneralComposite_name,
			buildPersistenceUnitNameHolder()
		);

		// Persistence Provider widgets
		buildLabeledText(
			container,
			JptUiPersistenceMessages.PersistenceUnitGeneralComposite_persistenceProvider,
			buildPersistenceProviderHolder()
		);

		// Description widgets
		buildLabeledText(
			container,
			JptUiPersistenceMessages.PersistenceUnitGeneralComposite_description,
			buildPersistenceUnitDescriptionHolder()
		);
	}

	private void initializeJPAMappingDescriptorsPane(Composite container) {

		container = buildSection(
			container,
			JptUiPersistenceMessages.PersistenceUnitGeneralComposite_jpaMappingDescriptors,
			JptUiPersistenceMessages.PersistenceUnitGeneralComposite_jpaMappingDescriptors_description
		);

		updateGridData(container);
		updateGridData(container.getParent());

		new PersistenceUnitMappingFilesComposite(this, container);
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected void initializeLayout(Composite container) {

		initializeGeneralPane(container);
		initializeMappedClassesPane(container);
		initializeJPAMappingDescriptorsPane(container);
//		initializeJavaArchivesPane(container);

	}

	private void initializeMappedClassesPane(Composite container) {

		container = buildSection(
			container,
			JptUiPersistenceMessages.PersistenceUnitGeneralComposite_mappedClasses
		);

		updateGridData(container);
		updateGridData(container.getParent());

		new PersistenceUnitClassesComposite(this, container);
	}

	private void updateGridData(Composite container) {

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace   = true;
		gridData.horizontalAlignment       = SWT.FILL;
		gridData.verticalAlignment         = SWT.FILL;
		container.setLayoutData(gridData);
	}
}
