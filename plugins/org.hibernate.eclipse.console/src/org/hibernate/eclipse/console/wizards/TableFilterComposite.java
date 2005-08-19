package org.hibernate.eclipse.console.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;

public class TableFilterComposite extends Composite {

	private Group dbgroup = null;
	private Composite includeExcludeGroup = null;
	private Button excludeButton = null;
	private Button includeButton = null;
	protected Tree databaseTree = null;
	private Group tableFiltersGroup = null;
	protected Table tableFilters = null;
	private Button upButton = null;
	private Button downButton = null;
	private Button removeButton = null;
	private Label fillLabel = null;
	private UpDownList upDown;
	private Composite composite = null;
	private Label label = null;
	private Button refreshButton = null;
	private Label emptyLabel = null;

	public TableFilterComposite(Composite parent, int style) {
		super( parent, style );
		initialize();
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
		dbgroup.setText("Database schema:");
		dbgroup.setLayout(new FillLayout());
		createDatabaseTree();
		dbgroup.setLayoutData(gridData);
	}

	/**
	 * This method initializes includeExcludeGroup	
	 *
	 */
	private void createIncludeExcludeGroup() {
		GridData gridData7 = new org.eclipse.swt.layout.GridData();
		gridData7.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData7.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
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
		includeExcludeGroup = new Composite( this, SWT.NONE );
		includeExcludeGroup.setLayoutData(gridData1);
		includeExcludeGroup.setLayout(gridLayout1);
		excludeButton = new Button(includeExcludeGroup, SWT.NONE);
		excludeButton.setText("Exclude...");
		excludeButton
				.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						doExclusion();
					}
				} );		
		includeButton = new Button(includeExcludeGroup, SWT.NONE);
		includeButton.setText("Include...");
		includeButton.setLayoutData(gridData7);
		includeButton
				.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						doInclusion();
					}
				} );
		fillLabel = new Label(includeExcludeGroup, SWT.NONE);
		fillLabel.setText("");
		fillLabel.setLayoutData(gridData6);
		upButton = new Button(includeExcludeGroup, SWT.NONE);
		upButton.setText("Up");
		upButton.setLayoutData(gridData5);
		upButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				doMoveUp();
			}
		} );
		downButton = new Button(includeExcludeGroup, SWT.NONE);
		downButton.setText("Down");
		downButton.setLayoutData(gridData4);
		downButton.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				doMoveDown();
			}
		} );
		removeButton = new Button(includeExcludeGroup, SWT.NONE);
		removeButton.setText("Remove");
		removeButton.setLayoutData(gridData3);
		removeButton
				.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						doRemove();
					}
				} );
	}

	protected void doRemove() {
		// TODO Auto-generated method stub
		
	}

	protected void doMoveDown() {
		// TODO Auto-generated method stub
		
	}

	protected void doMoveUp() {
		// TODO Auto-generated method stub
		
	}

	protected void doInclusion() {
		// TODO Auto-generated method stub
		
	}

	protected void doExclusion() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method initializes databaseTree	
	 *
	 */
	private void createDatabaseTree() {
		databaseTree = new Tree(dbgroup, SWT.MULTI);
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
		tableFiltersGroup.setText("Table filters:");
		tableFiltersGroup.setLayout(new FillLayout());
		createTableFilters();
		tableFiltersGroup.setLayoutData(gridData2);
	}

	/**
	 * This method initializes tableFilters	
	 *
	 */
	private void createTableFilters() {
		tableFilters = new Table(tableFiltersGroup, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
		tableFilters.setHeaderVisible(true);
		tableFilters.setLinesVisible(true);
				
		TableColumn column = new TableColumn(tableFilters, SWT.CENTER, 0);		
		column.setText("!");
		column.setWidth(20);
		
		column = new TableColumn(tableFilters, SWT.LEFT, 1);
		column.setText("Catalog");
		column.setWidth(100);
		
		column = new TableColumn(tableFilters, SWT.LEFT, 2);
		column.setText("Schema");
		column.setWidth(100);

		column = new TableColumn(tableFilters, SWT.LEFT, 3);
		column.setText("Table");
		column.setWidth(100);

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
		emptyLabel.setText("");
		emptyLabel.setLayoutData(gridData9);
		refreshButton = new Button(composite, SWT.NONE);
		refreshButton.setText("Refresh");
		refreshButton
				.addSelectionListener( new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						doRefreshDatabaseSchema();					
					}
				} );
	}

	protected void doRefreshDatabaseSchema() {
		// TODO Auto-generated method stub
		
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
