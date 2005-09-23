package org.hibernate.eclipse.mapper.editors.reveng;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;

public class CounterFormPart extends RevEngSectionPart {

	private Text text;
	private IReverseEngineeringDefinition def;
	private PropertyChangeListener listener;

	public CounterFormPart(Composite parent, IManagedForm form) {
		super(parent,form);
	}

	public boolean setFormInput(IReverseEngineeringDefinition def) {
		this.def = def;
		listener = new PropertyChangeListener() {
			int cnt = 0;
			public void propertyChange(PropertyChangeEvent evt) {
				text.setText("" + cnt++);		
			}
		
		};
		def.addPropertyChangeListener(listener);
		return true;
	}
	
	public void dispose() {
		def.removePropertyChangeListener(listener);
	}
	
	Control createClient(IManagedForm form) {
		FormToolkit toolkit = form.getToolkit();
		Composite composite = toolkit.createComposite(getSection());
		composite.setLayout(new FillLayout());
		text = toolkit.createText(composite, "Zero");
		return composite;
	}
	
	protected String getSectionDescription() {
		return "debug counter for property changes";
	}
	
	protected String getSectionTitle() {
		return "Debug counter";
	}
}
