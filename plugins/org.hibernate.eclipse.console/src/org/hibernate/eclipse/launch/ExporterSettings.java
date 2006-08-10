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
package org.hibernate.eclipse.launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySheetEntryListener;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.ExtensionManager;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class ExporterSettings extends AbstractLaunchConfigurationTab {
	private Button enableEJB3annotations;

	private Button enableJDK5;

	private List selectedExporters;

	private CheckboxTableViewer exporterTable;

	private Button selectAll;

	private Button deselectAll;

	private PropertySheetPage propertySheet;

	private Button add;

	private Button remove;

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
		selectedExporters = new ArrayList();		

		// ScrolledComposite scrolled = new ScrolledComposite(parent,
		// SWT.V_SCROLL | SWT.H_SCROLL);

		Composite container = new Composite( parent, SWT.NONE );
		GridData controlData = new GridData( GridData.FILL_BOTH );
		container.setLayoutData( controlData );

		GridLayout layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.verticalSpacing = 1;
		container.setLayout( layout );

		createGeneralSettings( container );

		createExporterTable( container );

		createExporterProperties( container );

		dialogChanged();
		setControl( container );
	}

	private void createExporterProperties(Composite parent) {
		Composite exportersComposite = createComposite( parent, "Properties:" );

		exportersComposite.setLayout( new GridLayout( 2, false ) );

		GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
		gd.minimumHeight = 100;
		exportersComposite.setLayoutData( gd );

		Group gr = new Group(exportersComposite, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		
		gr.setLayout( gridLayout );		
		gd = new GridData( SWT.FILL, SWT.FILL, true, true );
		gd.verticalSpan = 2;		
		gr.setLayoutData( gd );

		Control sheet = createPropertySheet( gr );
		gd = new GridData( SWT.FILL, SWT.FILL, true, true );		
		sheet.setLayoutData( gd );
		
		add = new Button( exportersComposite, SWT.PUSH );
		add.setEnabled( false );
		add.setText( "Add..." );
		gd = new GridData( GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING );
		gd.horizontalIndent = 5;
		add.setLayoutData( gd );
		add.addSelectionListener( new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) exporterTable.getSelection();
				ExporterFactory ef = (ExporterFactory) ss.getFirstElement();

				if(ef!=null) {
					AddPropertyDialog dialog = new AddPropertyDialog(getShell(), ef);
					if(dialog.open()==Dialog.OK) {
						ef.setProperty( dialog.getPropertyName(), dialog.getPropertyValue() );
						dialogChanged();	
						refreshPropertySheet();
					}
				}
			}
		
		} );

		remove = new Button( exportersComposite, SWT.PUSH );
		remove.setText( "Remove..." );
		remove.setEnabled( false );
		remove.addSelectionListener( new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				if(currentDescriptor!=null) {
					IStructuredSelection ss = (IStructuredSelection) exporterTable.getSelection();
					ExporterFactory ef = (ExporterFactory) ss.getFirstElement();
					ef.removeProperty( (String) currentDescriptor.getId() );
					dialogChanged();
					refreshPropertySheet();
				}				
			}
		
		} );
		
		gd = new GridData( GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING );
		gd.horizontalIndent = 5;
		remove.setLayoutData( gd );

	}

	public class MyPropertySheetEntry extends PropertySheetEntry {
		
		public IPropertyDescriptor getMyDescriptor() {
			return super.getDescriptor();
		}
		
		protected PropertySheetEntry createChildEntry() {
			return new MyPropertySheetEntry();
		}
	}
	
	// currently selected in the propertysheet
	private IPropertyDescriptor currentDescriptor;
	
	private Control createPropertySheet(Composite exportersComposite) {
		propertySheet = new PropertySheetPage() {
			

			public void handleEntrySelection(ISelection selection) {
				super.handleEntrySelection( selection );
				IStructuredSelection iss = (IStructuredSelection) selection;
				if(iss.isEmpty()) {
					currentDescriptor = null;
				} else {
					MyPropertySheetEntry mse = (MyPropertySheetEntry)iss.getFirstElement();
					currentDescriptor = mse.getMyDescriptor();
				}
			}
		};
		
		propertySheet.createControl( exportersComposite );

		final PropertySheetEntry propertySheetEntry = new MyPropertySheetEntry();

		propertySheetEntry
				.setPropertySourceProvider( new IPropertySourceProvider() {

					public IPropertySource getPropertySource(Object object) {
						if ( object instanceof ExporterFactory ) {
							return new ExporterFactoryPropertySource(
									(ExporterFactory) object ) {
								public void setPropertyValue(Object id, Object value) {
									super.setPropertyValue( id, value );
									dialogChanged();
								}
							};
						}
						else {
							return null;
						}
					}
				} );
		propertySheet.setRootEntry( propertySheetEntry );
		// propertySheetEntry.setValues( new Object[] { this });
		
		exporterTable
				.addSelectionChangedListener( new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection s = (IStructuredSelection) event
						.getSelection();
						if(s.isEmpty()) {
							if(add!=null) add.setEnabled( false );
							if(remove!=null) remove.setEnabled( false );		
					
						} else {
							if(add!=null) add.setEnabled( true );
							if(remove!=null) remove.setEnabled( true );		
					
							ExporterFactory ep = (ExporterFactory) s
							.getFirstElement();
							propertySheetEntry.setValues( new Object[] { ep } );
							// if(ep.isEnabled( configuration ))
						}
					}

				} );

		return propertySheet.getControl();
	}

	private void createExporterTable(Composite parent) {
		Composite exporterOptions = createComposite( parent, "Exporters:" );

		GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
		gd.minimumHeight = 100;
		exporterOptions.setLayoutData( gd );

		exporterOptions.setLayout( new GridLayout( 2, false ) );

		Table table = new Table( exporterOptions, SWT.CHECK | SWT.BORDER
				| SWT.V_SCROLL );
		exporterTable = new CheckboxTableViewer( table );
		exporterTable.setContentProvider( new ExporterContentProvider() );
		exporterTable.setLabelProvider( new ExporterLabelProvider() );
		
		// exporterTable.getControl().setLayoutData(
		// new GridData( SWT.FILL, SWT.FILL, true, true ) );
		exporterTable.setColumnProperties( new String[] { "", "Description" } );
		exporterTable.addCheckStateListener( new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {

				ExporterFactory factory = (ExporterFactory) event.getElement();

				if ( !event.getChecked()
						&& selectedExporters.contains( factory ) ) {
					selectedExporters.remove( factory );
				}
				else if ( event.getChecked()
						&& !selectedExporters.contains( factory ) ) {
					selectedExporters.add( factory );
				}

				dialogChanged();
			}
		} );

		gd = new GridData( SWT.FILL, SWT.FILL, true, true );
		gd.verticalSpan = 2;
		gd.horizontalSpan = 1;
		table.setLayoutData( gd );

		selectAll = new Button( exporterOptions, SWT.PUSH );
		selectAll.setText( "Select All" );
		selectAll.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				exporterTable.setAllChecked( true );
				dialogChanged();
			}
		} );
		gd = new GridData( GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING );
		gd.horizontalIndent = 5;
		selectAll.setLayoutData( gd );

		deselectAll = new Button( exporterOptions, SWT.PUSH );
		deselectAll.setText( "Deselect All" );
		deselectAll.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				exporterTable.setAllChecked( false );
				dialogChanged();
			}
		} );

		gd = new GridData( GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING );
		gd.horizontalIndent = 5;
		deselectAll.setLayoutData( gd );

	}

	private void createGeneralSettings(Composite parent) {

		SelectionListener fieldlistener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected( e );
			}

			public void widgetSelected(SelectionEvent e) {
				dialogChanged();
			}
		};
		Composite generalSettingsComposite = createComposite( parent,
				"General settings:" );
		generalSettingsComposite.setLayoutData( new GridData( SWT.BEGINNING,
				SWT.BEGINNING, false, false ) );

		enableJDK5 = new Button( generalSettingsComposite, SWT.CHECK );
		enableJDK5.setText( "Use Java 5 syntax" );
		enableJDK5.addSelectionListener( fieldlistener );

		enableEJB3annotations = new Button( generalSettingsComposite, SWT.CHECK );
		enableEJB3annotations.setText( "Generate EJB3 annotations" );
		enableEJB3annotations.addSelectionListener( fieldlistener );
	}

	private class ExporterContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return ((List) inputElement ).toArray();
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
			ExporterFactory ef = (ExporterFactory) element;
			ExporterDefinition definition = ef.getExporterDefinition();
			Image image = (Image) exp2img.get( definition.getId() );
			if ( image == null ) {
				image = definition.getIconDescriptor().createImage();
				exp2img.put( definition.getId(), image );
			}
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			ExporterFactory ef = (ExporterFactory) element;
			ExporterDefinition definition = ef.getExporterDefinition();
			return definition.getDescription();
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

	private Composite createComposite(Composite parent, String name) {

		new Label( parent, SWT.NONE ).setText( name );
		Composite client = new Composite( parent, SWT.NONE );
		client.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
		client.setLayout( new GridLayout() );
		// client.setBackground( ColorConstants.cyan );
		return client;
	}

	private void dialogChanged() {
		boolean configSelected = true; // TODO: only active if configname in
		// settings
		// ...getConfigurationName().length()==0;

		if ( !configSelected ) {
			updateStatus( "Console configuration must be specified" );
			return;
		}

		if ( selectedExporters.size() == 0 ) {
			updateStatus( "At least one exporter option must be selected" );
			return;
		}
		updateStatus( null );
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
			ExporterAttributes attributes = new ExporterAttributes(
					configuration );
			selectedExporters.clear();

			enableEJB3annotations.setSelection( attributes.isEJB3Enabled() );
			enableJDK5.setSelection( attributes.isJDK5Enabled() );

			List exporterFactories = attributes.getExporterFactories();
			exporterTable.setInput( exporterFactories );
			for (Iterator iter = exporterFactories.iterator(); iter.hasNext();) {
				ExporterFactory exporterFactory = (ExporterFactory) iter.next();
				if ( exporterFactory.isEnabled() ) {
					exporterTable.setChecked( exporterFactory, true );
					selectedExporters.add( exporterFactory );
				}
				else {
					exporterTable.setChecked( exporterFactory, false );
				}
			}

			refreshPropertySheet();
			
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

	private void refreshPropertySheet() {
		exporterTable.setSelection( exporterTable.getSelection() ); // here to make sure the dependent propertysheet actually will reread what ever the selection is.
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS,
				enableEJB3annotations.getSelection() );
		configuration.setAttribute( HibernateLaunchConstants.ATTR_ENABLE_JDK5,
				enableJDK5.getSelection() );

		List exporterFactories = (List) exporterTable.getInput();
		for (Iterator iter = exporterFactories.iterator(); iter.hasNext();) {
			ExporterFactory ef = (ExporterFactory) iter.next();
			boolean enabled = selectedExporters.contains( ef );
			
			String propertiesId = ef.getId() + ".properties";
			
			ef.setEnabled( configuration, enabled );
			
				
			HashMap map = new HashMap(ef.getProperties());

			if(map.isEmpty()) {				
				configuration.setAttribute( propertiesId, (Map)null );
			} else {
				configuration.setAttribute( propertiesId, map );
			}		
		}
		
	}

	public String getName() {
		return "Exporters";
	}

	public Image getImage() {
		return EclipseImages.getImage( ImageConstants.MINI_HIBERNATE );
	}

}
