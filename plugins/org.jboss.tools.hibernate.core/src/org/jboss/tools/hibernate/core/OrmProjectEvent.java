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

import java.util.EventObject;

/**
 * @author alex
 *
 * An event describes a change to the structure or contents of ORM model. 
 */
public class OrmProjectEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private IOrmProject project;
	public OrmProjectEvent(IOrmProject project, Object source){
		super(source);
		this.project=project;
	}
	public IOrmProject getProject(){ return project; }
}
