/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.core;

import java.io.Serializable;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author alex
 *
 * Base interface for all ORM elements
 */
public interface IOrmElement extends Serializable, IAdaptable {
	public String getName();
	public Object accept(IOrmModelVisitor visitor, Object argument);
	// add tau 06.10.2005
	public String getQualifiedName(TreeItem item);
}
