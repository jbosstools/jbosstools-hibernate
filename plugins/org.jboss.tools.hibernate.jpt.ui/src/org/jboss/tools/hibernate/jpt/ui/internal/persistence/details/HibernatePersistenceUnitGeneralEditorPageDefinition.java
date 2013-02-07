/**
 * 
 */
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jpt.common.ui.WidgetFactory;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.JpaStructureNode;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.ui.editors.JpaEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.jpa.ui.internal.jpa2.persistence.PersistenceUnitEditorPageDefinition;
import org.eclipse.jpt.jpa.ui.internal.persistence.JptUiPersistenceMessages;
import org.eclipse.jpt.jpa.ui.internal.persistence.PersistenceUnitGeneralEditorPage;
import org.eclipse.swt.widgets.Composite;

/**
 * @author eskimo
 *
 */
public class HibernatePersistenceUnitGeneralEditorPageDefinition extends PersistenceUnitEditorPageDefinition
{
	// singleton
	private static final JpaEditorPageDefinition INSTANCE = 
			new HibernatePersistenceUnitGeneralEditorPageDefinition();

	/**
	 * Return the singleton.
	 */
	public static JpaEditorPageDefinition instance() {
		return INSTANCE;
	}


	/**
	 * Ensure single instance.
	 */
	private HibernatePersistenceUnitGeneralEditorPageDefinition() {
		super();
	}

	public String getHelpID() {
		return JpaHelpContextIds.PERSISTENCE_XML_GENERAL;
	}

	@Override
	protected void buildEditorPageContent(Composite parent,
			WidgetFactory widgetFactory, ResourceManager resourceManager,
			PropertyValueModel<PersistenceUnit> persistenceUnitModel) {
		new HibernatePersistenceUnitGeneralEditorPage(persistenceUnitModel, parent, widgetFactory, resourceManager);
	}


	@Override
	public ImageDescriptor getTitleImageDescriptor() {
		return null;
	}


	@Override
	public String getTitleText() {
		return JptUiPersistenceMessages.PersistenceUnitGeneralComposite_general;
	}
}