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
package org.jboss.tools.hibernate.orm.test;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.mapper.extractor.HBMInfoExtractor;
import org.hibernate.eclipse.mapper.extractor.JavaTypeHandler;
import org.hibernate.eclipse.mapper.extractor.PackageHandler;
import org.jboss.tools.hibernate.orm.test.utils.project.TestProject;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.RuntimeServiceManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

/**
 * @author Vitali
 * @author koen
 */
public class HBMInfoExtractorTest {
	private HBMInfoExtractorStub sourceLocator = null;
	private TestProject testProj = null;

	@Before
	public void setUp() throws Exception {
		testProj = new TestProject("HBMInfoProj" + System.currentTimeMillis()); //$NON-NLS-1$
		IPackageFragmentRoot sourcePackageFragment = testProj.createSourceFolder();
		List<IPath> libs = testProj.copyLibs(testProj.getFolder("lib"));
		testProj.generateClassPath(libs, sourcePackageFragment);
		testProj.fullBuild();
		sourceLocator = new HBMInfoExtractorStub(RuntimeServiceManager.getInstance().getDefaultService());
	}

	@After
	public void tearDown() throws Exception {
		testProj.deleteIProject();
		testProj = null;
		sourceLocator = null;
	}

	private void executeJavaTypeHandlerTest(String start, String attributeName) {
		sourceLocator.setPackageName("org"); //$NON-NLS-1$
	    IJavaProject project = testProj.getIJavaProject();
		JavaTypeHandler javaTypeHandler = new JavaTypeHandler(sourceLocator);
		ICompletionProposal[] res =
			javaTypeHandler.attributeCompletionProposals(project, null, 
				attributeName, start, 0);
		
	    Assert.assertTrue( res.length > 0 );
	}

	@Test
	public void testJavaTypeHandler1() {
		executeJavaTypeHandlerTest("a", "name");  //$NON-NLS-1$//$NON-NLS-2$
	}

	@Test
	public void testJavaTypeHandler2() {
		executeJavaTypeHandlerTest("", "name");  //$NON-NLS-1$//$NON-NLS-2$
	}
	
	@Test
	public void testJavaTypeHandler3() {
		executeJavaTypeHandlerTest("a", "class"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testJavaTypeHandler4() {
		executeJavaTypeHandlerTest("", "class"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private void executePackageHandlerTest(String start, String attributeName) {
		sourceLocator.setPackageName("org"); //$NON-NLS-1$
	    IJavaProject project = testProj.getIJavaProject();
	    PackageHandler packageHandler = new PackageHandler(sourceLocator);
		ICompletionProposal[] res =
			packageHandler.attributeCompletionProposals(project, null, 
				attributeName, start, 0);
		
	    Assert.assertTrue( res.length > 0 );
	}

	@Test
	public void testPackageHandler1() {
		executePackageHandlerTest("o", "package");  //$NON-NLS-1$//$NON-NLS-2$
	}

	@Test
	public void testPackageHandler2() {
		executePackageHandlerTest("", "package"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public class HBMInfoExtractorStub extends HBMInfoExtractor {

		public HBMInfoExtractorStub(IService service) {
			super(service);
		}

		protected String packageName = null;
		
		protected String getPackageName(Node root) {
			return packageName;		
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;		
		}

	}
	
}
