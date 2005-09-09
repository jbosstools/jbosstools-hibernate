package org.hibernate.eclipse.graph;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;

public class RelationalConfigurationEditPart extends ConfigurationEditPart
		implements EditPart {

	public RelationalConfigurationEditPart(ConfigurationViewAdapter configuration) {
		super( configuration );
	}
	
	protected List getModelChildren() {
		if ( getConfigurationViewAdapter() == null )
			return super.getModelChildren();
		return getConfigurationViewAdapter().getSelectedTables();
	}

}
