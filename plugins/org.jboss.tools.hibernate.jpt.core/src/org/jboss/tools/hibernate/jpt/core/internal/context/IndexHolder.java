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

import org.eclipse.jpt.jpa.core.context.JpaContextNode;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaIndex;

/**
 * @author Dmitry Geraskov
 *
 */
public interface IndexHolder extends JpaContextNode {
	
	//******************** index *****************

	String INDEX_PROPERTY = "index"; //$NON-NLS-1$
	
	JavaIndex getIndex();
	
	JavaIndex addIndex();
	
	void removeIndex();

}
