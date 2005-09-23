package org.hibernate.eclipse.mapper.editors.reveng;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.IRevEngTable;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.workbench.AnyAdaptableLabelProvider;
import org.hibernate.eclipse.console.workbench.DeferredContentProvider;
import org.hibernate.eclipse.console.workbench.LazyDatabaseSchema;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;
import org.hibernate.eclipse.mapper.model.RevEngColumnAdapter;
import org.hibernate.eclipse.mapper.model.RevEngTableAdapter;
import org.hibernate.mapping.Table;

public class TablePropertiesBlock extends MasterDetailsBlock {
	
	private TreeViewer viewer;
	private final ReverseEngineeringEditor editor;

	public TablePropertiesBlock(ReverseEngineeringEditor editor) {
		this.editor = editor;		
	}

	public SashForm getComposite() {
		return sashForm;
	}
	
	protected void createMasterPart(final IManagedForm managedForm,	Composite parent) {
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true));
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		Section section = toolkit.createSection( parent, Section.DESCRIPTION );
		section.setText( "Tables && Columns" );
		section.setDescription( "Explicitly control settings for table && columns for which the defaults is not applicable. Click Add, select the relevant tables && columns and adjust their settings here." );
		section.marginWidth = 10;
		section.marginHeight = 5;
		toolkit.createCompositeSeparator( section );
		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		client.setLayout( layout );
		Tree t = toolkit.createTree( client, SWT.NULL );
		GridData gd = new GridData( GridData.FILL_BOTH );
		gd.heightHint = 20;
		gd.widthHint = 100;
		t.setLayoutData( gd );
		toolkit.paintBordersFor( client );
		Button b = toolkit.createButton( client, "Add...", SWT.PUSH );
		b.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				doAdd();
			}
		
		});
		gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
		b.setLayoutData( gd );
		section.setClient( client );
		final SectionPart spart = new SectionPart( section ) {
			public boolean setFormInput(Object input) {
				if(input instanceof IReverseEngineeringDefinition) {
					viewer.setInput(input);
					return true;
				} 
				return false;
			}
		};
		managedForm.addPart( spart );
		viewer = new TreeViewer( t );
		viewer.addSelectionChangedListener( new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged( spart, event.getSelection() );
			}
		} );
		viewer.setLabelProvider( new TablePropertiesLabelProvider() );
		TablePropertiesContentProvider tablePropertiesContentProvider = new TablePropertiesContentProvider();
		viewer.setContentProvider( tablePropertiesContentProvider );		
	}

	protected void doAdd() {
		CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(getComposite().getShell(), new AnyAdaptableLabelProvider(), new DeferredContentProvider()) {
			
			protected Composite createSelectionButtons(Composite composite) {
		        Composite buttonComposite = new Composite(composite, SWT.RIGHT);
		        GridLayout layout = new GridLayout();
		        layout.numColumns = 2;
		        buttonComposite.setLayout(layout);
		        buttonComposite.setFont(composite.getFont());
		        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END
		                | GridData.GRAB_HORIZONTAL);
		        data.grabExcessHorizontalSpace = true;
		        composite.setData(data);
		        Button selectButton = createButton(buttonComposite,
		                IDialogConstants.SELECT_ALL_ID, "Select all children",
		                false);
		        SelectionListener listener = new SelectionAdapter() {
		            public void widgetSelected(SelectionEvent e) {
		                IStructuredSelection viewerElements = (IStructuredSelection) getTreeViewer().getSelection();
		                Iterator iterator = viewerElements.iterator();
		                while(iterator.hasNext()) {
		                	getTreeViewer().setSubtreeChecked(iterator.next(), true);
		                }	                            
		                updateOKStatus();
		            }
		        };
		        selectButton.addSelectionListener(listener);
		        Button deselectButton = createButton(buttonComposite,
		                IDialogConstants.DESELECT_ALL_ID, WorkbenchMessages.CheckedTreeSelectionDialog_deselect_all,
		                false);
		        listener = new SelectionAdapter() {
		            public void widgetSelected(SelectionEvent e) {
		                getTreeViewer().setCheckedElements(new Object[0]);
		                updateOKStatus();
		            }
		        };
		        deselectButton.addSelectionListener(listener);
		        return buttonComposite;
		    }

			
		};
		
		ConsoleConfiguration configuration = KnownConfigurations.getInstance().find( editor.getConsoleConfigurationName() );
		
		if(configuration!=null) {
			dialog.setInput(new LazyDatabaseSchema(configuration));
			dialog.setContainerMode(true);
			dialog.open();
			Object[] result = dialog.getResult();
			for (int i = 0; i < result.length; i++) {
				Object object = result[i];
				if(object instanceof Table) {
					Table table = (Table) object;
					IRevEngTable retable = null;
					//	editor.getReverseEngineeringDefinition().findTable(TableIdentifier.create(table));
					if(retable==null) {
						retable = editor.getReverseEngineeringDefinition().createTable();
						retable.setCatalog(table.getCatalog());
						retable.setSchema(table.getSchema());
						retable.setName(table.getName());
						editor.getReverseEngineeringDefinition().addTable(retable);
					}
				}
			}
			
			//editor.getReverseEngineeringDefinition();
		}
		
		
	}

	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();

		Action haction = new Action( "hor", IAction.AS_RADIO_BUTTON ) {
			public void run() {
				sashForm.setOrientation( SWT.HORIZONTAL );
				form.reflow( true );
			}
		};
		haction.setChecked( true );
		haction.setToolTipText( "Horizontal orientation" );
		Action vaction = new Action( "ver", IAction.AS_RADIO_BUTTON ) {
			public void run() {
				sashForm.setOrientation( SWT.VERTICAL );
				form.reflow( true );
			}
		};
		vaction.setChecked( false );
		vaction.setToolTipText( "Vertical orientation" );
		form.getToolBarManager().add( haction );
		form.getToolBarManager().add( vaction );
	}

	protected void registerPages(DetailsPart dp) {
		dp.registerPage( RevEngColumnAdapter.class, new ColumnDetailsPage() );
		dp.registerPage( RevEngTableAdapter.class, new TableDetailsPage() );
		//dp.registerPage( org.hibernate.mapping.Table.class, new TypeOneDetailsPage() );
	}
}