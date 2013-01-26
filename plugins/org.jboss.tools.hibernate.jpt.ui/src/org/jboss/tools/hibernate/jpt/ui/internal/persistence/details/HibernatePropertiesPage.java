package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details;

import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.ui.internal.persistence.JptUiPersistenceMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.ui.wizard.Messages;

public class HibernatePropertiesPage extends Pane<BasicHibernateProperties> {
	private Composite parent;
	private WidgetFactory widgetFactory;
	public HibernatePropertiesPage(
			PropertyValueModel<BasicHibernateProperties> buildPersistenceUnitModel,
			Composite parent, WidgetFactory widgetFactory) {
		super(buildPersistenceUnitModel,parent,widgetFactory);
		this.parent = parent;
		this.widgetFactory = widgetFactory;
	}

	@Override
	protected void initializeLayout(Composite container) {
		//Composite section = addSection(container, Messages.HibernatePropertiesComposite_basic_properties);
		Section section = this.getWidgetFactory().createSection(container, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText(Messages.HibernatePropertiesComposite_basic_properties);
		Composite sub = addSubPane(section);
		section.setClient(sub);
		Control generalComposite = this.buildHibernatePropertiesComposite(section);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalAlignment = SWT.TOP;
		section.setLayoutData(gridData);
		section.setClient(generalComposite);
	}
	
	protected Control buildHibernatePropertiesComposite(Composite parent) {
		return new HibernatePropertiesComposite(this, parent).getControl();
	}
}
