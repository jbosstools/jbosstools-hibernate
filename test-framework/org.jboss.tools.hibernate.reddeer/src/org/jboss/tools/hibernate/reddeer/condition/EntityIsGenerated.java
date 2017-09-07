 /*******************************************************************************
  * Copyright (c) 2016 - 2017 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.condition;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.eclipse.jdt.ui.packageview.PackageExplorerPart;

public class EntityIsGenerated extends AbstractWaitCondition{
	
	private String project;
	private PackageExplorerPart pe;
	private String[] entityPath;
	
	public EntityIsGenerated(String project, String... entityPath) {
		this.entityPath = entityPath;
		this.project = project;
		pe = new PackageExplorerPart();
		pe.open();
	}

	@Override
	public boolean test() {
		if(pe.getProject(project).containsResource(entityPath)){
			return true;
		} else {
			pe.getProject(project).refresh();
			return false;
		}
	}

	@Override
	public String description() {
		return "entity "+entityPath[entityPath.length-1]+" is generated";
	}

	@Override
	public String errorMessageUntil() {
		return "entity "+entityPath[entityPath.length-1]+" was not generated";
	}
	
	

}
