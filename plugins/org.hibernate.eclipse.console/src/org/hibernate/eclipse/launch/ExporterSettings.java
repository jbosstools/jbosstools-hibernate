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
import org.eclipse.jdt.internal.ui.preferences.ScrolledPageContent;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
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
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.ExtensionManager;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class ExporterSettings extends AbstractLaunchConfigurationTab {
	private Button enableEJB3annotations;

	private Button enableJDK5;

	
	private ExpansionListener expansionListener = new ExpansionListener();

	private List selectedExporters;

	private CheckboxTableViewer exporterTable;

	private ExporterDefinition[] exporters;

	private Button selectAll;

	private Button deselectAll;

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
		exporters = ExtensionManager.findExporterDefinitions();

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

		enableJDK5 = new Button( generalSettingsComposite, SWT.CHECK );
		enableJDK5.setText( "Use Java 5 syntax" );
		enableJDK5.addSelectionListener( fieldlistener );

		enableEJB3annotations = new Button( generalSettingsComposite, SWT.CHECK );
		enableEJB3annotations.setText( "Generate EJB3 annotations" );
		enableEJB3annotations.addSelectionListener( fieldlistener );

		Composite exporterOptions = new Composite( exportersComposite, SWT.NONE );
		exporterOptions.setLayout( new GridLayout( 2, false ) );
		
		Table table = new Table( exporterOptions, SWT.CHECK | SWT.BORDER
				| SWT.V_SCROLL );
		exporterTable = new CheckboxTableViewer( table );
		exporterTable.setContentProvider( new ExporterContentProvider() );
		exporterTable.setLabelProvider( new ExporterLabelProvider() );
		exporterTable.setInput( exporters );
		exporterTable.getControl().setLayoutData(
				new GridData( SWT.FILL, SWT.FILL, true, true ) );
		exporterTable.setColumnProperties( new String[] { "", "Description" } );
		exporterTable.addCheckStateListener( new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				ExporterDefinition definition = (ExporterDefinition) event
						.getElement();

				if ( !event.getChecked() && selectedExporters.contains( definition ) ) {
					selectedExporters.remove( definition );
				} else if ( event.getChecked() && !selectedExporters.contains( definition ) ) {
					selectedExporters.add( definition );
				}

				dialogChanged();
			}
		} );

		Composite listActionsComposite = new Composite( exporterOptions,
				SWT.NONE );
		listActionsComposite.setLayout( new GridLayout( 1, true ) );
		listActionsComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL,
				true, true ) );

		selectAll = new Button( listActionsComposite, SWT.PUSH );
		selectAll.setText( "Select All" );
		selectAll.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				exporterTable.setAllChecked( true );
				dialogChanged();
			}
		} );

		deselectAll = new Button( listActionsComposite, SWT.PUSH );
		deselectAll.setText( "Deselect All" );
		deselectAll.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				exporterTable.setAllChecked( false );
				dialogChanged();
			}
		} );

		dialogChanged();
		setControl( sc );
	}

	private class ExporterContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
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
			ExporterDefinition definition = (ExporterDefinition) element;
			Image image = (Image) exp2img.get( definition.getId() );
			if ( image == null ) {
				image = definition.getIconDescriptor().createImage();
				exp2img.put( definition.getId(), image );
			}
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			ExporterDefinition definition = (ExporterDefinition) element;
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
			ExporterAttributes attributes = new ExporterAttributes( configuration );
			selectedExporters.clear();

			enableEJB3annotations.setSelection( attributes.isEJB3Enabled() );
			enableJDK5.setSelection( attributes.isJDK5Enabled() );

			for (int i = 0; i < exporters.length; i++) {
				if ( exporters[i].isEnabled( configuration ) ) {
					exporterTable.setChecked( exporters[i], true );
					selectedExporters.add( exporters[i] );
				}
				else {
					exporterTable.setChecked( exporters[i], false );
				}
			}			

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
		configuration.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, enableEJB3annotations.getSelection());
		configuration.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_JDK5, enableJDK5.getSelection() );

		for (int i = 0; i < exporters.length; i++) {
			ExporterDefinition exporterDefinition = exporters[i];
			boolean enabled = selectedExporters.contains( exporterDefinition );
			exporterDefinition.setEnabled( configuration, enabled );
		}
	}

	public String getName() {
		return "Exporters";
	}

	public Image getImage() {
		return EclipseImages.getImage( ImageConstants.MINI_HIBERNATE );
	}

}
