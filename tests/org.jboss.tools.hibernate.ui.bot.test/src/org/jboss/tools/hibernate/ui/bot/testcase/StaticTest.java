package org.jboss.tools.hibernate.ui.bot.testcase;

import java.io.File;

import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.helper.FileHelper;
import org.jboss.tools.ui.bot.ext.helper.ImportHelper;
import org.jboss.tools.ui.bot.ext.helper.ResourceHelper;
import org.junit.Test;

@Require(db=@DB, clearProjects = false,  perspective="Hibernate")
public class StaticTest extends SWTTestExt {
		
	@Test 
	public void importHibernateProjects() {
		String rpath = ResourceHelper.getResourceAbsolutePath(
				Activator.PLUGIN_ID, "resources/prj");
		String wpath = ResourceHelper.getWorkspaceAbsolutePath();

		String[] projects = {"hibernate35.zip","hibernate36.zip","hibernate40.zip"};
		File outputDir = new File(wpath);
		
		for (int i = 0; i < projects.length; i++ ) {			
			File archive = new File(rpath + File.separator + projects[i]);
			try {
				FileHelper.unzipArchive(archive, outputDir);
			} catch (Exception e) {
				fail("Unable to extract projects");
			}
		}
		
		ImportHelper.importAllProjects(wpath);
	}
	
	
}
