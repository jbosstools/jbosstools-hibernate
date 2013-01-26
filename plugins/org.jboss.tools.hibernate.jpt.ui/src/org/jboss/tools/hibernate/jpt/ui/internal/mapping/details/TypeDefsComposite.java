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
import org.eclipse.jpt.common.ui.internal.widgets.AddRemovePane.Adapter;
import org.eclipse.jpt.common.ui.internal.widgets.NewNameDialog;
import org.eclipse.jpt.common.ui.internal.widgets.NewNameDialogBuilder;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.common.utility.internal.iterator.ArrayIterator;
import org.eclipse.jpt.common.utility.internal.model.value.CollectionPropertyValueModelAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.ItemPropertyListValueModelAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.SimpleCollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.CollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.ListValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiableCollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.common.utility.transformer.Transformer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.PageBook;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTypeDefContainer;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaTypeDef;

/**
 * @author Dmitry Geraskov
 *
 */
public class TypeDefsComposite extends Pane<HibernateJavaTypeDefContainer> {

//	private AddRemoveListPane<HibernateJavaTypeDefContainer, JavaTypeDef> listPane;
	Pane<? extends JavaTypeDef> typeDefPane;
	
//	private ModifiablePropertyValueModel<JavaTypeDef> typeDefHolder;
	private NewNameDialogBuilder dialogBuilder = null;
	private ModifiableCollectionValueModel<JavaTypeDef> selectedTypeDefsModel;
	private PropertyValueModel<JavaTypeDef> selectedTypeDefModel;

	public TypeDefsComposite(
		Pane<?> parentPane, 
		PropertyValueModel<? extends HibernateJavaTypeDefContainer> subjectHolder,
		Composite parent) {
		super(parentPane, subjectHolder, parent);
		dialogBuilder = new NewNameDialogBuilder(getShell());
		dialogBuilder.setDialogTitle(HibernateUIMappingMessages.TypeDefsComposite_dialogTitle);
		dialogBuilder.setDescriptionTitle(HibernateUIMappingMessages.TypeDefsComposite_DescriptionTitle);
		dialogBuilder.setDescription(HibernateUIMappingMessages.TypeDefsComposite_Description);
		dialogBuilder.setLabelText(HibernateUIMappingMessages.TypeDefsComposite_Name);		
	}

//	void addTypeDef() {
//		addTypeDefFromDialog(buildAddTypeDefDialog());
//	}
	
	JavaTypeDef addTypeDef() {
		return addTypeDefFromDialog(buildAddTypeDefDialog());
	}
	
	protected HibernatePersistenceUnit getPersistenceUnit(){
		return (HibernatePersistenceUnit)this.getSubject().getPersistenceUnit();
	}

	protected NewNameDialog buildAddTypeDefDialog() {
		dialogBuilder.setExistingNames(new ArrayIterator<String>(getPersistenceUnit().uniqueTypeDefNames()));
		return dialogBuilder.buildDialog();
	}

//	protected void addTypeDefFromDialog(NewNameDialog dialog) {
//		if (dialog.open() != Window.OK) {
//			return;
//		}
//		JavaTypeDef typeDef = this.getSubject().addTypeDef();
//		typeDef.setName(dialog.getName());
//		this.getTypeDefHolder().setValue(typeDef);//so that it gets selected in the List for the user to edit
//	}

	protected JavaTypeDef addTypeDefFromDialog(NewNameDialog dialog) {
		if (dialog.open() != Window.OK) {
			return null;
		}
		JavaTypeDef typeDef = this.getSubject().addTypeDef();
		typeDef.setName(dialog.getName());
//		this.getTypeDefHolder().setValue(typeDef);//so that it gets selected in the List for the user to edit
		return typeDef;
	}

	private ListValueModel<JavaTypeDef> buildDisplayableTypeDefsListHolder() {
		return new ItemPropertyListValueModelAdapter<JavaTypeDef>(
			buildTypeDefsListHolder(),
			JavaTypeDef.NAME_PROPERTY
		);
	}
	
	private AddRemoveListPane<HibernateJavaTypeDefContainer, JavaTypeDef> addListPane(Composite container) {

		return new AddRemoveListPane<HibernateJavaTypeDefContainer, JavaTypeDef>(
			this,
			container,
			buildTypeDefsAdapter(),
			buildDisplayableTypeDefsListHolder(),
			this.selectedTypeDefsModel,
			buildTypeDefsListLabelProvider()
		);
	}

	private ListValueModel<JavaTypeDef> buildTypeDefsListHolder() {
		return new ListAspectAdapter<HibernateJavaTypeDefContainer, JavaTypeDef>(
			getSubjectHolder(),
			HibernateJavaTypeDefContainer.TYPE_DEFS_LIST)
		{
			@Override
			protected ListIterator<JavaTypeDef> listIterator_() {
				return this.subject.getTypeDefs().iterator();
			}

			@Override
			protected int size_() {
				return this.subject.getTypeDefsSize();
			}
		};
	}

	private Transformer<JavaTypeDef, Control> buildPaneTransformer() {
		return new Transformer<JavaTypeDef, Control>() {
			public Control transform(JavaTypeDef typeDef) {

				if (typeDef == null) {
					return null;
				}

				return TypeDefsComposite.this.typeDefPane.getControl();				
			}
		};
	}
	
	private Adapter<JavaTypeDef> buildTypeDefsAdapter() {

		return new AddRemoveListPane.AbstractAdapter<JavaTypeDef>() {

//			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
//				addTypeDef();
//			}

//			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
//				for (Object item : listSelectionModel.selectedValues()) {
//					if (item instanceof JavaTypeDef) {
//						getSubject().removeTypeDef((JavaTypeDef) item);
//					}
//				}
//			}

			@Override
			public JavaTypeDef addNewItem() {
				return addTypeDef();
			}

			@Override
			public void removeSelectedItems(
					CollectionValueModel<JavaTypeDef> selectedItemsModel) {
				Iterator<JavaTypeDef> iterator = selectedItemsModel.iterator();
				while (iterator.hasNext()) {
					getSubject().removeTypeDef(iterator.next());
				}
			}
		};
	}


	private ILabelProvider buildTypeDefsListLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				JavaTypeDef typeDef = (JavaTypeDef) element;
				String name = typeDef.getName();

				if (name == null) {
					int index = IterableTools.indexOf(getSubject().getTypeDefs(), typeDef);
					name = NLS.bind(HibernateUIMappingMessages.TypeDefsComposite_displayString, Integer.valueOf(index));
				}

				return name;
			}
		};
	}

//	private ModifiablePropertyValueModel<JavaTypeDef> buildTypeDefHolder() {
//		return new SimplePropertyValueModel<JavaTypeDef>();
//	}

//	@Override
//	public void enableWidgets(boolean enabled) {
//		super.enableWidgets(enabled);
//		this.listPane.enableWidgets(enabled);
//	}

	@Override
	protected void initialize() {
		super.initialize();
		this.selectedTypeDefsModel = this.buildSelectedTypeDefsModel();
		this.selectedTypeDefModel = this.buildSelectedTypeDefModel(this.selectedTypeDefsModel);
//		this.typeDefHolder = buildTypeDefHolder();
	}

	private ModifiableCollectionValueModel<JavaTypeDef> buildSelectedTypeDefsModel() {
		return new SimpleCollectionValueModel<JavaTypeDef>();
	}

	private PropertyValueModel<JavaTypeDef> buildSelectedTypeDefModel(CollectionValueModel<JavaTypeDef> selectedTypeDefsModel) {
		return new CollectionPropertyValueModelAdapter<JavaTypeDef, JavaTypeDef>(selectedTypeDefsModel) {
			@Override
			protected JavaTypeDef buildValue() {
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
//		this.listPane = this.addListPane(container);
		addListPane(container);

		// Property pane
		PageBook pageBook = new PageBook(container, SWT.NULL);
		pageBook.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Named JavaTypeDef property pane
		this.typeDefPane = this.buildTypeDefPropertyComposite(pageBook);

		installPaneSwitcher(pageBook);
	}
	
	protected Pane<? extends JavaTypeDef> buildTypeDefPropertyComposite(PageBook pageBook) {
		return new TypeDefPropertyComposite<JavaTypeDef>(
			this,
			this.getTypeDefHolder(),
			pageBook
		);
	}

	private void installPaneSwitcher(PageBook pageBook) {
		new ControlSwitcher(this.getTypeDefHolder(), this.buildPaneTransformer(), pageBook);
	}
	
	protected PropertyValueModel<JavaTypeDef> getTypeDefHolder() {
		return this.selectedTypeDefModel;
	}
	
}
