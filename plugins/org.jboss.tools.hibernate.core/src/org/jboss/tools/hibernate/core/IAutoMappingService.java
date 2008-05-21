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

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ReversStatistic;



/**
 * @author alex
 *
 * Interface for automated mapping generator
 */
public interface IAutoMappingService {

	public class Settings {
		public boolean canChangeClasses;
		public boolean canChangeTables;
		public boolean canChangeExistingMapping;
		public boolean canUseHeuristicAlgorithms;
	
        public static final Settings DEFAULT_SETTINGS = new Settings();
        static
        {
            DEFAULT_SETTINGS.canChangeClasses = false;
            DEFAULT_SETTINGS.canChangeExistingMapping = false;
            DEFAULT_SETTINGS.canChangeTables = false;
            DEFAULT_SETTINGS.canUseHeuristicAlgorithms = false;
        }
    }
	public void generateMapping(IPersistentClass[] classes, Settings settings) throws CoreException;
	//akuzmin 29.07.2005
	public void generateMapping(IDatabaseTable[] classes, Settings settings,ReversStatistic info) throws CoreException;
	public String getJavaType(IPersistentField pf);
	// added by Nick 11.07.2005
    public static final String REVERSING_REPORT_FILENAME = "ES_reversing_report.htm";
    // by Nick
}
