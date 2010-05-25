/*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.core.context.java.JavaJoinTableEnabledRelationshipReference;
import org.eclipse.jpt.core.internal.context.java.GenericJavaJoinTableJoiningStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.context.NamingStrategyMappingTools;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaJoinTableJoiningStrategy extends
		GenericJavaJoinTableJoiningStrategy {

	/**
	 * @param parent
	 */
	public HibernateJavaJoinTableJoiningStrategy(
			JavaJoinTableEnabledRelationshipReference parent) {
		super(parent);
	}
	
	public String getJoinTableDefaultName() {
		return NamingStrategyMappingTools.buildJoinTableDefaultName(getRelationshipReference());
	}

}
