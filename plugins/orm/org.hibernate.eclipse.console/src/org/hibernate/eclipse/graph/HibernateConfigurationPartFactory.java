/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.graph;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.graph.model.AssociationViewAdapter;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;
import org.hibernate.eclipse.graph.model.PersistentClassViewAdapter;
import org.hibernate.eclipse.graph.model.PropertyViewAdapter;
import org.hibernate.eclipse.graph.model.TableViewAdapter;
import org.hibernate.eclipse.graph.parts.AssociationEditPart;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;
import org.hibernate.eclipse.graph.parts.PersistentClassEditPart;
import org.hibernate.eclipse.graph.parts.PropertyEditPart;
import org.hibernate.eclipse.graph.parts.TableEditPart;

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
		} else if ( model instanceof TableViewAdapter ) {
			return new TableEditPart( (TableViewAdapter) model);
		}

		throw new IllegalArgumentException(NLS.bind(
				HibernateConsoleMessages.HibernateConfigurationPartFactory_not_known_by_factory, 
				model.getClass()));
	}

}
