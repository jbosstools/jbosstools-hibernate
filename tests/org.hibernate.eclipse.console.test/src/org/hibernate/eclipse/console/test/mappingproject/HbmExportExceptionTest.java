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
package org.hibernate.eclipse.console.test.mappingproject;

import java.io.File;
import java.io.PrintWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.osgi.util.NLS;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.project.ConfigurableTestProject;
import org.hibernate.eclipse.console.test.utils.ConsoleConfigUtils;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.ExporterException;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class HbmExportExceptionTest extends BaseTestSetCase {

	protected ConfigurableTestProject testProject = null;

	public HbmExportExceptionTest() {
	}

	public HbmExportExceptionTest(String name) {
		super(name);
	}

	protected void tearDown() throws Exception {
		testProject = null;
		super.tearDown();
	}
	
	public void testHbmExportExceptionTest() throws Exception {
		try {
			Object[] persClassesInit = getPersistenceClasses(false);

			final ConsoleConfiguration consCFG = getConsoleConfig();
			Configuration config = consCFG.getConfiguration();
			//delete old hbm files
			assertNotNull(testPackage);
			int nDeleted = 0;
			if (testPackage.getNonJavaResources().length > 0) {
				Object[] ress = testPackage.getNonJavaResources();
				for (int i = 0; i < ress.length; i++) {
					if (ress[i] instanceof IFile){
						IFile res = (IFile)ress[i];
						if (res.getName().endsWith(".hbm.xml")) { //$NON-NLS-1$
							res.delete(true, false, null);
							nDeleted++;
						}
					}
				}
			}
			
			HibernateMappingGlobalSettings hmgs = new HibernateMappingGlobalSettings();
			
			HibernateMappingExporter hce = new HibernateMappingExporter(config, 
				getSrcFolder());
			
			hce.setGlobalSettings(hmgs);
			try {
				hce.start();
				ArtifactCollector collector = hce.getArtifactCollector();
				collector.formatFiles();
	
				try {//build generated configuration
					testPackage.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
					testPackage.getJavaProject().getProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
					ConsoleConfigUtils.customizeCfgXmlForPack(testPackage);
					assertNotNull(consCFG);
					consCFG.reset();
					consCFG.build();
					assertTrue(consCFG.hasConfiguration());
					consCFG.execute(new ExecutionContext.Command() {
						public Object execute() {
							if(consCFG.hasConfiguration()) {
								consCFG.getConfiguration().buildMappings();
							}
							return consCFG;
						}
					});
					config = consCFG.getConfiguration();
				} catch (CoreException e) {
					String out = NLS.bind(ConsoleTestMessages.UpdateConfigurationTest_error_customising_file_for_package,
							new Object[] { ConsoleConfigUtils.CFG_FILE_NAME, testPackage.getPath(), e.getMessage() } );
					fail(out);
				}
			} catch (ExporterException e){
				throw (Exception)e.getCause();
			}
			//
			Object[] persClassesReInit = getPersistenceClasses(false);
			//
			int nCreated = 0;
			if (testPackage.getNonJavaResources().length > 0) {
				Object[] ress = testPackage.getNonJavaResources();
				for (int i = 0; i < ress.length; i++) {
					if (ress[i] instanceof IFile) {
						IFile res = (IFile)ress[i];
						if (res.getName().endsWith(".hbm.xml")) { //$NON-NLS-1$
							nCreated++;
						}
					}
				}
			}
			//
			assertTrue(persClassesInit.length == persClassesReInit.length);
			assertTrue(nCreated > 0);
			assertTrue(nDeleted >= 0 && persClassesInit.length > 0);
			assertTrue(nCreated <= persClassesInit.length);
		} catch (Exception e){
			String newMessage = "\nPackage " + testPackage.getElementName() + ":"; //$NON-NLS-1$ //$NON-NLS-2$
			throw new WripperException(newMessage, e);
		}
	}
	
	private File getSrcFolder() throws JavaModelException{
		PackageFragmentRoot packageFragmentRoot = null;
		IPackageFragmentRoot[] roots = testProject.getIJavaProject().getAllPackageFragmentRoots();
	    for (int i = 0; i < roots.length && packageFragmentRoot == null; i++) {
	    	if (roots[i].getClass() == PackageFragmentRoot.class) {
				packageFragmentRoot = (PackageFragmentRoot) roots[i];
	    	}
	    }
	    assertNotNull(packageFragmentRoot);
	    return packageFragmentRoot.getResource().getLocation().toFile();
	}

	public ConfigurableTestProject getTestProject() {
		return testProject;
	}

	public void setTestProject(ConfigurableTestProject testProject) {
		this.testProject = testProject;
	}
}

class WripperException extends Exception {
	
	private static final long serialVersionUID = 8192540921613389467L;
	private String message;
	
	public WripperException(String message, Exception cause){
		super(cause);
		this.message = message;
		setStackTrace(cause.getStackTrace());
	}
	
	@Override
	public Throwable getCause() {
		return null;
	}
	
	@Override
	public void printStackTrace(PrintWriter s) {
		s.println(message);
		super.getCause().printStackTrace(s);
	}
}
