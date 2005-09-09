package org.hibernate.eclipse.graph;

import org.eclipse.gef.EditPart;
import org.hibernate.eclipse.graph.model.ColumnViewAdapter;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;

public class RelationalConfigurationEditPartFactory extends
		HibernateConfigurationPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		if(model instanceof ConfigurationViewAdapter) {
			return new RelationalConfigurationEditPart((ConfigurationViewAdapter)model);
		} else if(model instanceof ColumnViewAdapter) {
			return new ColumnEditPart((ColumnViewAdapter)model);
		}
		
		return super.createEditPart( context, model );
	}
}
