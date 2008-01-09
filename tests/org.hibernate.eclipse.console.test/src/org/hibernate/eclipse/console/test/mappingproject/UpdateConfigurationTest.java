package org.hibernate.eclipse.console.test.mappingproject;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

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
			fail("Error customising " + ProjectUtil.CFG_FILE_NAME + " file for package " 
					+ pack.getPath() + ".\n" + e.getMessage());
		}
	}
}
