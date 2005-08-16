package org.hibernate.eclipse.graph;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.hibernate.eclipse.graph.model.AssociationViewAdapter;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;
import org.hibernate.eclipse.graph.model.PersistentClassViewAdapter;
import org.hibernate.eclipse.graph.model.PropertyViewAdapter;
import org.hibernate.eclipse.graph.parts.AssociationEditPart;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;
import org.hibernate.eclipse.graph.parts.PersistentClassEditPart;
import org.hibernate.eclipse.graph.parts.PropertyEditPart;

public class HibernateConfigurationPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		if ( model instanceof ConfigurationViewAdapter ) {
			return new ConfigurationEditPart( (ConfigurationViewAdapter) model );
		} else if ( model instanceof PersistentClassViewAdapter ) {
			return new PersistentClassEditPart( (PersistentClassViewAdapter)model );
		} else if ( model instanceof PropertyViewAdapter ) {
			return new PropertyEditPart( (PropertyViewAdapter)model );
		} else if ( model instanceof AssociationViewAdapter) {
			return new AssociationEditPart( (AssociationViewAdapter) model);
		}
			throw new IllegalArgumentException(model.getClass() + " not known by factory");
	}

}
