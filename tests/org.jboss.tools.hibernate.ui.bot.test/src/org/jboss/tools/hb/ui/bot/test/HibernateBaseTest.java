package org.jboss.tools.hb.ui.bot.test;

import java.io.File;
import java.io.IOException;

import org.jboss.tools.hibernate.ui.bot.testcase.Activator;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.helper.FileHelper;
import org.jboss.tools.ui.bot.ext.helper.ImportHelper;
import org.jboss.tools.ui.bot.ext.helper.ResourceHelper;
import org.jboss.tools.ui.bot.ext.helper.SubversiveHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.view.ErrorLogView;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class HibernateBaseTest extends SWTTestExt {
	
	@BeforeClass
	public static void hbBaseBeforeClass() {
		eclipse.closeView(IDELabel.View.WELCOME);
		eclipse.closeView(IDELabel.View.JBOSS_CENTRAL);
		eclipse.closeAllEditors();
		util.waitForAll();
		bot.closeAllShells();
		SubversiveHelper.disableSVNDecoration();
	}
	
	@Test
	public void emptyTest() {
		
	}
	
	public void emptyErrorLog() {
		ErrorLogView el = new ErrorLogView();
		if (el.getRecordCount() > 0) {
			el.delete();
		}
		util.waitForNonIgnoredJobs();
	}
	
	public void checkErrorLog() {
		ErrorLogView el = new ErrorLogView();
		int count = el.getRecordCount();
		if (count > 0) {
			el.logMessages();
			// Ignored for now
			// fail("Unexpected messages in Error log, see test log");
		}
	}
	
	public void importTestProject(String dir) {
		String rpath = ResourceHelper.getResourceAbsolutePath(
				Activator.PLUGIN_ID, dir);
		String wpath = ResourceHelper.getWorkspaceAbsolutePath() + dir;
		File rfile = new File(rpath);
		File wfile = new File(wpath);
		
		wfile.mkdirs();
		try {
			FileHelper.copyFilesBinaryRecursively(rfile, wfile, null);
		} catch (IOException e) {
			fail("Unable to copy test project");
		}
		ImportHelper.importAllProjects(wpath);
		util.waitForNonIgnoredJobs();		
	}
	
	@AfterClass
	public static void afterClass() {
		// wait for all jobs
		util.waitForAll();
	}
		
		 
}
