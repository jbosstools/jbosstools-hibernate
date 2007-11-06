package org.hibernate.eclipse.console;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public abstract class ComboContribution extends ControlContribution {

	protected Combo comboControl;

	protected ComboContribution(String id) {
		super( id );
	}

	String getText() {
		if(comboControl.isDisposed()) {
			return "";
		} else {
			return comboControl.getText();
		}
	}
	
	protected Control createControl(Composite parent) {
		Composite panel = new Composite( parent, SWT.NONE );
		GridLayout gridLayout = new GridLayout(2,false);
		gridLayout.horizontalSpacing = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.marginHeight=0;
		gridLayout.marginWidth=0;
		//gridLayout.
		panel.setLayout( gridLayout );
		
		if(getLabelText()!=null) {
			Label label = new Label(panel, SWT.None);
			label.setText(getLabelText());
			GridData gd = new GridData();
			gd.horizontalSpan = 1;
			gd.verticalAlignment = GridData.BEGINNING;
			gd.horizontalAlignment = GridData.END;
		}
		comboControl = new Combo( panel, SWT.DROP_DOWN | (isReadOnly()?SWT.READ_ONLY:SWT.NONE) );
		populateComboBox();
		comboControl.pack();
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		//gd.verticalIndent = -8;
		gd.widthHint = getComboWidth();
		gd.verticalAlignment = GridData.CENTER;
		gd.horizontalAlignment = GridData.END;
		comboControl.setLayoutData( gd );
		
		

		if(getSelectionAdapter()!=null) {
			comboControl.addSelectionListener( getSelectionAdapter() );
		}

		return panel;
	}

	protected int getComboWidth() {
		return 100;
	}

	protected int computeWidth(Control control) {
		return super.computeWidth(control);
	}
	
	protected boolean isReadOnly() {
		return true;
	}

	String getLabelText() {
		return null;
	}
	
	abstract protected SelectionListener getSelectionAdapter();
	
	abstract void populateComboBox();

	public void dispose() {
		if ( getSelectionAdapter() != null ) {
			if ( !comboControl.isDisposed() ) {
				comboControl.removeSelectionListener( getSelectionAdapter() );
			}
		}
	}
}
