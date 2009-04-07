/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.wizards;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

public class TreeToTableComposite extends Composite {

	static protected class NullableTextCellEditor extends TextCellEditor {
		public NullableTextCellEditor(Composite parent) {
			super( parent );
		}

		protected void doSetValue(Object value) {
			if(value==null) { value=""; } //$NON-NLS-1$
			super.doSetValue( value );
		}

		public Object doGetValue() {
			String str = (String) super.doGetValue();
			if(str==null || str.trim().length()==0) {
				return null;
			} else {
				return super.doGetValue();
			}
		}
	}


	static public class MultiStateCellEditor extends CellEditor {

		private int value;
		private final int maxStates;

		public MultiStateCellEditor(Composite parent, int stateCount, int initialValue) {
			super(parent);
			if(stateCount < 1) throw new IllegalArgumentException(HibernateConsoleMessages.TreeToTableComposite_incorrect_state_count);
			maxStates= stateCount;

			if(!(initialValue >= 0 && initialValue < stateCount)) throw new IllegalArgumentException(HibernateConsoleMessages.TreeToTableComposite_incorrect_initial_value);
			value= initialValue;

			setValueValid(true);
		}

		public void activate() {
			value= getNextValue(maxStates, value);
			fireApplyEditorValue();
		}

		public static int getNextValue(int stateCount, int currentValue){
			if(!(stateCount > 1)) throw new IllegalStateException(HibernateConsoleMessages.TreeToTableComposite_incorrect_state_count);
			if(!(currentValue >= 0 && currentValue < stateCount)) throw new IllegalStateException(HibernateConsoleMessages.TreeToTableComposite_incorrect_initial_value);
			return (currentValue + 1) % stateCount;
		}

		protected Control createControl(Composite parent) {
			return null;
		}
		protected Object doGetValue() {
			return Integer.valueOf(value);
		}

		protected void doSetFocus() {
			// Ignore
		}

		protected void doSetValue(Object value) {
			this.value = ((Integer) value).intValue();
			if(!(this.value >= 0 && this.value < maxStates)) {
				throw new IllegalStateException(HibernateConsoleMessages.TreeToTableComposite_invalid_value);
			}
		}
	}


	/** CellEditor that works like a texteditor, but returns/accepts Integer values. If the entered string is not parsable it returns null */
	static protected final class IntegerCellEditor extends NullableTextCellEditor {
		public IntegerCellEditor(Composite parent) {
			super( parent );
		}

		protected void doSetValue(Object value) {
			if(value!=null && value instanceof Integer) {
				value = ((Integer)value).toString();
			}
			super.doSetValue( value );
		}

		public Object doGetValue() {
			String str = (String) super.doGetValue();
			if(str==null || str.trim().length()==0) {
				return null;
			} else {
				try {
				return Integer.valueOf(Integer.parseInt((String) super.doGetValue()));
				} catch(NumberFormatException nfe) {
					return null;
				}
			}
		}
	}

	private Group dbgroup = null;
	private Composite manipulationGroup = null;
	protected Tree tree = null;
	private Group tableFiltersGroup = null;
	protected Table rightTable = null;
	private Button upButton = null;
	private Button downButton = null;
	private Button removeButton = null;
	private Button removeAllButton = null;
	private Label fillLabel = null;
	private Composite composite = null;
	private Label label = null;
	private Button refreshButton = null;
	private Label emptyLabel = null;
	private Button[] addButtons;
	private SelectionListener buttonListener= new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			Widget button = e.widget;
			for (int i = 0; i < addButtons.length; i++) {
				Button but = addButtons[i];
				if(button == but) {
				 handleAddButtonPressed(i);
				}
			}
		}
	};;

	public TreeToTableComposite(Composite parent, int style) {
		super( parent, style );
		initialize();
	}

	protected void handleAddButtonPressed(int i) {

	}


	protected void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		createDbgroup();
		this.setLayout(gridLayout);
		createIncludeExcludeGroup();
		createTableFiltersGroup();
		createComposite();
		setSize(new org.eclipse.swt.graphics.Point(913,358));
		label = new Label(this, SWT.NONE);
	}

	/**
	 * This method initializes dbgroup
	 *
	 */
	private void createDbgroup() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData.horizontalIndent = 0;
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		dbgroup = new Group( this, SWT.NONE );
		dbgroup.setText(getTreeTitle());
		dbgroup.setLayout(new FillLayout());
		createTree();
		dbgroup.setLayoutData(gridData);
	}

	protected String getTreeTitle() {
		return HibernateConsoleMessages.TreeToTableComposite_database_schema;
	}

	/**
	 * This method initializes includeExcludeGroup
	 *
	 */
	private void createIncludeExcludeGroup() {
		GridData gridData6 = new org.eclipse.swt.layout.GridData();
		gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData6.verticalSpan = 1;
		gridData6.horizontalSpan = 1;
		gridData6.grabExcessVerticalSpace = false;
		gridData6.heightHint = 24;
		gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridData gridData5 = new org.eclipse.swt.layout.GridData();
		gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData5.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridData4 = new org.eclipse.swt.layout.GridData();
		gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridData gridData3 = new org.eclipse.swt.layout.GridData();
		gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.marginWidth = 5;
		gridLayout1.marginHeight = 5;
		GridData gridData1 = new org.eclipse.swt.layout.GridData();
		gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.grabExcessHorizontalSpace = false;
		gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		manipulationGroup = new Composite( this, SWT.NONE );
		manipulationGroup.setLayoutData(gridData1);
		manipulationGroup.setLayout(gridLayout1);
		createAddButtons(manipulationGroup);
		fillLabel = new Label(manipulationGroup, SWT.NONE);
		fillLabel.setText(""); //$NON-NLS-1$
		fillLabel.setLayoutData(gridData6);
		upButton = new Button(manipulationGroup, SWT.NONE);
		upButton.setText(HibernateConsoleMessages.TreeToTableComposite_up);
		upButton.setLayoutData(gridData5);
		upButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				doMoveUp();
			}
		} );
		downButton = new Button(manipulationGroup, SWT.NONE);
		downButton.setText(HibernateConsoleMessages.TreeToTableComposite_down);
		downButton.setLayoutData(gridData4);
		downButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				doMoveDown();
			}
		} );
		removeButton = new Button(manipulationGroup, SWT.NONE);
		removeButton.setText(HibernateConsoleMessages.TreeToTableComposite_remove);
		removeButton.setLayoutData(gridData3);
		removeButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				doRemove();
			}
		});
		removeAllButton = new Button(manipulationGroup, SWT.NONE);
		removeAllButton.setText(HibernateConsoleMessages.TreeToTableComposite_remove_all);
		removeAllButton.setLayoutData(gridData3);
		removeAllButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				doRemoveAll();
			}
		});
	}

	private void createAddButtons(Composite parent) {

		String[] addButtonLabels = getAddButtonLabels();
		addButtons = new Button[addButtonLabels.length];
		for (int i = 0; i < addButtonLabels.length; i++) {
			String label = addButtonLabels[i];
			addButtons[i] = createButton(parent, label);
			addButtons[i].setEnabled(true);
		}
	}

	private Button createButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;

		button.setLayoutData(data);
		button.setFont(parent.getFont() );
		button.setText(label);
		button.setEnabled(false);
		button.addSelectionListener(buttonListener );
		return button;
	}

	protected String[] getAddButtonLabels() {
		return new String[] { HibernateConsoleMessages.TreeToTableComposite_include, HibernateConsoleMessages.TreeToTableComposite_exclude};
	}

	protected void doRemove() {
		// TODO Auto-generated method stub

	}

	protected void doRemoveAll() {
		// TODO Auto-generated method stub

	}

	protected void doMoveDown() {
		// TODO Auto-generated method stub

	}

	protected void doMoveUp() {
		// TODO Auto-generated method stub

	}

	/**
	 * This method initializes tree
	 *
	 */
	private void createTree() {
		tree = new Tree(dbgroup, SWT.MULTI);
	}

	/**
	 * This method initializes tableFiltersGroup
	 *
	 */
	private void createTableFiltersGroup() {
		GridData gridData2 = new org.eclipse.swt.layout.GridData();
		gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.horizontalSpan = 1;
		gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		tableFiltersGroup = new Group( this, SWT.NONE );
		tableFiltersGroup.setText(getTableTitle());
		tableFiltersGroup.setLayout(new FillLayout());
		createTableFilters();
		tableFiltersGroup.setLayoutData(gridData2);
	}

	protected String getTableTitle() {
		return HibernateConsoleMessages.TreeToTableComposite_table_filters;
	}

	/**
	 * This method initializes tableFilters
	 *
	 */
	private void createTableFilters() {
		rightTable = new Table(tableFiltersGroup, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
		rightTable.setHeaderVisible(true);
		rightTable.setLinesVisible(true);

		createTableColumns(rightTable);

	}

	protected void createTableColumns(Table table) {
	}

	/**
	 * This method initializes composite
	 *
	 */
	private void createComposite() {
		GridData gridData9 = new org.eclipse.swt.layout.GridData();
		gridData9.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData9.grabExcessHorizontalSpace = true;
		gridData9.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		GridData gridData8 = new org.eclipse.swt.layout.GridData();
		gridData8.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData8.grabExcessHorizontalSpace = false;
		gridData8.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		composite = new Composite( this, SWT.NONE );
		composite.setLayoutData(gridData8);
		composite.setLayout(gridLayout2);
		emptyLabel = new Label(composite, SWT.NONE);
		emptyLabel.setText(""); //$NON-NLS-1$
		emptyLabel.setLayoutData(gridData9);
		refreshButton = new Button(composite, SWT.NONE);
		refreshButton.setText(HibernateConsoleMessages.TreeToTableComposite_refresh);
		refreshButton
				.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						doRefreshTree();
					}
				} );
	}

	protected void doRefreshTree() {


	}

	public void setEnabled(boolean enabled) {
		upButton.setEnabled(enabled);
		downButton.setEnabled(enabled);
		removeButton.setEnabled(enabled);
		refreshButton.setEnabled(enabled);
		for (int i = 0; i < addButtons.length; i++) {
			addButtons[i].setEnabled(enabled);
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
