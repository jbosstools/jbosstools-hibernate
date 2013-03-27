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

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemoveListPane;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.ui.internal.widgets.AddRemovePane.Adapter;
import org.eclipse.jpt.common.utility.model.value.ListValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiableCollectionValueModel;
import org.eclipse.jpt.jpa.core.context.persistence.ClassRef;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.ui.persistence.JptJpaUiPersistenceMessages;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * 
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class AddMappingListPane<E extends Object> extends AddRemoveListPane<PersistenceUnit, E> {
	
	private Button addButton;
	
	private ExtendedAdapter adapter;
	
	public AddMappingListPane(Pane<? extends PersistenceUnit> parentPane,
            Composite parent,
            Adapter<E> adapter,
            ListValueModel<E> listHolder,
            ModifiableCollectionValueModel<E> selectedItemHolder,
            ILabelProvider labelProvider,
            String helpId) {
		super(parentPane, parent, adapter, listHolder, selectedItemHolder, labelProvider, helpId);
	}
	
	@Override
	protected void initialize(Adapter<E> adapter,
            ListValueModel<?> listHolder,
            ModifiableCollectionValueModel<E> selectedItemsModel,
            IBaseLabelProvider labelProvider) {
		super.initialize(adapter, listHolder, selectedItemsModel, labelProvider);
		this.adapter = (ExtendedAdapter) adapter;
	}
	
	@Override
	protected void addCustomButtonAfterAddButton(Composite container,
			String helpId) {
		// Add package button
		this.addButton = addUnmanagedButton(container,
				adapter.addPackageButtonText(),
				buildAddPackageItemAction()
			);
//		addAlignRight(this.addButton);
	}

	private Runnable buildAddPackageItemAction() {
		return new Runnable() {
			public void run() {
				adapter.addPackage((ModifiableCollectionValueModel<ClassRef>) getSelectedItemsModel());
			}
		};
	}

}

abstract class ExtendedAdapter extends AddRemoveListPane.AbstractAdapter<ClassRef> {
	
	@Override
	public String addButtonText() {
		return Messages.AddMappingListPane_Add_Class;
	}
	
	public String addPackageButtonText() {
		return Messages.AddMappingListPane_Add_Package;
	}
	
	@Override
	public boolean hasOptionalButton() {
		return true;
	}

	@Override
	public String optionalButtonText() {
		return JptJpaUiPersistenceMessages.PERSISTENCE_UNIT_CLASSES_COMPOSITE_OPEN;
	}

	public abstract ClassRef addPackage(ModifiableCollectionValueModel<ClassRef> listSelectionModel);
	
}
