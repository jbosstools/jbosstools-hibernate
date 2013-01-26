package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.JpaStructureNode;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.ui.editors.JpaEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.jpa2.persistence.PersistenceUnitEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitConnectionEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitPropertiesEditorPage;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;


public class HibernatePropertiesPageDefinition extends PersistenceUnitEditorPageDefinition {

	// singleton
	private static final JpaEditorPageDefinition INSTANCE = 
			new HibernatePropertiesPageDefinition();

	/**
	 * Return the singleton.
	 */
	public static JpaEditorPageDefinition instance() {
		return INSTANCE;
	}
	
	@Override
	public String getHelpID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getPageImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPageText() {
		// TODO Auto-generated method stub
		return "Hibernate";
	}

	@Override
	protected void buildEditorPageContent(Composite parent,
			WidgetFactory widgetFactory,
			PropertyValueModel<JpaStructureNode> jpaRootStructureNodeModel) {
		new HibernatePropertiesPage(this.buildBasicHibernatePropertiesHolder(buildPersistenceUnitModel(jpaRootStructureNodeModel)), parent, widgetFactory);		
	}
	
	protected PropertyValueModel<BasicHibernateProperties> buildBasicHibernatePropertiesHolder(
			PropertyValueModel<PersistenceUnit> subjectHolder) {
		return new TransformationPropertyValueModel<PersistenceUnit, BasicHibernateProperties>(subjectHolder) {
			@Override
			protected BasicHibernateProperties transform_(PersistenceUnit value) {
				return ((HibernatePersistenceUnit)value).getHibernatePersistenceUnitProperties();
			}
		};
	}

}
