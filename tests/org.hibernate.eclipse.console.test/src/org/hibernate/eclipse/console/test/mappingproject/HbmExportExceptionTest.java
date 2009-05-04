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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.ExporterException;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;

/**
 * @author Dmitry Geraskov
 *
 */
public class HbmExportExceptionTest extends TestCase {
	
	public void testHbmExportExceptionTest() throws Exception {
		IPackageFragment pack = HibernateAllMappingTests.getActivePackage();		
		try{
			KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
			final ConsoleConfiguration consCFG = knownConfigurations.find(ProjectUtil.ConsoleCFGName);
			assertNotNull(consCFG);
			consCFG.reset();
			consCFG.build();
			assertTrue(consCFG.hasConfiguration());
			consCFG.execute( new ExecutionContext.Command() {

				public Object execute() {
					if(consCFG.hasConfiguration()) {
						consCFG.getConfiguration().buildMappings();
					}
					return consCFG;
				}
			} );
			Configuration config = consCFG.getConfiguration();
			
			//delete old hbm files
			assertNotNull( pack );
			if (pack.getNonJavaResources().length > 0){
				Object[] ress = pack.getNonJavaResources();
				for (int i = 0; i < ress.length; i++) {
					if (ress[i] instanceof IFile){
						IFile res = (IFile)ress[i];
						if (res.getName().endsWith(".hbm.xml")){ //$NON-NLS-1$
							res.delete(true, false, null);
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
					pack.getResource().refreshLocal(IResource.DEPTH_ONE, null);
					waitForJobs();
					ProjectUtil.customizeCFGFileForPack(pack);
					assertNotNull(consCFG);
					consCFG.reset();
	
						consCFG.build();
						assertTrue(consCFG.hasConfiguration());
						consCFG.execute( new ExecutionContext.Command() {

							public Object execute() {
								if(consCFG.hasConfiguration()) {
									consCFG.getConfiguration().buildMappings();
								}
								return consCFG;
							}
						} );
						config = consCFG.getConfiguration();
				} catch (CoreException e) {
					String out = NLS.bind(ConsoleTestMessages.UpdateConfigurationTest_error_customising_file_for_package,
							new Object[] { ProjectUtil.CFG_FILE_NAME, pack.getPath(), e.getMessage() } );
					fail(out);
				}
			} catch (ExporterException e){
				throw (Exception)e.getCause();
			}
		} catch (Exception e){
			String newMessage = "\nPackage " + pack.getElementName() + ":";
			throw new WripperException(newMessage, e);
		}
	}
	
	private File getSrcFolder() throws JavaModelException{
		MappingTestProject mapProject = MappingTestProject.getTestProject();
		PackageFragmentRoot packageFragmentRoot = null;
		IPackageFragmentRoot[] roots = mapProject.getIJavaProject().getAllPackageFragmentRoots();
	    for (int i = 0; i < roots.length; i++) {
	    	if (roots[i].getClass() != PackageFragmentRoot.class) continue;
			packageFragmentRoot = (PackageFragmentRoot) roots[i];
			break;
	    }
	    assertNotNull(packageFragmentRoot);
	    return packageFragmentRoot.getResource().getLocation().toFile();
	}
	
	public void waitForJobs() {
		long start = System.currentTimeMillis();
		// Job.getJobManager().isIdle() is more efficient than EditorTestHelper.allJobsQuiet()
		// EditorTestHelper.allJobsQuiet() isn't thread-safe
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=198241 is fixed 
		//while (!EditorTestHelper.allJobsQuiet()) {
		while (!Job.getJobManager().isIdle()) {
			delay(1000);
			if ( (System.currentTimeMillis()-start) > MAX_IDLE ) 
				throw new RuntimeException("A long running task detected"); //$NON-NLS-1$
		}
	}
	private static final long MAX_IDLE = 30*60*1000L;
	
	protected void delay(long waitTimeMillis) {
		if (waitTimeMillis <= 0) return;
		Display display = Display.getCurrent();

		// If this is the UI thread,
		// then process input.
		if (display != null) {
			long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
			while (System.currentTimeMillis() < endTimeMillis) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.update();
		}

		// Otherwise, perform a simple sleep.
		else {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException e) {
				// Ignored.
			}
		}
	}
}

class WripperException extends Exception {
	
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
