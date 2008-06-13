/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.context.persistence.Property;
import org.eclipse.jpt.utility.internal.model.AbstractModel;
import org.eclipse.jpt.utility.model.event.PropertyChangeEvent;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaProperties extends AbstractModel implements
		HibernateProperties {
	
	private PersistenceUnit persistenceUnit;
	
	public HibernateJpaProperties(PersistenceUnit parent) {
		super();
		this.initialize(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.eclipselink.core.internal.context.PersistenceUnitProperties#getJpaProject()
	 */
	public JpaProject getJpaProject() {
		return this.persistenceUnit.getJpaProject();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.eclipselink.core.internal.context.PersistenceUnitProperties#itemIsProperty(org.eclipse.jpt.core.context.persistence.Property)
	 */
	public boolean itemIsProperty(Property item) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.eclipselink.core.internal.context.PersistenceUnitProperties#persistenceUnit()
	 */
	public PersistenceUnit persistenceUnit() {
		return this.persistenceUnit;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.eclipselink.core.internal.context.PersistenceUnitProperties#propertyIdFor(org.eclipse.jpt.core.context.persistence.Property)
	 */
	public String propertyIdFor(Property property) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jpt.utility.model.listener.PropertyChangeListener#propertyChanged(org.eclipse.jpt.utility.model.event.PropertyChangeEvent)
	 */
	public void propertyChanged(PropertyChangeEvent event) {
		throw new UnsupportedOperationException();
	}
	
	protected void initialize(PersistenceUnit parent) {
		this.persistenceUnit = parent;
	}

}
