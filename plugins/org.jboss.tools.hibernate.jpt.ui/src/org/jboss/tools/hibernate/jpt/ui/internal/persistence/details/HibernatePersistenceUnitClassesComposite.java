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

import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jpt.common.ui.internal.JptCommonUiMessages;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.ItemPropertyListValueModelAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.SimpleCollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.CollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.ListValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiableCollectionValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentType;
import org.eclipse.jpt.jpa.core.context.persistence.ClassRef;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.jpa.ui.internal.JptUiIcons;
import org.eclipse.jpt.jpa.ui.internal.persistence.JptUiPersistenceMessages;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitClassesComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.progress.IProgressService;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.context.persistence.PackageInfoRef;
import org.jboss.tools.hibernate.jpt.ui.HibernateJptUIPlugin;


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

//	private void addMappedClass(ObjectListSelectionModel listSelectionModel) {
//		IType type = chooseType();
//
//		if (type != null) {
//			String className = type.getFullyQualifiedName('$');
//			if(classRefExists(className)) {
//				return;
//			}
//			ClassRef classRef = getSubject().addSpecifiedClassRef(className);
//			listSelectionModel.setSelectedValue(classRef);
//		}
//	}
	
	private ClassRef addMappedClass() {
		IType type = chooseType();
		ClassRef result = null;
		if (type != null) {
			String className = type.getFullyQualifiedName('$');
			result = getClassRef(className);
			if(result == null) {
				result = getSubject().addSpecifiedClassRef(className);
			}
		}
		return result;
	}

//	private void addMappedPackage(ObjectListSelectionModel listSelectionModel) {
//		IPackageFragment pack = choosePackage();
//
//		if (pack != null) {
//			if(classRefExists(pack.getElementName())) {
//				return;
//			}
//			ClassRef classRef = getSubject().addSpecifiedClassRef(pack.getElementName());
//			listSelectionModel.setSelectedValue(classRef);
//		}
//	}
	
	private ClassRef addMappedPackage() {
		IPackageFragment pack = choosePackage();
		ClassRef result = null;
		if (pack != null) {
			result = getClassRef(pack.getElementName());
			if(result == null) {
				result = getSubject().addSpecifiedClassRef(pack.getElementName());
			}
		}
		return result;
	}

	
	private ClassRef getClassRef(String className) {
		for ( ListIterator<ClassRef> i = getSubject().getSpecifiedClassRefs().iterator(); i.hasNext(); ) {
			ClassRef classRef = i.next();
			if( classRef.getClassName().equals(className)) {
				return classRef;
			}
		}
		return null;
	}

//	private boolean classRefExists(String className) {
//		return getClassRef(className) != null;
//	}

	private ExtendedAdapter buildAdapter() {
		return new ExtendedAdapter() {
			
//			@Override
//			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
//				addMappedClass(listSelectionModel);
//			}

//			@Override
//			public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
//				if (!super.enableOptionOnSelectionChange(listSelectionModel)) {
//					return false;
//				}
//
//				return findType((ClassRef) listSelectionModel.selectedValue()) != null;
//			}

//			@Override
//			public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
//				openMappedClass((ClassRef) listSelectionModel.selectedValue());
//			}

			@Override
			public void optionOnSelection(CollectionValueModel<ClassRef> selectedItemsModel) {
				Iterator<ClassRef> iterator = selectedItemsModel.iterator();
				if (iterator.hasNext()) {
					openMappedClass(iterator.next());
				}
			}

			//			@Override
//			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
//				for (Object item : listSelectionModel.selectedValues()) {
//					getSubject().removeSpecifiedClassRef((ClassRef) item);
//				}
//			}

//			@Override
//			public void addPackage(ObjectListSelectionModel listSelectionModel) {
//				addMappedPackage(listSelectionModel);
//			}

			@Override
			public ClassRef addNewItem() {
				return addMappedClass();
			}

			@Override
			public void removeSelectedItems(
					CollectionValueModel<ClassRef> selectedItemsModel) {
				Iterator<ClassRef> iterator = selectedItemsModel.iterator();
				while (iterator.hasNext()) {
					getSubject().removeSpecifiedClassRef(iterator.next());
				}
			}

			@Override
			public ClassRef addPackage(
					ModifiableCollectionValueModel<ClassRef> listSelectionModel) {
				return addMappedPackage();
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
					image = this.getImage(persistentType);
//					image = JpaMappingImageHelper.imageForTypeMapping(persistentType.getMappingKey());
				} else if (classRef instanceof PackageInfoRef){
					PackageInfoRef packageInfoRef = (PackageInfoRef)classRef;
					if (packageInfoRef.getJavaPackageInfo() != null){
						image = JavaPlugin.getImageDescriptorRegistry().get(JavaPluginImages.DESC_OBJS_PACKAGE);
					}
				}

				if (image != null) {
					return image;
				}
//				return JptJpaUiPlugin.instance().getImage(JptUiIcons.WARNING);
				
				return HibernateJptUIPlugin.getDefault().getImage("warning");
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

	private ModifiableCollectionValueModel<ClassRef> buildSelectedItemHolder() {
		return new SimpleCollectionValueModel<ClassRef>();
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
				"" //$NON-NLS-1$
			);
		}
		catch (JavaModelException e) {
			HibernateJptUIPlugin.logException(e);
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
				HibernateJptUIPlugin.logException(e);
			}
		}

		return null;
	}
	
	private IPackageFragment choosePackage() {
		return DialogSelectionHelper.choosePackage(getShell(),
				new IJavaProject[]{getJavaProject()},
				createPackageFilter(),
				Messages.HibernatePersistenceUnitClassesComposite_PackageSelectionDialog_title,
				JptCommonUiMessages.ClassChooserPane_dialogMessage);
	}
	
	protected IFilter createPackageFilter(){
		return new IFilter() {
			
			public boolean select(Object element) {
				if (element instanceof PackageFragment) {
					PackageFragment pf = (PackageFragment) element;
					return pf.getCompilationUnit(Hibernate.PACKAGE_INFO_JAVA).exists();

				}
				return false;
			}
		};
	}
	
	/*
	 * (non-Javadoc)
	 */
	@Override
	protected void initializeLayout(Composite container) {

		// Description
//		addMultiLineLabel(
//			container,
//			Messages.HibernatePersistenceUnitClassesComposite_ClassesComposite_message
//		);

		// List pane
		new AddMappingListPane<ClassRef>(
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
				HibernateJptUIPlugin.logException(e);
			}
			catch (JavaModelException e) {
				HibernateJptUIPlugin.logException(e);
			}
		}
	}

	private IJavaProject getJavaProject() {
		return getSubject().getJpaProject().getJavaProject();
	}
}
