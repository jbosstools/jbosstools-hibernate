/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.mapper;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.console.test.project.ConfigurableTestProject;
import org.hibernate.eclipse.mapper.extractor.JavaTypeHandler;
import org.hibernate.eclipse.mapper.extractor.PackageHandler;
import junit.framework.TestCase;

/**
 * @author Vitali
 *
 */
public class HBMInfoExtractorTest extends TestCase {
	private HBMInfoExtractorStub sourceLocator = new HBMInfoExtractorStub();
	private ConfigurableTestProject testProj = null;

	protected void setUp() throws Exception {
		testProj = new ConfigurableTestProject("HBMInfoProj"); //$NON-NLS-1$
	}

	protected void tearDown() throws Exception {
		testProj.deleteIProject();
		testProj = null;
	}

	public void executeJavaTypeHandlerTest(String start, String attributeName) {
		sourceLocator.setPackageName("org"); //$NON-NLS-1$
	    IJavaProject project = testProj.getIJavaProject();
		JavaTypeHandler javaTypeHandler = new JavaTypeHandler(sourceLocator);
		ICompletionProposal[] res =
			javaTypeHandler.attributeCompletionProposals(project, null, 
				attributeName, start, 0);
		
	    assertTrue( res.length > 0 );
	}

	public void testJavaTypeHandler1() {
		executeJavaTypeHandlerTest("a", "name");  //$NON-NLS-1$//$NON-NLS-2$
	}

	public void testJavaTypeHandler2() {
		executeJavaTypeHandlerTest("", "name");  //$NON-NLS-1$//$NON-NLS-2$
	}

	public void testJavaTypeHandler3() {
		executeJavaTypeHandlerTest("a", "class"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testJavaTypeHandler4() {
		executeJavaTypeHandlerTest("", "class"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void executePackageHandlerTest(String start, String attributeName) {
		sourceLocator.setPackageName("org"); //$NON-NLS-1$
	    IJavaProject project = testProj.getIJavaProject();
	    PackageHandler packageHandler = new PackageHandler(sourceLocator);
		ICompletionProposal[] res =
			packageHandler.attributeCompletionProposals(project, null, 
				attributeName, start, 0);
		
	    assertTrue( res.length > 0 );
	}

	public void testPackageHandler1() {
		executePackageHandlerTest("o", "package");  //$NON-NLS-1$//$NON-NLS-2$
	}

	public void testPackageHandler2() {
		executePackageHandlerTest("", "package"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
