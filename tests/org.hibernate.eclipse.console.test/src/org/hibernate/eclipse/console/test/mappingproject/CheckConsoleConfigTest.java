/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.mappingproject;

import org.hibernate.mapping.PersistentClass;

/**
 * @author vy (vyemialyanchyk@gmail.com)
 */
public class CheckConsoleConfigTest extends BaseTestSetCase {

	public CheckConsoleConfigTest() {
	}

	public CheckConsoleConfigTest(String name) {
		super(name);
	}

	public void testCheckConsoleConfiguration() {
		Object[] persClasses = getPersistenceClasses(true);
		assertTrue(persClasses.length > 0);
		for (int i = 0; i < persClasses.length; i++) {
			assertTrue(persClasses[i] instanceof PersistentClass);
		}
	}
}
