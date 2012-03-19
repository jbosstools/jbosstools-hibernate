package org.jboss.tools.hb.ui.bot.test.jpa;

import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Test;
import org.osgi.framework.Version;

/**
 * Create JPA Project ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = false, perspective="JPA")
public class CreateJPAProjectTest extends HibernateBaseTest {
	
	final String prj = "jpa35test";
	
	@Test
	public void createJPAProject() {
		createProject();
	}

	private void createProject() {
		EntityType type = EntityType.JPA_PROJECT;
		eclipse.createNew(type);

		// JPA Project Page
		eclipse.waitForShell("New JPA Project");
		bot.textWithLabel("Project name:").setText(prj); 	
		bot.button(IDELabel.Button.NEXT).click();
		// Java Page
		bot.button(IDELabel.Button.NEXT).click();
		// JPA Facet Page
		Version version = jbt.getJBTVersion();
		if ((version.getMajor() == 3) && (version.getMinor() == 1))		
			bot.comboBoxInGroup("Platform").setSelection("Hibernate");
		else
			bot.comboBoxInGroup("Platform").setSelection("Hibernate (JPA 2.x)");		
		bot.comboBoxInGroup("JPA implementation").setSelection("Disable Library Configuration");		

		// Finish
		bot.button(IDELabel.Button.FINISH).click();
		eclipse.waitForClosedShell(bot.shell("New JPA Project"));
		util.waitForNonIgnoredJobs();
	}
}
