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

import java.util.Arrays;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jpt.ui.internal.JptUiMessages;
import org.eclipse.jpt.ui.internal.listeners.SWTListChangeListenerWrapper;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.model.value.swing.ListModelAdapter;
import org.eclipse.jpt.utility.internal.model.value.swing.ObjectListSelectionModel;
import org.eclipse.jpt.utility.model.Model;
import org.eclipse.jpt.utility.model.event.ListChangeEvent;
import org.eclipse.jpt.utility.model.listener.ListChangeListener;
import org.eclipse.jpt.utility.model.value.ListValueModel;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * The abstract definition of a pane that has buttons for adding, removing and
 * possibly editing the items.
 *
 * @see AddRemoveListPane
 *
 * @version 1.0
 * @since 2.0
 */
public abstract class AddRemovePane<T extends Model> extends AbstractPane<T>
{
	private Adapter adapter;
	private Button addButton;
	private Composite container;
	private boolean enabled;
	private IBaseLabelProvider labelProvider;
	private ListValueModel<?> listHolder;
	private Button optionalButton;
	private Button removeButton;
	private WritablePropertyValueModel<Object> selectedItemHolder;
	private ObjectListSelectionModel selectionModel;

	/**
	 * Creates a new <code>AddRemovePane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param parent The parent container
	 * @param adapter This <code>Adapter</code> is used to dictacte the behavior
	 * of this <code>AddRemovePane</code> and by delegating to it some of the
	 * behavior
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the list holder's items
	 */
	protected AddRemovePane(AbstractPane<? extends T> parentPane,
	                        Composite parent,
	                        Adapter adapter,
	                        ListValueModel<?> listHolder,
	                        WritablePropertyValueModel<?> selectedItemHolder,
	                        IBaseLabelProvider labelProvider) {

		this(parentPane,
		     parent,
		     adapter,
		     listHolder,
		     selectedItemHolder,
		     labelProvider,
		     null);
	}

	/**
	 * Creates a new <code>AddRemovePane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param parent The parent container
	 * @param adapter This <code>Adapter</code> is used to dictacte the behavior
	 * of this <code>AddRemovePane</code> and by delegating to it some of the
	 * behavior
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the list holder's items
	 * @param helpId The topic help ID to be registered with this pane
	 */
	protected AddRemovePane(AbstractPane<? extends T> parentPane,
	                        Composite parent,
	                        Adapter adapter,
	                        ListValueModel<?> listHolder,
	                        WritablePropertyValueModel<?> selectedItemHolder,
	                        IBaseLabelProvider labelProvider,
	                        String helpId) {

		super(parentPane, parent);

		initialize(
			adapter,
			listHolder,
			selectedItemHolder,
			labelProvider
		);

		initializeLayout(
			adapter,
			listHolder,
			selectedItemHolder,
			labelProvider,
			helpId
		);
	}

	/**
	 * Creates a new <code>AddRemovePane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param subjectHolder The holder of the subject
	 * @param adapter This <code>Adapter</code> is used to dictacte the behavior
	 * of this <code>AddRemovePane</code> and by delegating to it some of the
	 * behavior
	 * @param parent The parent container
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the list holder's items
	 */
	protected AddRemovePane(AbstractPane<?> parentPane,
	                        PropertyValueModel<? extends T> subjectHolder,
	                        Composite parent,
	                        Adapter adapter,
	                        ListValueModel<?> listHolder,
	                        WritablePropertyValueModel<?> selectedItemHolder,
	                        IBaseLabelProvider labelProvider) {

		this(parentPane,
		     subjectHolder,
		     parent,
		     adapter,
		     listHolder,
		     selectedItemHolder,
		     labelProvider,
		     null);
	}

	/**
	 * Creates a new <code>AddRemovePane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param subjectHolder The holder of the subject
	 * @param adapter This <code>Adapter</code> is used to dictacte the behavior
	 * of this <code>AddRemovePane</code> and by delegating to it some of the
	 * behavior
	 * @param parent The parent container
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the list holder's items
	 * @param helpId The topic help ID to be registered with this pane
	 */
	protected AddRemovePane(AbstractPane<?> parentPane,
	                        PropertyValueModel<? extends T> subjectHolder,
	                        Composite parent,
	                        Adapter adapter,
	                        ListValueModel<?> listHolder,
	                        WritablePropertyValueModel<?> selectedItemHolder,
	                        IBaseLabelProvider labelProvider,
	                        String helpId) {

		super(parentPane, subjectHolder, parent);

		initialize(
			adapter,
			listHolder,
			selectedItemHolder,
			labelProvider
		);

		initializeLayout(
			adapter,
			listHolder,
			selectedItemHolder,
			labelProvider,
			helpId
		);
	}

	/**
	 * Gives the possibility to add buttons after the Add button and before the
	 * optional button.
	 *
	 * @param container The parent container
	 * @param helpId The topic help ID to be registered with the buttons
	 *
	 * @category Layout
	 */
	protected void addCustomButtonAfterAddButton(Composite container,
	                                             String helpId) {
	}

	/**
	 * Gives the possibility to add buttons after the optional button and before
	 * the Remove button.
	 *
	 * @param container The parent container
	 * @param helpId The topic help ID to be registered with the buttons
	 *
	 * @category Layout
	 */
	protected void addCustomButtonAfterOptionalButton(Composite container,
	                                                  String helpId) {
	}

	/**
	 * @category Add
	 */
	protected void addItem() {
		adapter.addNewItem(selectionModel);
	}

	/**
	 * @category Initialize
	 */
	protected Adapter buildAdapter() {
		return adapter;
	}

	/**
	 * @category Add
	 */
	protected Button buildAddButton(Composite parent) {
		return buildButton(
			parent,
			adapter.addButtonText(),
			buildAddItemAction()
		);
	}

	/**
	 * @category Add
	 */
	private Runnable buildAddItemAction() {
		return new Runnable() {
			public void run() {
				AddRemovePane.this.addItem();
			}
		};
	}

	private ListChangeListener buildListChangeListener() {
		return new SWTListChangeListenerWrapper(buildListChangeListener_());
	}

	private ListChangeListener buildListChangeListener_() {
		return new ListChangeListener() {

			public void itemsAdded(ListChangeEvent e) {
			}

			public void itemsMoved(ListChangeEvent e) {
			}

			public void itemsRemoved(ListChangeEvent e) {
				Object selectedItem = selectedItemHolder.getValue();

				if (selectedItem == null) {
					updateButtons();
					return;
				}

				if (CollectionTools.contains(e.items(), selectedItem)) {
					selectedItemHolder.setValue(null);
					updateButtons();
				}
			}

			public void itemsReplaced(ListChangeEvent e) {
			}

			public void listChanged(ListChangeEvent e) {
			}

			public void listCleared(ListChangeEvent e) {
				selectedItemHolder.setValue(null);
				updateButtons();
			}
		};
	}

	/**
	 * @category Option
	 */
	private Runnable buildOptionalAction() {
		return new Runnable() {
			public void run() {
				AddRemovePane.this.editItem();
			}
		};
	}

	/**
	 * @category Option
	 */
	protected Button buildOptionalButton(Composite container) {
		return buildButton(
			container,
			adapter.optionalButtonText(),
			buildOptionalAction()
		);
	}

	/**
	 * @category Add
	 */
	protected Button buildRemoveButton(Composite parent) {
		return buildButton(
			parent,
			adapter.removeButtonText(),
			buildRemoveItemsAction()
		);
	}

	/**
	 * @category Remove
	 */
	private Runnable buildRemoveItemsAction() {
		return new Runnable() {
			public void run() {
				AddRemovePane.this.removeItems();
			}
		};
	}

	protected ObjectListSelectionModel buildRowSelectionModel(ListValueModel<?> listModel) {
		return new ObjectListSelectionModel(new ListModelAdapter(listModel));
	}

	/**
	 * @category Option
	 */
	protected void editItem() {
		this.adapter.optionOnSelection(getSelectionModel());
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public void enableWidgets(boolean enabled) {

		super.enableWidgets(enabled);
		this.enabled = enabled;

		if (!this.getMainControl().isDisposed()) {
			this.getMainControl().setEnabled(enabled);
		}

		this.updateButtons();
	}

	protected final Composite getContainer() {
		return container;
	}

	protected IBaseLabelProvider getLabelProvider() {
		return labelProvider;
	}

	protected final ListValueModel<?> getListHolder() {
		return listHolder;
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	public abstract Composite getMainControl();

	protected final WritablePropertyValueModel<Object> getSelectedItemHolder() {
		return selectedItemHolder;
	}

	public final ObjectListSelectionModel getSelectionModel() {
		return selectionModel;
	}

	/**
	 * Initializes this add/remove pane.
	 *
	 * @param adapter This <code>Adapter</code> is used to dictacte the behavior
	 * of this <code>AddRemovePane</code> and by delegating to it some of the
	 * behavior
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the list holder's items
	 *
	 * @category Initialization
	 */
	@SuppressWarnings("unchecked")
	protected void initialize(Adapter adapter,
	                          ListValueModel<?> listHolder,
	                          WritablePropertyValueModel<?> selectedItemHolder,
	                          IBaseLabelProvider labelProvider)
	{
		this.listHolder         = listHolder;
		this.labelProvider      = labelProvider;
		this.adapter            = (adapter == null) ? buildAdapter() : adapter;
		this.selectedItemHolder = (WritablePropertyValueModel<Object>) selectedItemHolder;
		this.selectionModel     = new ObjectListSelectionModel(new ListModelAdapter(listHolder));

		this.listHolder.addListChangeListener(
			ListValueModel.LIST_VALUES,
			buildListChangeListener()
		);
	}

	/**
	 * Initializes the pane containing the buttons (Add, optional (if required)
	 * and Remove).
	 *
	 * @param container The parent container
	 * @param helpId The topic help ID to be registered with the buttons
	 *
	 * @category Layout
	 */
	protected void initializeButtonPane(Composite container, String helpId) {

		container = buildSubPane(container);

		GridData gridData = new GridData();
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment       = SWT.TOP;
		container.setLayoutData(gridData);

		// Add button
		addButton = buildAddButton(container);
		addAlignRight(addButton);
		removeFromEnablementControl(addButton);

		// Custom button
		addCustomButtonAfterAddButton(container, helpId);

		// Optional button
		if (adapter.hasOptionalButton()) {
			optionalButton = buildOptionalButton(container);
			removeFromEnablementControl(optionalButton);
			addAlignRight(optionalButton);
		}

		// Custom button
		addCustomButtonAfterOptionalButton(container, helpId);

		// Remove button
		removeButton = buildRemoveButton(container);
		removeFromEnablementControl(removeButton);
		addAlignRight(removeButton);

		// Update the help topic ID
		if (helpId != null) {
			helpSystem().setHelp(addButton, helpId);
			helpSystem().setHelp(removeButton, helpId);

			if (optionalButton != null) {
				helpSystem().setHelp(optionalButton, helpId);
			}
		}
	}

	/**
	 * Initializes this add/remove pane by creating the widgets. The subclass is
	 * required to build the main widget.
	 *
	 * @param adapter This <code>Adapter</code> is used to dictacte the behavior
	 * of this <code>AddRemovePane</code> and by delegating to it some of the
	 * behavior
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the list holder's items
	 * @param helpId The topic help ID to be registered with this pane
	 *
	 * @category Layout
	 */
	protected void initializeLayout(Adapter adapter,
    	                             ListValueModel<?> listHolder,
   	                             WritablePropertyValueModel<?> selectedItemHolder,
   	                             IBaseLabelProvider labelProvider,
   	                             String helpId) {

		initializeMainComposite(
			container,
			adapter,
			listHolder,
			selectedItemHolder,
			labelProvider,
			helpId);

		initializeButtonPane(container, helpId);
		enableWidgets(subject() != null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeLayout(Composite container) {
		this.container = buildSubPane(container, 2, 0, 0, 0, 0);
	}

	/**
	 * Initializes the main widget of this add/remove pane.
	 *
	 * @param container The parent container
	 * @param adapter This <code>Adapter</code> is used to dictacte the behavior
	 * of this <code>AddRemovePane</code> and by delegating to it some of the
	 * behavior
	 * @param listHolder The <code>ListValueModel</code> containing the items
	 * @param selectedItemHolder The holder of the selected item, if more than
	 * one item or no items are selected, then <code>null</code> will be passed
	 * @param labelProvider The renderer used to format the list holder's items
	 * @param helpId The topic help ID to be registered with this pane or
	 * <code>null</code> if it was not specified
	 *
	 * @category Layout
	 */
	protected abstract void initializeMainComposite(Composite container,
	                                                Adapter adapter,
	                   	                           ListValueModel<?> listHolder,
	                  	                           WritablePropertyValueModel<?> selectedItemHolder,
	                  	                           IBaseLabelProvider labelProvider,
	                  	                           String helpId);

	/**
	 * @category Remove
	 */
	protected void removeItems() {

		// Keep track of the selected indices so we can select an item
		// before the lowest index
		int[] indices = selectionModel.selectedIndices();
		Arrays.sort(indices);

		// Notify the adapter to remove the selected items
		adapter.removeSelectedItems(selectionModel);

		// Select a new item
		if (getListHolder().size() > 0) {
			int index = Math.min(indices[0], getListHolder().size() - 1);
			Object item = getListHolder().get(index);
			selectedItemHolder.setValue(item);
		}
		// The list is empty, clear the value
		else {
			selectedItemHolder.setValue(null);
		}
	}

	/**
	 * Selects the given value, which can be <code>null</code>.
	 *
	 * @param value The new selected value
	 */
	public void setSelectedItem(Object value) {
		selectedItemHolder.setValue(value);
	}

	/**
	 * @category UpdateButtons
	 */
	protected void updateAddButton(Button addButton) {
		addButton.setEnabled(
			enabled &&
			subject() != null
		);
	}

	/**
	 * @category UpdateButtons
	 */
	protected void updateButtons() {
		if (!container.isDisposed()) {
			updateAddButton(addButton);
			updateRemoveButton(removeButton);
			updateOptionalButton(optionalButton);
		}
	}

	/**
	 * @category UpdateButtons
	 */
	protected void updateOptionalButton(Button optionalButton) {
		if (optionalButton != null) {
			optionalButton.setEnabled(
				enabled &&
				adapter.enableOptionOnSelectionChange(selectionModel)
			);
		}
	}

	/**
	 * @category UpdateButtons
	 */
	protected void updateRemoveButton(Button removeButton) {
		removeButton.setEnabled(
			enabled &&
			adapter.enableRemoveOnSelectionChange(selectionModel)
		);
	}

	/**
	 * An abstract implementation of <code>Adapter</code>.
	 */
	public static abstract class AbstractAdapter implements Adapter {

		/**
		 * The text of the add button.
		 */
		private String addButtonText;

		/**
		 * Determines whether the optional button should be shown or not.
		 */
		private boolean hasOptionalButton;

		/**
		 * The text of the optional button, if used.
		 */
		private String optionalButtonText;

		/**
		 * The text of the remove button.
		 */
		private String removeButtonText;

		/**
		 * Creates a new <code>AbstractAdapter</code> with default text for the
		 * add and remove buttons.
		 */
		public AbstractAdapter() {
			this(JptUiMessages.AddRemovePane_AddButtonText,
			     JptUiMessages.AddRemovePane_RemoveButtonText);
		}

		/**
		 * Creates a new <code>AbstractAdapter</code> with default text for the
		 * add and remove buttons.
		 *
		 * @param hasOptionalButton <code>true</code> to show an optional button
		 * and to use the behavior related to the optional button;
		 * <code>false</code> to not use it
		 */
		public AbstractAdapter(boolean hasOptionalButton) {
			this();
			this.setHasOptionalButton(hasOptionalButton);
		}

		/**
		 * Creates a new <code>AbstractAdapter</code> with default text for the
		 * add and remove buttons.
		 *
		 * @param optionalButtonText The text of the optional button, which means
		 * the optional button will be shown
		 */
		public AbstractAdapter(String optionalButtonText) {
			this(true);
			this.setOptionalButtonText(optionalButtonText);
		}

		/**
		 * Creates a new <code>AbstractAdapter</code>.
		 *
		 * @param addButtonText The add button's text
		 * @param removeButtonText The remove button's text
		 */
		public AbstractAdapter(String addButtonText,
		                       String removeButtonText) {

			super();
			this.addButtonText    = addButtonText;
			this.removeButtonText = removeButtonText;
		}

		/**
		 * Creates a new <code>AbstractAdapter</code>.
		 *
		 * @param addButtonText The add button's text
		 * @param removeButtonText The remove button's text
		 * @param optionalButtonText The text of the optional button, which means
		 * the optional button will be shown
		 */
		public AbstractAdapter(String addButtonText,
		                       String removeButtonText,
		                       String optionalButtonText) {

			this(optionalButtonText);
			this.setAddButtonText(addButtonText);
			this.setRemoveButtonText(removeButtonText);
		}

		/*
		 * (non-Javadoc)
		 */
		public String addButtonText() {
			return addButtonText;
		}

		/*
		 * (non-Javadoc)
		 */
		public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
			return listSelectionModel.selectedValuesSize() == 1;
		}

		public boolean enableRemoveOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
			return listSelectionModel.selectedValue() != null;
		}

		/*
		 * (non-Javadoc)
		 */
		public boolean hasOptionalButton() {
			return hasOptionalButton;
		}

		/*
		 * (non-Javadoc)
		 */
		public String optionalButtonText() {
			return optionalButtonText;
		}

		/*
		 * (non-Javadoc)
		 */
		public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
		}

		/*
		 * (non-Javadoc)
		 */
		public String removeButtonText() {
			return removeButtonText;
		}

		/**
		 * Changes the text of the add button. This method has to be called before
		 * the <code>AddRemoveListPane</code> is initialized.
		 *
		 * @param addButtonText The add button's text
		 */
		public void setAddButtonText(String addButtonText) {
			this.addButtonText = addButtonText;
		}

		/**
		 * Changes the state of the optional button, meaning if it should be shown
		 * between the add and remove buttons or not.
		 *
		 * @param hasOptionalButton <code>true</code> to show an optional button
		 * and to use the behavior related to the optional button;
		 * <code>false</code> to not use it
		 */
		public void setHasOptionalButton(boolean hasOptionalButton) {
			this.hasOptionalButton = hasOptionalButton;
		}

		/**
		 * Changes the text of the optional button. This method has to be called
		 * before the <code>AddRemoveListPane</code> is initialized. This does not
		 * make the optional button visible.
		 *
		 * @param optionalButtonText The optional button's text
		 */
		public void setOptionalButtonText(String optionalButtonText) {
			this.optionalButtonText = optionalButtonText;
		}

		/**
		 * Changes the text of the remove button. This method has to be called
		 * before the <code>AddRemoveListPane</code> is initialized.
		 *
		 * @param removeButtonText The remove button's text
		 */
		public void setRemoveButtonText(String removeButtonText) {
			this.removeButtonText = removeButtonText;
		}
	}

	/**
	 * This adapter is used to perform the actual action when adding a new item
	 * or removing the selected items. It is possible to add an optional button.
	 */
	public static interface Adapter {

		/**
		 * The add button's text.
		 *
		 * @return The text shown on the add button
		 */
		String addButtonText();

		/**
		 * Invoked when the user selects the Add button.
		 */
		void addNewItem(ObjectListSelectionModel listSelectionModel);

		/**
		 * Invoked when selection changes. Implementation dictates whether button
		 * should be enabled.
		 */
		boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel);

		/**
		 * Invoked when selection changes. Implementation dictates whether remove button
		 * should be enabled.
		 */
		boolean enableRemoveOnSelectionChange(ObjectListSelectionModel listSelectionModel);

		/**
		 * Determines whether an optional button should be added between the add
		 * and remove buttons.
		 *
		 * @return <code>true</code> to show an optional button and to use the
		 * behavior related to the optional button; <code>false</code> to not use
		 * it
		 */
		boolean hasOptionalButton();

		/**
		 * Resource string key for the optional button.
		 */
		String optionalButtonText();

		/**
		 * Invoked when the user selects the optional button
		 */
		void optionOnSelection(ObjectListSelectionModel listSelectionModel);

		/**
		 * The remove button's text.
		 *
		 * @return The text shown on the remove button
		 */
		String removeButtonText();

		/**
		 * Invoked when the user selects the Remove button.
		 */
		void removeSelectedItems(ObjectListSelectionModel listSelectionModel);
	}
}