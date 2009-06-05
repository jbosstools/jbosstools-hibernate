/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.mappingproject;


/**
 * @author Dmitry Geraskov
 *
 */
public class Customization {
	
	/**
	 * is use TEST_PACKS_PATTERN to packages
	 */
	public static final boolean U_TEST_PACKS_PATTERN = false;

	/**
	 * packages name pattern
	 */
	public static final String TEST_PACKS_PATTERN = 
		"mapping\\.((idclass))"; //$NON-NLS-1$
	
	/**
	 * is stop after missing package or run further
	 */
	public static final boolean STOP_AFTER_MISSING_PACK = false;
	
	/**
	 * Use time profiler
	 */
	public static final boolean USE_CONSOLE_OUTPUT = true;
	
	/**
	 * Hibernate Dialect
	 */
	public static final String HIBERNATE_DIALECT = "org.hibernate.dialect.HSQLDialect"; //$NON-NLS-1$
	
	

}
