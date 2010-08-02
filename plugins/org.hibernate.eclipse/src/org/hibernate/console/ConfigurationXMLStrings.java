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
package org.hibernate.console;

/**
 * XML Ant code generation strings for definition of configuration part
 *  
 * @author vitali
 */
public interface ConfigurationXMLStrings {
	//
	public static final String JDBCCONFIGURATION = "jdbcconfiguration"; //$NON-NLS-1$
	public static final String ANNOTATIONCONFIGURATION = "annotationconfiguration"; //$NON-NLS-1$
	public static final String JPACONFIGURATION = "jpaconfiguration"; //$NON-NLS-1$
	public static final String CONFIGURATION = "configuration"; //$NON-NLS-1$
	//
	public static final String CONFIGURATIONFILE = "configurationFile"; //$NON-NLS-1$
	public static final String PROPERTYFILE = "propertyFile"; //$NON-NLS-1$
	public static final String ENTITYRESOLVER = "entityResolver"; //$NON-NLS-1$
	public static final String NAMINGSTRATEGY = "namingStrategy"; //$NON-NLS-1$
	public static final String PERSISTENCEUNIT = "persistenceUnit"; //$NON-NLS-1$
	public static final String PREFERBASICCOMPOSITEIDS = "preferBasicCompositeIds"; //$NON-NLS-1$
	public static final String DETECTMANYTOMANY = "detectManyToMany"; //$NON-NLS-1$
	public static final String DETECTONTTOONE = "detectOneToOne"; //$NON-NLS-1$
	public static final String DETECTOPTIMISTICLOCK = "detectOptimisticLock"; //$NON-NLS-1$
	public static final String PACKAGENAME = "packageName"; //$NON-NLS-1$
	public static final String REVENGFILE = "revEngFile"; //$NON-NLS-1$
	public static final String REVERSESTRATEGY = "reverseStrategy"; //$NON-NLS-1$
	public static final String ISREVENG = "isRevEng"; //$NON-NLS-1$
}
