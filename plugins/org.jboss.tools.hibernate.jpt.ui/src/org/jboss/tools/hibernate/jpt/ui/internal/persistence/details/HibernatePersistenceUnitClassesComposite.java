/*******************************************************************************
  * Copyright (c) 2012 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details;

import java.util.ListIterator;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jpt.common.ui.internal.JptCommonUiMessages;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemoveListPane;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemovePane.Adapter;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.ItemPropertyListValueModelAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.common.utility.internal.model.value.swing.ObjectListSelectionModel;
import org.eclipse.jpt.common.utility.model.value.ListValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentType;
import org.eclipse.jpt.jpa.core.context.persistence.ClassRef;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.ui.JptJpaUiPlugin;
import org.eclipse.jpt.jpa.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.jpa.ui.internal.JpaMappingImageHelper;
import org.eclipse.jpt.jpa.ui.internal.JptUiIcons;
import org.eclipse.jpt.jpa.ui.internal.persistence.JptUiPersistenceMessages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.progress.IProgressService;


/**
 * 
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernatePersistenceUnitClassesComposite extends Pane<PersistenceUnit> {

	public HibernatePersistenceUnitClassesComposite(
			Pane<? extends PersistenceUnit> parentPane, Composite parent) {
		super(parentPane, parent);
	}

	private void addMappedClass(ObjectListSelectionModel listSelectionModel) {
		IType type = chooseType();

		if (type != null) {
			String className = type.getFullyQualifiedName('$');
			if(classRefExists(className)) {
				return;
			}
			ClassRef classRef = getSubject().addSpecifiedClassRef(className);
			listSelectionModel.setSelectedValue(classRef);
		}
	}
	
	private boolean classRefExists(String className) {
		for ( ListIterator<ClassRef> i = getSubject().getSpecifiedClassRefs().iterator(); i.hasNext(); ) {
			ClassRef classRef = i.next();
			if( classRef.getClassName().equals(className)) {
				return true;
			}
		}
		return false;
	}

	private Adapter buildAdapter() {
		return new AddRemoveListPane.AbstractAdapter() {
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addMappedClass(listSelectionModel);
			}

			@Override
			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
				if (!super.enableOptionOnSelectionChange(listSelectionModel)) {
					return false;
				}

				return findType((ClassRef) listSelectionModel.selectedValue()) != null;
			}

			@Override
			public boolean hasOptionalButton() {
				return true;
			}

			@Override
			public String optionalButtonText() {
				return JptUiPersistenceMessages.PersistenceUnitClassesComposite_open;
			}

			@Override
			public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
				openMappedClass((ClassRef) listSelectionModel.selectedValue());
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				for (Object item : listSelectionModel.selectedValues()) {
					getSubject().removeSpecifiedClassRef((ClassRef) item);
				}
			}
		};
	}

	private ModifiablePropertyValueModel<Boolean> buildExcludeUnlistedMappedClassesHolder() {
		return new PropertyAspectAdapter<PersistenceUnit, Boolean>(
			getSubjectHolder(),
			PersistenceUnit.SPECIFIED_EXCLUDE_UNLISTED_CLASSES_PROPERTY)
		{
			@Override
			protected Boolean buildValue_() {
				return this.subject.getSpecifiedExcludeUnlistedClasses();
			}

			@Override
			protected void setValue_(Boolean value) {
				if (value != null && !value){
					//fix for https://issues.jboss.org/browse/JBIDE-11773 and https://hibernate.onjira.com/browse/HHH-7301
					//remove the element
					value = null;
				}
				this.subject.setSpecifiedExcludeUnlistedClasses(value);
			}
		};
	}
	
	private ILabelProvider buildLabelProvider() {
		return new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				ClassRef classRef = (ClassRef) element;
				JavaPersistentType persistentType = classRef.getJavaPersistentType();
				Image image = null;

				if (persistentType != null) {
					image = JpaMappingImageHelper.imageForTypeMapping(persistentType.getMappingKey());
				}

				if (image != null) {
					return image;
				}

				return JptJpaUiPlugin.getImage(JptUiIcons.WARNING);
			}

			@Override
			public String getText(Object element) {
				ClassRef classRef = (ClassRef) element;
				String name = classRef.getClassName();

				if (name == null) {
					name = JptUiPersistenceMessages.PersistenceUnitClassesComposite_mappedClassesNoName;
				}

				return name;
			}
		};
	}

	private ListValueModel<ClassRef> buildItemListHolder() {
		return new ItemPropertyListValueModelAdapter<ClassRef>(
			buildListHolder(),
			ClassRef.JAVA_PERSISTENT_TYPE_PROPERTY,
			ClassRef.CLASS_NAME_PROPERTY
		);
	}

	private ListValueModel<ClassRef> buildListHolder() {
		return new ListAspectAdapter<PersistenceUnit, ClassRef>(getSubjectHolder(), PersistenceUnit.SPECIFIED_CLASS_REFS_LIST) {
			@Override
			protected ListIterator<ClassRef> listIterator_() {
				return subject.getSpecifiedClassRefs().iterator();
			}

			@Override
			protected int size_() {
				return subject.getSpecifiedClassRefsSize();
			}
		};
	}

	private ModifiablePropertyValueModel<ClassRef> buildSelectedItemHolder() {
		return new SimplePropertyValueModel<ClassRef>();
	}

	/**
	 * Prompts the user the Open Type dialog.
	 *
	 * @return Either the selected type or <code>null</code> if the user
	 * canceled the dialog
	 */
	private IType chooseType() {
		IJavaProject javaProject = getJavaProject();
		IJavaElement[] elements = new IJavaElement[] { javaProject };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		SelectionDialog typeSelectionDialog;

		try {
			typeSelectionDialog = JavaUI.createTypeDialog(
				getShell(),
				service,
				scope,
				IJavaElementSearchConstants.CONSIDER_CLASSES,
				false,
				""
			);
		}
		catch (JavaModelException e) {
			JptJpaUiPlugin.log(e);
			return null;
		}

		typeSelectionDialog.setTitle(JptCommonUiMessages.ClassChooserPane_dialogTitle);
		typeSelectionDialog.setMessage(JptCommonUiMessages.ClassChooserPane_dialogMessage);

		if (typeSelectionDialog.open() == Window.OK) {
			return (IType) typeSelectionDialog.getResult()[0];
		}

		return null;
	}

	private IType findType(ClassRef classRef) {
		String className = classRef.getClassName();

		if (className != null) {
			try {
				return getSubject().getJpaProject().getJavaProject().findType(className.replace('$', '.'));
			}
			catch (JavaModelException e) {
				JptJpaUiPlugin.log(e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected void initializeLayout(Composite container) {

		// Description
		addMultiLineLabel(
			container,
			JptUiPersistenceMessages.PersistenceUnitClassesComposite_description
		);

		// List pane
		new AddRemoveListPane<PersistenceUnit>(
			this,
			container,
			this.buildAdapter(),
			this.buildItemListHolder(),
			this.buildSelectedItemHolder(),
			this.buildLabelProvider(),
			JpaHelpContextIds.PERSISTENCE_XML_GENERAL
		)
		{
			@Override
			protected void initializeTable(Table table) {
				super.initializeTable(table);

				Composite container = table.getParent();
				GridData gridData   = (GridData) container.getLayoutData();
				gridData.heightHint = 75;
			}
		};

		this.addCheckBox(
			container,
			JptUiPersistenceMessages.PersistenceUnitClassesComposite_excludeUnlistedMappedClasses,
			buildExcludeUnlistedMappedClassesHolder(),
			JpaHelpContextIds.PERSISTENCE_XML_GENERAL
		);
	}

	private void openMappedClass(ClassRef classRef) {

		IType type = findType(classRef);

		if (type != null) {
			try {
				IJavaElement javaElement = type.getParent();
				JavaUI.openInEditor(javaElement, true, true);
			}
			catch (PartInitException e) {
				JptJpaUiPlugin.log(e);
			}
			catch (JavaModelException e) {
				JptJpaUiPlugin.log(e);
			}
		}
	}

	private IJavaProject getJavaProject() {
		return getSubject().getJpaProject().getJavaProject();
	}
}
