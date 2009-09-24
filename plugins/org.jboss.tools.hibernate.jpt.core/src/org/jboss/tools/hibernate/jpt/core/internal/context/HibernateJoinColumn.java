/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.hibernate.jpt.core.internal.context;

import org.eclipse.jpt.core.context.JoinColumn;
import org.eclipse.jpt.core.context.PersistentAttribute;

/**
 * @author Dmitry Geraskov
 *
 */
public interface HibernateJoinColumn extends JoinColumn {
	
	String getDBColumnName();

	String getSpecifiedDBColumnName();

	String getDefaultDBColumnName();
	
	//**** referenced column
	
	String getReferencedDBColumnName();

	String getReferencedSpecifiedDBColumnName();

	String getReferencedDefaultDBColumnName();
	
	// ******** required for NamingStrategy
	PersistentAttribute getReferencedPersistentAttribute();

}
