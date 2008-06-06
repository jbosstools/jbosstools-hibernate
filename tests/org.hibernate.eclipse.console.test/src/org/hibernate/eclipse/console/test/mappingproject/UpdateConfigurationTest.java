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

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;

/**
 * @author Dmitry Geraskov
 *
 */
public class UpdateConfigurationTest extends TestCase {

	public void testUpdateConfiguration() throws JavaModelException{
		//fail("test fail");
		IPackageFragment pack = HibernateAllMappingTests.getActivePackage();
		assertNotNull( pack );
		try {
			ProjectUtil.customizeCFGFileForPack(pack);
		} catch (CoreException e) {
			String out = NLS.bind(ConsoleTestMessages.UpdateConfigurationTest_error_customising_file_for_package,
					new Object[] { ProjectUtil.CFG_FILE_NAME, pack.getPath(), e.getMessage() } );
			fail(out);
		}
	}
}
