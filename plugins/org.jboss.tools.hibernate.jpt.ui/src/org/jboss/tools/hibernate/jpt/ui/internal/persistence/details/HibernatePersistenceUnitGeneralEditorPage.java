/**
 * 
 */
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.ui.internal.persistence.JptUiPersistenceMessages;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitGeneralEditorPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author eskimo
 *
 */
public class HibernatePersistenceUnitGeneralEditorPage extends
	PersistenceUnitGeneralEditorPage {


	public HibernatePersistenceUnitGeneralEditorPage(
			PropertyValueModel<PersistenceUnit> subjectHolder,
			Composite parent, WidgetFactory widgetFactory) {
		super(subjectHolder, parent, widgetFactory);
	}
	
	protected Control buildClassesComposite(Composite parent) {
		return new HibernatePersistenceUnitClassesComposite(this, parent).getControl();
	}	

}
