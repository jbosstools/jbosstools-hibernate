package org.hibernate.eclipse.launch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.eclipse.console.model.impl.ExporterProperty;
import org.hibernate.util.StringHelper;

public class AddPropertyDialog extends TitleAreaDialog {

	private final ExporterFactory ef;
	private ComboViewer propertyCombo;
	private Text value;
	private String propertyName;
	private String propertyValue;

	protected AddPropertyDialog(Shell parentShell, ExporterFactory ef) {
		super( parentShell );
		this.ef = ef;
	}

	protected Control createDialogArea(Composite parent) {
		getShell().setText(HibernateConsoleMessages.AddPropertyDialog_add_exporter_property);
		setTitle(HibernateConsoleMessages.AddPropertyDialog_add_property_to + ef.getExporterDefinition().getDescription());
		Composite control = (Composite) super.createDialogArea( parent );

		Composite composite = new Composite(control,SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2,false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);

        ModifyListener modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateStatus();
			}

		};

		Label label = new Label(composite, SWT.NONE);
		label.setText( HibernateConsoleMessages.AddPropertyDialog_name );
		Combo combo = new Combo(composite, SWT.BORDER | SWT.LEAD | SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		combo.setFocus();
		combo.addModifyListener( modifyListener );
		propertyCombo = new ComboViewer(combo);

		label = new Label(composite, SWT.NONE);
		label.setText( HibernateConsoleMessages.AddPropertyDialog_value );

		value = new Text(composite, SWT.BORDER | SWT.LEAD );
		value.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		value.addModifyListener( modifyListener );

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
				Iterator set = localEf.getDefaultExporterProperties().entrySet().iterator();
				List values = new ArrayList(4);
				while ( set.hasNext() ) {
					Map.Entry element = (Map.Entry) set.next();
					//if(!localEf.hasLocalValueFor((String) element.getKey())) {
						ExporterProperty exporterProperty = localEf.getExporterProperty( (String) element.getKey() );
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

			public void selectionChanged(SelectionChangedEvent event) {
				if(value==null) return;
				IStructuredSelection iss = (IStructuredSelection) event.getSelection();
				if(!iss.isEmpty()) {
					value.setText( ((ExporterProperty)iss.getFirstElement()).getDefaultValue() );
				}
			}

		} );
		viewer.setInput( ef );
		if(viewer.getCombo().getItemCount()>0) {
			viewer.setSelection( new StructuredSelection(viewer.getElementAt( 0 )));
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
			if (ef.hasLocalValueFor( getPropertyName() )) {
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
			propertyValue = value.getText();
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
