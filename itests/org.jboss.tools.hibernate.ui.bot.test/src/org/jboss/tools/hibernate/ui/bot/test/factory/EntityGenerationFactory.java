/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.bot.test.factory;

import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.requirements.db.DatabaseConfiguration;
import org.jboss.tools.hibernate.reddeer.jpt.ui.wizard.GenerateEntitiesWizard;
import org.jboss.tools.hibernate.reddeer.jpt.ui.wizard.GenerateEntitiesWizardPage;

/**
 * Entity Generation help to create common task like like JPA Entity generation 
 * @author jpeterka
 *
 */
public class EntityGenerationFactory {

	/**
	 * Creates JPA entities from database by using Hibernate JPA entity generation facility
	 * @param cfg database configuration
	 * @param prj project
	 * @param pkg package where entities will be located
	 * @param hbVersion hibernate version
	 */
	public static void generateJPAEntities(DatabaseConfiguration cfg, String prj, String pkg, String hbVersion, boolean useConsole) {
		
    	ProjectExplorer pe = new ProjectExplorer();
    	pe.open();
    	pe.selectProjects(prj);
    	
    	GenerateEntitiesWizard w = new GenerateEntitiesWizard();
    	w.open();
    		
    	GenerateEntitiesWizardPage p = new GenerateEntitiesWizardPage();    	
    	p.setUseConsole(useConsole);
    	p.setPackage(pkg);
    	if (!useConsole) {
    		p.setHibernateVersion(hbVersion);
    		p.setDatabaseConnection(cfg.getProfileName());
    	}

    	w.finish();		
	}

}
