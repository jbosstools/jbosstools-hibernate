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

import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jpt.common.ui.internal.util.ControlSwitcher;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemoveListPane;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemovePane.AbstractAdapter;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemovePane.Adapter;
import org.eclipse.jpt.common.ui.internal.widgets.NewNameDialog;
import org.eclipse.jpt.common.ui.internal.widgets.NewNameDialogBuilder;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.common.utility.internal.model.value.CollectionPropertyValueModelAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.ItemPropertyListValueModelAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.SimpleCollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.CollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.ListValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiableCollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.transformer.Transformer;
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
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateGenericGeneratorContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaDbGenericGenerator;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenericGeneratorsComposite extends Pane<HibernateGenericGeneratorContainer> {
	
	private AddRemoveListPane<HibernateGenericGeneratorContainer, JavaDbGenericGenerator> listPane;
	Pane<?> genericGeneratorPane;
//	private SimpleCollectionValueModel<GenericDbGenerator> generatorHolder;
	private NewNameDialogBuilder dialogBuilder = null;
	
	private ModifiableCollectionValueModel<JavaDbGenericGenerator> selectedGeneratorsModel;
	private PropertyValueModel<JavaDbGenericGenerator> selectedGeneratorModel;
	

	public GenericGeneratorsComposite(
			Pane<?> parentPane, 
			PropertyValueModel<? extends HibernateGenericGeneratorContainer> subjectHolder,
			Composite parent) {
//	public GenericGeneratorsComposite(
//		Pane<? extends Model> parentPane, 
//		PropertyValueModel<? extends HibernateGenericGeneratorContainer> subjectHolder,
//		PropertyValueModel<Boolean> enabledModel,
//		Composite parent) {
		super((Pane<? extends HibernateGenericGeneratorContainer>) parentPane, parent);
		dialogBuilder = new NewNameDialogBuilder(getShell());
		dialogBuilder.setDialogTitle(HibernateUIMappingMessages.GenericGeneratorsComposite_dialogTitle);
		dialogBuilder.setDescriptionTitle(HibernateUIMappingMessages.GenericGeneratorsComposite_DescriptionTitle);
		dialogBuilder.setDescription(HibernateUIMappingMessages.GenericGeneratorsComposite_Description);
		dialogBuilder.setLabelText(HibernateUIMappingMessages.GenericGeneratorsComposite_Name);		
	}

	JavaDbGenericGenerator addGenericDbGenerator() {
		return addGenericGeneratorFromDialog(buildAddGenericGeneratorDialog());
	}
	
	protected HibernatePersistenceUnit getPersistenceUnit(){
		return (HibernatePersistenceUnit)this.getSubject().getPersistenceUnit();
	}

	protected NewNameDialog buildAddGenericGeneratorDialog() {
		dialogBuilder.setExistingNames(getPersistenceUnit().getUniqueGeneratorNames().iterator());
		return dialogBuilder.buildDialog();
	}

	protected JavaDbGenericGenerator addGenericGeneratorFromDialog(NewNameDialog dialog) {
		if (dialog.open() != Window.OK) {
			return null;
		}
		JavaDbGenericGenerator generator = this.getSubject().addGenericGenerator();
		generator.setName(dialog.getName());
//		this.getGenericGeneratorHolder().setValue(generator);//so that it gets selected in the List for the user to edit
		return generator;
	}

	private ListValueModel<GenericGenerator> buildDisplayableGenericGeneratorsListHolder() {
		return new ItemPropertyListValueModelAdapter<GenericGenerator>(
			buildGenericGeneratorsListHolder(),
			GenericGenerator.NAME_PROPERTY
		);
	}
	
	private AddRemoveListPane<HibernateGenericGeneratorContainer, JavaDbGenericGenerator> addListPane(Composite container) {

		return new AddRemoveListPane<HibernateGenericGeneratorContainer, JavaDbGenericGenerator>(
				this, 
				container, 
				buildGenericGeneratorsAdapter(), 
				buildDisplayableGenericGeneratorsListHolder(), 
				this.selectedGeneratorsModel, 
				buildGenericGeneratorsListLabelProvider());
//		return new AddRemoveListPane<HibernateGeneratorContainer, GenericGenerator>(
//			this,
//			container,
//			buildGenericGeneratorsAdapter(),
//			buildDisplayableGenericGeneratorsListHolder(),
//			this.getGenericGeneratorHolder(),
//			buildGenericGeneratorsListLabelProvider(),
//			(String)null
//		);
	}

	private ListValueModel<GenericGenerator> buildGenericGeneratorsListHolder() {
		return new ListAspectAdapter<HibernateGeneratorContainer, GenericGenerator>(
			getSubjectHolder(),
			HibernateGeneratorContainer.GENERIC_GENERATORS_LIST)
		{
			@Override
			protected ListIterator<GenericGenerator> listIterator_() {
				return (ListIterator<GenericGenerator>) this.subject.getGenericGenerators().iterator();
			}

			@Override
			protected int size_() {
				return this.subject.getGenericGeneratorsSize();
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
	
	private Adapter<JavaDbGenericGenerator> buildGenericGeneratorsAdapter() {

		return new AbstractAdapter<JavaDbGenericGenerator>() {

			public JavaDbGenericGenerator addNewItem() {
				return addGenericDbGenerator();
			}

			public void removeSelectedItems(
					CollectionValueModel<JavaDbGenericGenerator> selectedItemsModel) {
				Iterator<JavaDbGenericGenerator> iterator = selectedItemsModel.iterator();
				while (iterator.hasNext()) {
					getSubject().removeGenericGenerator(iterator.next());
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
					int index = IterableTools.indexOf(getSubject().getGenericGenerators(), generator);
					name = NLS.bind(HibernateUIMappingMessages.GenericGeneratorsComposite_displayString, Integer.valueOf(index));
				}

				return name;
			}
		};
	}

//	private SimpleCollectionValueModel<GenericDbGenerator> buildGenericGeneratorHolder() {
//		return new SimpleCollectionValueModel<GenericDbGenerator>();
//	}

/*	@Override
	public void enableWidgets(boolean enabled) {
		super.enableWidgets(enabled);
		this.listPane.enableWidgets(enabled);
	}
*/
	@Override
	protected void initialize() {
		super.initialize();
//		this.generatorHolder = buildGenericGeneratorHolder();
		this.selectedGeneratorsModel = this.buildSelectedGeneratorsModel();
		this.selectedGeneratorModel = this.buildSelectedGeneratorModel(this.selectedGeneratorsModel);
	}

	private ModifiableCollectionValueModel<JavaDbGenericGenerator> buildSelectedGeneratorsModel() {
		return new SimpleCollectionValueModel<JavaDbGenericGenerator>();
	}

	private PropertyValueModel<JavaDbGenericGenerator> buildSelectedGeneratorModel(CollectionValueModel<JavaDbGenericGenerator> selectedGeneratorsModel) {
		return new CollectionPropertyValueModelAdapter<JavaDbGenericGenerator, JavaDbGenericGenerator>(selectedGeneratorsModel) {
			@Override
			protected JavaDbGenericGenerator buildValue() {
				if (this.collectionModel.size() == 1) {
					return this.collectionModel.iterator().next();
				}
				return null;
			}
		};
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
	
	protected Pane<? extends JavaDbGenericGenerator> buildGenericGeneratorComposite(PageBook pageBook) {
		return new GenericGeneratorComposite(
			this,
			(PropertyValueModel<JavaDbGenericGenerator>) this.selectedGeneratorModel,
			pageBook,
			buildGenericGeneratorBuilder()
		);
	}
	
	protected GeneratorBuilder<JavaDbGenericGenerator> buildGenericGeneratorBuilder() {
		return new GeneratorBuilder<JavaDbGenericGenerator>() {
			public JavaDbGenericGenerator addGenerator() {
				HibernateGenericGeneratorContainer container = (HibernateGenericGeneratorContainer)getSubject();
				JavaDbGenericGenerator generator = container.addGenericGenerator(container.getGenericGeneratorsSize());
//				generatorHolder.setValue(generator);
				return generator;
			}
		};
	}

	private void installPaneSwitcher(PageBook pageBook) {
		new ControlSwitcher(this.getGenericGeneratorHolder(), this.buildPaneTransformer(), pageBook);
	}
	
	protected PropertyValueModel<JavaDbGenericGenerator> getGenericGeneratorHolder() {
		return this.selectedGeneratorModel;
	}
	
}
