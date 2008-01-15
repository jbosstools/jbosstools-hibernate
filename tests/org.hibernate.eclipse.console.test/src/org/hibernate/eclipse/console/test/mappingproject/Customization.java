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
	public static final String TEST_PACKS_PATTERN = ".*\\.optlock.*";
	
	/**
	 * is stop after missing package or run further
	 */
	public static final boolean STOP_AFTER_MISSING_PACK = false;
	
	/**
	 * Shows each test run as individual
	 */
	public static final boolean SHOW_EACH_TEST = true;
	
	/**
	 * Delay in milliseconds after each package
	 */
	public static final int EACTH_PACK_TEST_DELAY = 0;
	
	/**
	 * Delay in milliseconds before closing workspace
	 */
	public static final long AFTER_ALL_PACKS_DELAY = 20000;
	
	/**
	 * Use time profiler
	 */
	public static final boolean USE_CONSOLE_OUTPUT = true;
	

}
