package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.jface.viewers.deferred.SetModel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;

public abstract class RevEngSectionPart extends SectionPart {

	public RevEngSectionPart(Composite parent, IManagedForm form) {
		super(parent, form.getToolkit(), Section.DESCRIPTION|/*ExpandableComposite.TWISTIE|*/ExpandableComposite.EXPANDED|ExpandableComposite.TITLE_BAR);
		
		getSection().addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				getManagedForm().getForm().reflow(true);
			}
		});

		//toolkit.createCompositeSeparator(getSection());
		
		setSectionLayout();
		
		getSection().setText(getSectionTitle());
		getSection().setDescription(getSectionDescription());
		
		getSection().setClient(createClient(form));
	}

	abstract protected String getSectionDescription();

	abstract protected String getSectionTitle();

	protected void setSectionLayout() {
		/*TableWrapData td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;
		getSection().setLayoutData(td);*/
	}

	abstract Control createClient(IManagedForm form);

	final public boolean setFormInput(Object input) {
		if(input instanceof IReverseEngineeringDefinition) {
			return setFormInput((IReverseEngineeringDefinition)input);
		}
		return super.setFormInput( input );
	}
	
	public boolean setFormInput(IReverseEngineeringDefinition reveng) {
		return false;
	}

	protected void adaptRecursively(FormToolkit tk, Composite composite) {
		tk.adapt(composite);
		Control[] children = composite.getChildren();
		for (int i = 0; i < children.length; i++) {
			Control control = children[i];
			if(control instanceof Composite) {
				adaptRecursively(tk, (Composite) control);
			} else {
				tk.adapt(control, true,false);
			}
		}
	}
	
	public void dispose() {
		super.dispose();
	}
}
