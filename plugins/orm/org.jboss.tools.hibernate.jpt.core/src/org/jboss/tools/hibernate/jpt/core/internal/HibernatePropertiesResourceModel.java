/*******************************************************************************
  * Copyright (c) 2012 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.jpt.common.core.JptResourceModel;
import org.eclipse.jpt.common.core.JptResourceModelListener;
import org.eclipse.jpt.common.core.JptResourceType;
import org.eclipse.jpt.common.core.internal.utility.ContentTypeTools;
import org.eclipse.jpt.jpa.core.JpaProject;

/**
 * 
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class HibernatePropertiesResourceModel implements JptResourceModel {
	
	protected final JpaProject project;
	
	protected final IFile file;
		
	public HibernatePropertiesResourceModel(JpaProject project, IFile file){
		this.project = project;
		this.file = file;
	}

	public JptResourceType getResourceType() {
		return ContentTypeTools.getResourceType(HibernateJptPlugin.JAVA_PROPERTIES_CONTENT_TYPE);
	}

	public IFile getFile() {
		return file;
	}

	// ********** JptResourceModel implementation **********
	
	public void addResourceModelListener(JptResourceModelListener listener) {
		//do nothing for now
	}
	
	public void removeResourceModelListener(JptResourceModelListener listener) {
		//do nothing for now
	}
	


}
