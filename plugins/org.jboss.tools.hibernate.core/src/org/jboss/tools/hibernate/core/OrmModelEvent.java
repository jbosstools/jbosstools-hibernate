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
 * @author Tau
 *
 */
public class OrmModelEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private IOrmModel model;
	private Object project;
	private int type;
	public static final int AddProject = 1;
	public static final int ChangeProject = 2;
	public static final int RemoveProject = 3;
	public static final int RemoveALLProjects = 4;
	public static final int ChangeModel = 5;	

	/**
	 * @param source
	 */
	public OrmModelEvent(IOrmModel model, Object source, Object project, int type) {
		super(source);
		this.model = model;
		this.project = project;		
		this.type = type;

	}
	
	public IOrmModel getOrmModel(){ return model; }
	
	public Object getSource(){ return source; }	
	
	public Object getProject(){ return project; }	
	
	public int getType(){ return type; }	

}

