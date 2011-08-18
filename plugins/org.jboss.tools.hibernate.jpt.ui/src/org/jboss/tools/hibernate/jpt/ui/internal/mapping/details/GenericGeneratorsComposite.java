/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import java.util.ListIterator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jpt.common.ui.internal.util.ControlSwitcher;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemoveListPane;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemovePane.Adapter;
import org.eclipse.jpt.common.ui.internal.widgets.NewNameDialog;
import org.eclipse.jpt.common.ui.internal.widgets.NewNameDialogBuilder;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.common.utility.internal.Transformer;
import org.eclipse.jpt.common.utility.internal.model.value.ItemPropertyListValueModelAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.common.utility.internal.model.value.swing.ObjectListSelectionModel;
import org.eclipse.jpt.common.utility.model.value.ListValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.WritablePropertyValueModel;
import org.eclipse.jpt.jpa.ui.internal.details.GeneratorComposite.GeneratorBuilder;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.PageBook;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGenerator;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenericGeneratorsComposite extends Pane<HibernateGeneratorContainer> {

	private AddRemoveListPane<HibernateGeneratorContainer> listPane;
	Pane<? extends GenericGenerator> genericGeneratorPane;
	private WritablePropertyValueModel<GenericGenerator> generatorHolder;
	private NewNameDialogBuilder dialogBuilder = null;

	public GenericGeneratorsComposite(
		Pane<?> parentPane, 
		PropertyValueModel<? extends HibernateGeneratorContainer> subjectHolder,
		Composite parent) {
		super(parentPane, subjectHolder, parent, false);
		dialogBuilder = new NewNameDialogBuilder(getShell());
		dialogBuilder.setDialogTitle(HibernateUIMappingMessages.GenericGeneratorsComposite_dialogTitle);
		dialogBuilder.setDescriptionTitle(HibernateUIMappingMessages.GenericGeneratorsComposite_DescriptionTitle);
		dialogBuilder.setDescription(HibernateUIMappingMessages.GenericGeneratorsComposite_Description);
		dialogBuilder.setLabelText(HibernateUIMappingMessages.GenericGeneratorsComposite_Name);		
	}

	void addGenericGenerator() {
		addGenericGeneratorFromDialog(buildAddGenericGeneratorDialog());
	}
	
	protected HibernatePersistenceUnit getPersistenceUnit(){
		return (HibernatePersistenceUnit)this.getSubject().getPersistenceUnit();
	}

	protected NewNameDialog buildAddGenericGeneratorDialog() {
		dialogBuilder.setExistingNames(getPersistenceUnit().getUniqueGeneratorNames().iterator());
		return dialogBuilder.buildDialog();
	}

	protected void addGenericGeneratorFromDialog(NewNameDialog dialog) {
		if (dialog.open() != Window.OK) {
			return;
		}
		GenericGenerator generator = this.getSubject().addGenericGenerator();
		generator.setName(dialog.getName());
		this.getGenericGeneratorHolder().setValue(generator);//so that it gets selected in the List for the user to edit
	}

	private ListValueModel<GenericGenerator> buildDisplayableGenericGeneratorsListHolder() {
		return new ItemPropertyListValueModelAdapter<GenericGenerator>(
			buildGenericGeneratorsListHolder(),
			GenericGenerator.NAME_PROPERTY
		);
	}
	
	private AddRemoveListPane<HibernateGeneratorContainer> addListPane(Composite container) {

		return new AddRemoveListPane<HibernateGeneratorContainer>(
			this,
			container,
			buildGenericGeneratorsAdapter(),
			buildDisplayableGenericGeneratorsListHolder(),
			this.getGenericGeneratorHolder(),
			buildGenericGeneratorsListLabelProvider()
		);
	}

	private ListValueModel<GenericGenerator> buildGenericGeneratorsListHolder() {
		return new ListAspectAdapter<HibernateGeneratorContainer, GenericGenerator>(
			getSubjectHolder(),
			HibernateGeneratorContainer.GENERIC_GENERATORS_LIST)
		{
			@Override
			protected ListIterator<GenericGenerator> listIterator_() {
				return (ListIterator<GenericGenerator>) this.subject.genericGenerators();
			}

			@Override
			protected int size_() {
				return this.subject.genericGeneratorsSize();
			}
		};
	}

	private Transformer<GenericGenerator, Control> buildPaneTransformer() {
		return new Transformer<GenericGenerator, Control>() {
			public Control transform(GenericGenerator generator) {

				if (generator == null) {
					return null;
				}

				return GenericGeneratorsComposite.this.genericGeneratorPane.getControl();				
			}
		};
	}
	
	private Adapter buildGenericGeneratorsAdapter() {

		return new AddRemoveListPane.AbstractAdapter() {

			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addGenericGenerator();
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				for (Object item : listSelectionModel.selectedValues()) {
					if (item instanceof GenericGenerator) {
						getSubject().removeGenericGenerator((GenericGenerator) item);
					}
				}
			}
		};
	}


	private ILabelProvider buildGenericGeneratorsListLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				GenericGenerator generator = (GenericGenerator) element;
				String name = generator.getName();

				if (name == null) {
					int index = CollectionTools.indexOf(getSubject().genericGenerators(), generator);
					name = NLS.bind(HibernateUIMappingMessages.GenericGeneratorsComposite_displayString, Integer.valueOf(index));
				}

				return name;
			}
		};
	}

	private WritablePropertyValueModel<GenericGenerator> buildGenericGeneratorHolder() {
		return new SimplePropertyValueModel<GenericGenerator>();
	}

	@Override
	public void enableWidgets(boolean enabled) {
		super.enableWidgets(enabled);
		this.listPane.enableWidgets(enabled);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.generatorHolder = buildGenericGeneratorHolder();
	}

	@Override
	protected void initializeLayout(Composite container) {

		// List pane
		this.listPane = this.addListPane(container);

		// Property pane
		PageBook pageBook = new PageBook(container, SWT.NULL);
		pageBook.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Named GenericGenerator property pane
		this.genericGeneratorPane = this.buildGenericGeneratorComposite(pageBook);

		installPaneSwitcher(pageBook);
	}
	
	protected Pane<? extends GenericGenerator> buildGenericGeneratorComposite(PageBook pageBook) {
		return new GenericGeneratorComposite(
			this,
			this.getGenericGeneratorHolder(),
			pageBook,
			buildGenericGeneratorBuilder()
		);
	}
	
	protected GeneratorBuilder<GenericGenerator> buildGenericGeneratorBuilder() {
		return new GeneratorBuilder<GenericGenerator>() {
			public GenericGenerator addGenerator() {
				HibernateJavaGeneratorContainer container = (HibernateJavaGeneratorContainer)getSubject();
				JavaGenericGenerator generator = container.addGenericGenerator(container.genericGeneratorsSize());
				generatorHolder.setValue(generator);
				return generator;
			}
		};
	}

	private void installPaneSwitcher(PageBook pageBook) {
		new ControlSwitcher(this.getGenericGeneratorHolder(), this.buildPaneTransformer(), pageBook);
	}
	
	protected WritablePropertyValueModel<GenericGenerator> getGenericGeneratorHolder() {
		return generatorHolder;
	}
	
}
