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
package org.jboss.tools.hibernate.internal.core.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.jboss.tools.hibernate.core.IOrmProject;

/**
 * Create ISchedulingRules
 * @author tau
 * 16.03.2006
 */
public class RuleUtils {
	
    public static final int CREATE_RULE = 0;
    public static final int DELETE_RULE = 1;
    public static final int MARKER_RULE = 2;
    public static final int MODIFY_RULE = 3;
    public static final int REFRESH_RULE = 4;
	
	public static ISchedulingRule modifyRule(IResource[] resource, int kindRule ) {
		ISchedulingRule combinedRule = null;
		IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
		for (int i = 0; i < resource.length; i++){
			ISchedulingRule rule = null;
			
			switch (kindRule) {
			case CREATE_RULE:
				rule = ruleFactory.createRule(resource[i]);				
				break;
			case DELETE_RULE:
				rule = ruleFactory.deleteRule(resource[i]);				
				break;
			case MARKER_RULE:
				rule = ruleFactory.markerRule(resource[i]);				
				break;
			case MODIFY_RULE:
				rule = ruleFactory.modifyRule(resource[i]);				
				break;
			case REFRESH_RULE:
				rule = ruleFactory.refreshRule(resource[i]);				
				break;
			}

			if (rule != null){
				combinedRule = MultiRule.combine(rule, combinedRule);				
			}
		}
		return combinedRule;
	}

	public static ISchedulingRule getOrmProjectRule(IOrmProject ormProject ) {
		ISchedulingRule combinedRule = null;
		combinedRule = MultiRule.combine(ormProject.getProject(), combinedRule);
		
		IProject[] referencedProjects = ormProject.getReferencedProjects();
		for (int i = 0; i < referencedProjects.length; i++){
			combinedRule = MultiRule.combine(referencedProjects[i], combinedRule);
		}
		return combinedRule;
	}

}
