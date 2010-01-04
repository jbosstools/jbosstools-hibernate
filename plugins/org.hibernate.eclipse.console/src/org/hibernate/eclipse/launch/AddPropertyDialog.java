/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.launch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.eclipse.console.model.impl.ExporterProperty;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.hibernate.util.StringHelper;

/**
 * Add/edit property dialog to configure Hibernate Exporters.
 */
public class AddPropertyDialog extends TitleAreaDialog {

	private final ExporterFactory ef;
	private final String selectedPropertyId;
	private final boolean flagEdit;
	private ComboViewer propertyCombo;
	private Control value;
	private Button addPathButton;
	private String propertyName;
	private String propertyValue;
	private ModifyListener modifyListener = new ModifyListener() {
				
			public void modifyText(ModifyEvent e) {
				updateStatus();
			}
		
		};

	protected AddPropertyDialog(Shell parentShell, ExporterFactory ef, String selectedPropertyId, boolean flagEdit) {
		super( parentShell );
		this.ef = ef;
		this.selectedPropertyId = selectedPropertyId;
		this.flagEdit = flagEdit;
	}

	protected Control createDialogArea(Composite parent) {
		
		String dialogTitle = HibernateConsoleMessages.AddPropertyDialog_add_exporter_property;
		String editTitle = HibernateConsoleMessages.AddPropertyDialog_add_property_to;
		if (flagEdit) {
			dialogTitle = HibernateConsoleMessages.AddPropertyDialog_edit_exporter_property;
			editTitle = HibernateConsoleMessages.AddPropertyDialog_edit_property_to;
		}
		getShell().setText(dialogTitle);
		setTitle(editTitle + ef.getExporterDefinition().getDescription());
		Composite control = (Composite) super.createDialogArea( parent );

		Composite composite = new Composite(control,SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(3,false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

		Label label = new Label(composite, SWT.NONE);
		label.setText( HibernateConsoleMessages.AddPropertyDialog_name );
		final Combo combo = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN);
		GridData pgd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		pgd.horizontalSpan = 2;
		combo.setLayoutData(pgd);
		combo.setFocus();
		combo.addModifyListener( modifyListener );
		propertyCombo = new ComboViewer(combo);
		
		combo.addKeyListener(new KeyListener(){
		
				public void keyPressed(KeyEvent e) {}
			
				public void keyReleased(KeyEvent e) {
					if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN) {
						//linux doesn't call selectionChanged event on this events
						propertyCombo.setSelection(propertyCombo.getSelection(), false);
						return;
					}

				    for (int i = 0; i < combo.getItemCount(); i++) {
						if (combo.getText().equals(combo.getItem(i))){
							if (combo.getSelectionIndex() != i){
								combo.select(i);
								propertyCombo.setSelection(propertyCombo.getSelection(), false);
							}								
							return;
						}
				    }
				    disposeBrowseButton();
				    createTextValueComposite(2);
				}
						
			});
					
		if (flagEdit) {
			propertyCombo.getControl().setEnabled(false);
		}

		label = new Label(composite, SWT.NONE);
		label.setText( HibernateConsoleMessages.AddPropertyDialog_value );

		value = new Text(composite, SWT.BORDER);
		value.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		((Text)value).addModifyListener( modifyListener );

		initDefaultNames(ef, propertyCombo);

		return control;
	}

	private void initDefaultNames(ExporterFactory ef2, ComboViewer viewer) {
		viewer.setContentProvider( new IStructuredContentProvider() {

			ExporterFactory localEf;

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				localEf = (ExporterFactory) newInput;
			}

			public void dispose() {
				localEf = null;
			}

			public Object[] getElements(Object inputElement) {
				Iterator<Map.Entry<String, ExporterProperty>> set = localEf.getDefaultExporterProperties().entrySet().iterator();
				List<ExporterProperty> values = new ArrayList<ExporterProperty>(4);
				while ( set.hasNext() ) {
					Map.Entry<String, ExporterProperty> element = set.next();
					//if(!localEf.hasLocalValueFor((String) element.getKey())) {
						ExporterProperty exporterProperty = localEf.getExporterProperty( element.getKey() );
						if(exporterProperty!=null) {
							values.add(exporterProperty);
						}
					//}
				}
				return values.toArray( new ExporterProperty[values.size()] );
			}
		});

		viewer.setLabelProvider( new ILabelProvider() {

			public void removeListener(ILabelProviderListener listener) {

			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void dispose() {

			}

			public void addListener(ILabelProviderListener listener) {

			}

			public String getText(Object element) {
				ExporterProperty exporterProperty = ((ExporterProperty)element);
				return exporterProperty.getDescriptionForLabel();
			}

			public Image getImage(Object element) {
				return null;
			}

		} );

		viewer.addSelectionChangedListener( new ISelectionChangedListener() {

			private SelectionListener getSelectionListener(ExporterProperty prop){
				if (!("path".equals(prop.getType()) || "directory".equals(prop.getType()))) return null; //$NON-NLS-1$//$NON-NLS-2$				
				final boolean isPath = "path".equals(prop.getType()); //$NON-NLS-1$
				return new SelectionListener(){
						public void widgetDefaultSelected(SelectionEvent e) {
							widgetSelected(e);
						}
			
						public void widgetSelected(SelectionEvent e) {
							String title = isPath ? HibernateConsoleMessages.ExporterSettingsTab_select_path: HibernateConsoleMessages.ExporterSettingsTab_select_dir;
							String description = isPath ? HibernateConsoleMessages.ExporterSettingsTab_select_path2 : HibernateConsoleMessages.ExporterSettingsTab_select_dir2;
							
							MessageDialog dialog = new MessageDialog(getShell(),
									title,
									null,
									description,	
									MessageDialog.QUESTION,
									new String[] { HibernateConsoleMessages.CodeGenerationSettingsTab_filesystem, HibernateConsoleMessages.CodeGenerationSettingsTab_workspace, IDialogConstants.CANCEL_LABEL},
									1);
							int answer = dialog.open();
							String strPath = null;
							if (answer == 0) { // filesystem
								DirectoryDialog dialog2 = new DirectoryDialog(getShell());
								dialog2.setText(title);
								dialog2.setMessage(description);
								
								String dir = dialog2.open();
								if (dir != null)
								{
									strPath = dir;
								}
							} else if (answer == 1){ // workspace								
								IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(), (IPath)null, new Path[0], 
										title, description,
										new String[0], isPath, true, false);
								if (paths != null && paths.length > 0){
									strPath = paths[0].toOSString();
									if (isPath){
										for (int i = 1; i < paths.length; i++) {
											strPath += ';' + paths[i].toOSString();
										}
									}
								}
							} else return;
							String oldPath = ((Text)value).getText();
							if (isPath && oldPath.trim().length() > 0 && strPath != null)
								((Text)value).setText( oldPath + ';' + strPath );
							else {
								if (strPath != null)
									((Text)value).setText( strPath );	
							}
						}
					};
			}

			public void selectionChanged(SelectionChangedEvent event) {
				if(value==null) return;
				IStructuredSelection iss = (IStructuredSelection) event.getSelection();
				if(!iss.isEmpty()) {
				ExporterProperty prop = (ExporterProperty)iss.getFirstElement();
					if ("boolean".equalsIgnoreCase(prop.getType())) {	//$NON-NLS-1$
						disposeBrowseButton();
						createComboValueComposite(new String[]{String.valueOf(true), String.valueOf(false)});
						((Combo)value).select(Boolean.valueOf(ef.getPropertyValue(prop.getName())).booleanValue() ? 0 : 1);
					} else if ("directory".equalsIgnoreCase(prop.getType())//$NON-NLS-1$
							|| "path".equalsIgnoreCase(prop.getType())) {	//$NON-NLS-1$
						disposeBrowseButton();
						createTextValueComposite(1);
						((Text) value).setText(ef.getPropertyValue(prop.getName()));
						createBrowseButton(getSelectionListener(prop), prop);
					} else {
						disposeBrowseButton();
						createTextValueComposite(2);
						((Text) value).setText(ef.getPropertyValue(prop.getName()));
					}
				} else {
					createTextValueComposite(2);
				}
			}
		} );
		viewer.setInput( ef );
		if(viewer.getCombo().getItemCount()>0) {
			Object selected = null;
			if (selectedPropertyId != null) {
				selected = ef.getExporterProperty(selectedPropertyId);
			}
			else {
				selected = viewer.getElementAt( 0 );
			}
			viewer.setSelection(new StructuredSelection(selected));
			viewer.getCombo().select(viewer.getCombo().getSelectionIndex());
		}
	}

	private void disposeBrowseButton(){
		if (addPathButton != null){
			Composite parent = addPathButton.getParent();
			addPathButton.dispose();
			addPathButton = null;
			parent.layout();
		}
	}
 	
	private void createBrowseButton(SelectionListener listener, ExporterProperty prop){
		disposeBrowseButton();
		addPathButton = new Button(value.getParent(), SWT.PUSH);
		if ("path".equals(prop.getType())){ //$NON-NLS-1$
			addPathButton.setText(HibernateConsoleMessages.AddPropertyDialog_add_path);
		} else {
			addPathButton.setText(HibernateConsoleMessages.AddPropertyDialog_browse);
		}		
		addPathButton.setLayoutData(new GridData(GridData.END));
		addPathButton.addSelectionListener(listener);		
		value.getParent().layout();
	}
	
	private void createTextValueComposite(int span){
		if (! (value instanceof Text)) {
			Composite parent = value.getParent();
			if (value != null){
				value.dispose();
			}
			value = new Text(parent, SWT.BORDER | SWT.LEAD | SWT.DROP_DOWN);
			((Text)value).addModifyListener( modifyListener );
			GridData vgd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
			vgd.horizontalSpan = span;
			value.setLayoutData(vgd);			
		} else {
			((GridData)value.getLayoutData()).horizontalSpan = span;
			((Text)value).setText(""); //$NON-NLS-1$
		}
		value.getParent().layout();
	}
	
	private void createComboValueComposite(String[] items){
		if (!(value instanceof Combo)) {
			Composite parent = value.getParent();
			if (value != null){
				value.dispose();
			}
			value = new Combo(parent, SWT.BORDER | SWT.LEAD | SWT.DROP_DOWN | SWT.READ_ONLY);
			GridData bgd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
			bgd.horizontalSpan = 2;
			value.setLayoutData(bgd);
			((Combo)value).setItems(items);
			((Combo)value).addModifyListener( modifyListener );
			if (items != null && items.length > 0){
				((Combo)value).select(0);
			}
			parent.layout();
		}
	}
	
	
	void updateStatus() {
		getEnteredValues();

		boolean ok = false;
		if(StringHelper.isEmpty( getPropertyName() )) {
			setMessage( HibernateConsoleMessages.AddPropertyDialog_the_property_name_must_be_chosen_or_entered, IMessageProvider.ERROR);
		} else if (getPropertyName().indexOf( ' ' )>=0 || getPropertyName().indexOf( '\t' )>=0) {
			setMessage( HibernateConsoleMessages.AddPropertyDialog_the_property_name_may_not_contain_whitespaces, IMessageProvider.ERROR);
		} else if(StringHelper.isEmpty( getPropertyValue() )) {
			setMessage( HibernateConsoleMessages.AddPropertyDialog_the_property_value_must_be_non_empty, IMessageProvider.ERROR);
		} else {
			if (!flagEdit && ef.hasLocalValueFor( getPropertyName() )) {
				String out = NLS.bind(HibernateConsoleMessages.AddPropertyDialog_the_property_is_already_set, getPropertyName());
				setMessage(out, IMessageProvider.WARNING);
			} else {
				setMessage(null, IMessageProvider.ERROR);
			}
			ok = true;
		}

		Button button = getButton(IDialogConstants.OK_ID);
		if(button!=null) {
			button.setEnabled( ok );
		}
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public String getPropertyName() {
		return propertyName;
	}

	void getEnteredValues() {
		if(propertyCombo==null) {
			propertyName = null;
		} else {
			IStructuredSelection selection = (IStructuredSelection) propertyCombo.getSelection();
			if(selection.isEmpty()) {
				propertyName = propertyCombo.getCombo().getText();
			} else {
				ExporterProperty p = (ExporterProperty) selection.getFirstElement();
				propertyName = p.getName();
			}
		}

		if(value!=null) {
			if (value instanceof Text) {
				propertyValue = ((Text) value).getText();
			} else if (value instanceof Combo) {
				propertyValue = ((Combo) value).getText();
			}
		} else {
			propertyValue = null;
		}
	}

	protected void okPressed() {
		getEnteredValues();
		super.okPressed();
	}

	public void create() {
		super.create();
		updateStatus();
	}
}
