package org.hibernate.eclipse.launch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.internal.ui.preferences.ScrolledPageContent;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.model.impl.ExporterInstance;
import org.hibernate.eclipse.console.model.impl.ExporterProperty;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class ExporterSettings extends AbstractLaunchConfigurationTab {
	private Button enableEJB3annotations;

	private Button enableJDK5;

	private LaunchAttributes attributes;

	private ExpansionListener expansionListener = new ExpansionListener();

	private ExporterInstance selectedExporter;

	private TableViewer exporterTable, exporterProperties;

	private Button addExporterButton;

	private Button removeExporterButton;
    
    private Button addPropertyButton, removePropertyButton;

    private static final String COLUMN_PROPERTY_NAME = "propertyname";
    private static final String COLUMN_PROPERTY_VALUE = "propertyvalue";
    
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ExporterSettings() {
		super();
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
//		selectedExporters = new ArrayList();
		
		// initializeDialogUnits(parent);
		ScrolledPageContent sc = new ScrolledPageContent( parent );
		Composite container = sc.getBody();
		// Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		container.setLayout( layout );
		// layout.numColumns = 1;
		// layout.verticalSpacing = 10;

		SelectionListener fieldlistener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected( e );
			}

			public void widgetSelected(SelectionEvent e) {
				dialogChanged();
			}
		};

		Composite generalSettingsComposite = createExpandableComposite(
				container, "General settings", true );
		Composite exportersComposite = createExpandableComposite( container,
				"Exporters", true );
		Composite exporterPropertiesComposite = createExpandableComposite( container,
				"Exporter Properties", true);
		
		enableJDK5 = new Button( generalSettingsComposite, SWT.CHECK );
		enableJDK5.setText( "Use Java 5 syntax" );
		enableJDK5.addSelectionListener( fieldlistener );

		enableEJB3annotations = new Button( generalSettingsComposite, SWT.CHECK );
		enableEJB3annotations.setText( "Generate EJB3 annotations" );
		enableEJB3annotations.addSelectionListener( fieldlistener );

		Composite exporterOptions = new Composite( exportersComposite, SWT.NONE );
		exporterOptions.setLayout( new GridLayout( 2, false ) );
        exporterOptions.setLayoutData( new GridData( SWT.FILL, SWT.FILL,
                true, true ) );
        
		Table table = new Table( exporterOptions, SWT.BORDER
				| SWT.V_SCROLL );
		exporterTable = new TableViewer( table );
		exporterTable.setContentProvider( new ExporterContentProvider() );
		exporterTable.setLabelProvider( new ExporterLabelProvider() );
		exporterTable.getControl().setLayoutData(
				new GridData( SWT.FILL, SWT.FILL, true, true ) );
		exporterTable.setColumnProperties( new String[] { "", "Description" } );


		Composite listActionsComposite = new Composite( exporterOptions,
				SWT.NONE );
		listActionsComposite.setLayout( new GridLayout( 1, true ) );
		listActionsComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL,
				true, true ) );
		
        addExporterButton = new Button(listActionsComposite, SWT.PUSH);
        addExporterButton.setText("Add Exporter...");
        
        removeExporterButton = new Button(listActionsComposite, SWT.PUSH);
        removeExporterButton.setText("Remove Exporter");

        Composite propertyOptions = new Composite(exporterPropertiesComposite, SWT.NONE);
        propertyOptions.setLayout( new GridLayout( 2, false ) );
        propertyOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        Table exporterPropertiesTable = new Table(propertyOptions, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
        TableLayout tableLayout = new TableLayout();
        exporterPropertiesTable.setLayout(tableLayout);
        
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
        exporterPropertiesTable.setLayoutData(data);
        
        exporterProperties = new TableViewer(exporterPropertiesTable);
        exporterProperties.setContentProvider(new ExporterContentProvider());
        exporterProperties.setLabelProvider(new ExporterPropertiesLabelProvider());
        exporterProperties.setColumnProperties(new String[] { COLUMN_PROPERTY_NAME, COLUMN_PROPERTY_VALUE } );
        exporterProperties.setCellModifier(new ExporterPropertiesCellModifier(exporterProperties));
        exporterProperties.setCellEditors(new CellEditor[] { new TextCellEditor(exporterPropertiesTable), new TextCellEditor(exporterPropertiesTable) } );

        exporterPropertiesTable.setHeaderVisible(true);
        exporterPropertiesTable.setLinesVisible(true);
        exporterPropertiesTable.setEnabled(false);
        
        tableLayout.addColumnData(new ColumnWeightData(50));
        TableColumn column = new TableColumn(exporterPropertiesTable, SWT.NONE, 0);
        column.setText("Name");
        column.setResizable(true);
        
        tableLayout.addColumnData(new ColumnWeightData(50));
        column = new TableColumn(exporterPropertiesTable, SWT.NONE, 1);
        column.setText("Value");
        column.setResizable(true);
        
        tableLayout.layout(exporterPropertiesTable, true);

        Composite propertyActionsComposite = new Composite(propertyOptions, SWT.NONE);
        propertyActionsComposite.setLayout( new GridLayout( 1, true ) );
        propertyActionsComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL,
                true, true ) );
        
        addPropertyButton = new Button(propertyActionsComposite, SWT.PUSH);
        addPropertyButton.setText("Add Property...");
        addPropertyButton.setEnabled(false);
        
        removePropertyButton = new Button(propertyActionsComposite, SWT.PUSH);
        removePropertyButton.setText("Remove Property");
        removePropertyButton.setEnabled(false);
        
        addListeners();
		dialogChanged();
		setControl( sc );
	}
    
    private void addListeners ()
    {
        exporterTable.addSelectionChangedListener(new ISelectionChangedListener () {
           
            public void  selectionChanged (SelectionChangedEvent e) {
               IStructuredSelection selection = (IStructuredSelection) e.getSelection();
               if (!selection.isEmpty())
               {
                  selectedExporter = (ExporterInstance) selection.getFirstElement();
                  
                  exporterProperties.setInput(selectedExporter.getProperties().keySet());
               }
               else {
                  exporterProperties.getTable().clearAll();
                  removePropertyButton.setEnabled(false);
               }
               
               addPropertyButton.setEnabled(!selection.isEmpty());
               exporterProperties.getTable().setEnabled(!selection.isEmpty());
               dialogChanged();
            }
        } );
        
        exporterProperties.addSelectionChangedListener(new ISelectionChangedListener () {
           public void selectionChanged (SelectionChangedEvent e) {
              IStructuredSelection selection = (IStructuredSelection) e.getSelection();
              removePropertyButton.setEnabled(!selection.isEmpty());
           }
        });
        
        addExporterButton.addSelectionListener( new SelectionAdapter () {
           public void widgetSelected(SelectionEvent e)
           {
              ExporterSelectionDialog dialog = new ExporterSelectionDialog(getShell());
              int response = dialog.open();
              if (response == Dialog.OK)
              {
                addExporter((ExporterDefinition) dialog.getFirstResult());
              }
           }
        });
        
        removeExporterButton.addSelectionListener( new SelectionAdapter () {
           public void widgetSelected(SelectionEvent e)
           {
              IStructuredSelection selection = (IStructuredSelection) exporterTable.getSelection();
              if (!selection.isEmpty())
              {
                 ExporterInstance exporter = (ExporterInstance) selection.getFirstElement();
                 attributes.getExporterInstances().remove(exporter);
                 exporterTable.refresh();
                 
                 exporterTable.getTable().deselectAll();
                 dialogChanged();
              }
           }
        });
        
        addPropertyButton.addSelectionListener( new SelectionAdapter () {
           public void widgetSelected(SelectionEvent e)
           {
              ExporterPropertyDialog dialog = new ExporterPropertyDialog(getShell(), selectedExporter);
              int response = dialog.open();
              if (response == Dialog.OK)
              {
                addProperty(dialog.getProperty(), dialog.getPropertyValue());
              }
           }
        });
        
        removePropertyButton.addSelectionListener( new SelectionAdapter () {
           public void widgetSelected(SelectionEvent e)
           {
              IStructuredSelection selection = (IStructuredSelection) exporterProperties.getSelection();
              if (!selection.isEmpty())
              {
                 ExporterProperty property = (ExporterProperty) selection.getFirstElement();
                 selectedExporter.getProperties().remove(property);
                 exporterProperties.refresh();
                 dialogChanged();
              }
           }
        });
    }

	private class ExporterContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[])
			{
				return (Object[]) inputElement;
			}
			
			else if (inputElement instanceof Collection)
			{
				Collection collection = (Collection) inputElement;
				return collection.toArray();
			}
			
			else return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private class ExporterLabelProvider implements ITableLabelProvider {
		Map exp2img = new HashMap(); // not the most optimized but better
										// than having a finalize method.

		public Image getColumnImage(Object element, int columnIndex) {
			ExporterInstance instance = (ExporterInstance) element;
			Image image = (Image) exp2img.get( instance.getDefinition().getId() );
			if ( image == null ) {
				image = instance.getDefinition().getIconDescriptor().createImage();
				exp2img.put( instance.getDefinition().getId(), image );
			}
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			ExporterInstance instance = (ExporterInstance) element;
			return instance.getDefinition().getDescription();
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {

			Iterator iterator = exp2img.values().iterator();
			while ( iterator.hasNext() ) {
				Image img = (Image) iterator.next();
				if ( img != null ) {
					img.dispose();
				}
			}
		}

		public boolean isLabelProperty(Object element, String property) {
			return true;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

	}
	
	private class ExporterPropertiesLabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			ExporterProperty property = (ExporterProperty) element;
			
			if (columnIndex == 0)
			{
				return property.getName();
			}
			else {
				ExporterInstance exporter = selectedExporter;
				if (exporter != null)
				{
					return exporter.getProperty(property);
				}
			}
			
			return null;
		}

		public void addListener(ILabelProviderListener listener) {}
		public void dispose() {}
		public boolean isLabelProperty(Object element, String property) {
			return true;
		}
		public void removeListener(ILabelProviderListener listener) {}
		
	}
    
    private class ExporterPropertiesCellModifier implements ICellModifier
    {
      private TableViewer viewer;
      public ExporterPropertiesCellModifier (TableViewer viewer)
      {
        this.viewer = viewer; 
      }
       
      public boolean canModify(Object element, String property)
      {
         return true;
      }

      public Object getValue(Object element, String property)
      {
         ExporterProperty exporterProperty = (ExporterProperty) element;
         if (property.equals(COLUMN_PROPERTY_NAME))
         {
            return exporterProperty.getName();
         }
         else
         {
            return selectedExporter.getProperty(exporterProperty);
         }
      }

      public void modify(Object element, String property, Object value)
      {
         TableItem item = (TableItem) element;
         ExporterProperty exporterProperty = (ExporterProperty) item.getData();
         
         if (property.equals(COLUMN_PROPERTY_NAME))
         {
            exporterProperty.setName(value.toString());
         }
         else if (property.equals(COLUMN_PROPERTY_VALUE))
         {
            selectedExporter.setProperty(exporterProperty, value.toString());
         }
         
         viewer.refresh(exporterProperty);
         setDirty(true);
         dialogChanged();
      }
       
    }

	private Composite createExpandableComposite(Composite parent, String name,
			boolean expanded) {
		ExpandableComposite composite = new ExpandableComposite( parent,
				SWT.NONE, ExpandableComposite.CLIENT_INDENT
						| ExpandableComposite.TWISTIE );
		composite.setExpanded( expanded );
		composite.setText( name );
		composite.setFont( JFaceResources.getFontRegistry().getBold(
				JFaceResources.DEFAULT_FONT ) );
		composite.setLayout( new GridLayout( 1, false ) );
		composite
				.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
		composite.addExpansionListener( expansionListener );
		ScrolledPageContent scrolledParent = getExpandableCompositeParent( composite );
		if ( scrolledParent != null )
			scrolledParent.adaptChild( composite );

		Composite client = new Composite( composite, SWT.NONE );
		composite.setClient( client );
		client.setLayout( new GridLayout( 1, false ) );
		client.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

		return client;
	}

	private class ExpansionListener extends ExpansionAdapter {

		public void expansionStateChanged(ExpansionEvent e) {
			ScrolledPageContent parent = getExpandableCompositeParent( (ExpandableComposite) e
					.getSource() );
			if ( parent != null )
				parent.reflow( true );

			((ExpandableComposite) e.getSource() ).redraw();
		}
	}

	private ScrolledPageContent getExpandableCompositeParent(
			ExpandableComposite composite) {
		Control parent = composite.getParent();
		while ( !(parent instanceof ScrolledPageContent ) && parent != null )
			parent = parent.getParent();

		if ( parent instanceof ScrolledPageContent )
			return (ScrolledPageContent) parent;
		else
			return null;
	}

	private void dialogChanged() {
		boolean configSelected = true; // TODO: only active if configname in
										// settings
										// ...getConfigurationName().length()==0;

		
		if ( !configSelected ) {
			updateStatus( "Console configuration must be specified" );
			return;
		}

		/*if ( selectedExporters.size() == 0 ) {
			updateStatus( "At least one exporter option must be selected" );
			return;
		}*/
		updateStatus( null );
	}
    
    private void addExporter (ExporterDefinition exporter)
    {
       ExporterInstance instance = attributes.createExporterInstance(exporter);
       attributes.getExporterInstances().add(instance);
       exporterTable.refresh();
       
       setDirty(true);
       dialogChanged();
    }
    
    private void addProperty (ExporterProperty property, String value)
    {
       selectedExporter.setProperty(property, value);
       exporterProperties.refresh();
       
       setDirty(true);
       dialogChanged();
    }

	protected String checkDirectory(IPath path, String name) {
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(
				path );
		if ( res != null ) {
			int resType = res.getType();
			if ( resType == IResource.PROJECT || resType == IResource.FOLDER ) {
				IProject proj = res.getProject();
				if ( !proj.isOpen() ) {
					return "Project for " + name + " is closed";
				}
			}
			else {
				return name + " has to be a folder or project";
			}
		}
		else {
			return name + " does not exist";
		}
		return null;
	}

	protected String checkFile(IPath path, String name) {
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(
				path );
		if ( res != null ) {
			int resType = res.getType();
			if ( resType == IResource.FILE ) {
				return null;
			}
			else {
				return name + " must be a file";
			}
		}
		else {
			return name + " does not exist";
		}
	}

	private void updateStatus(String message) {
		setErrorMessage( message );
		updateLaunchConfigurationDialog();
	}

	private Path pathOrNull(String p) {
		if ( p == null || p.trim().length() == 0 ) {
			return null;
		}
		else {
			return new Path( p );
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// ExporterAttributes tmpAttrs = new ExporterAttributes();
		// tmpAttrs.setEnableAllExporters(true);
		// tmpAttrs.save(configuration);
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			attributes = new LaunchAttributes( configuration );

			enableEJB3annotations.setSelection( attributes.isEJB3Enabled() );
			enableJDK5.setSelection( attributes.isJDK5Enabled() );		

			exporterTable.setInput(attributes.getExporterInstances());
			dialogChanged();

		}
		catch (CoreException ce) {
			HibernateConsolePlugin
					.getDefault()
					.logErrorMessage(
							"Problem when reading hibernate tools launch configuration",
							ce );
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		attributes.setEnableEJB3( enableEJB3annotations.getSelection() );
		attributes.setEnableJDK5( enableJDK5.getSelection() );
		attributes.save( configuration );
	}

	public String getName() {
		return "Exporters";
	}

	public Image getImage() {
		return EclipseImages.getImage( ImageConstants.MINI_HIBERNATE );
	}

}
