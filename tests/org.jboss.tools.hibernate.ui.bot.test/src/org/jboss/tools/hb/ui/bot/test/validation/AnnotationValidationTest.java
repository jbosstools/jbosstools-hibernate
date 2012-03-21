package org.jboss.tools.hb.ui.bot.test.validation;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.hb.ui.bot.common.ProjectExplorer;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.junit.Test;

/**
 * Hibernate annotation validation test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = false, perspective = "JPA")
public class AnnotationValidationTest extends HibernateBaseTest {

	final String prj = "jpatest40";
	final String pkg = "org.validation";
	
	@Test
	public void annotationValidationTest() {
		importTestProject("/resources/prj/hibernatelib");
		importTestProject("/resources/prj/jpatest40");
		
		checkGenericGeneratorValidation();
	}

	private void checkGenericGeneratorValidation() {
		ProjectExplorer.open(prj, "src",pkg);
		
		String desc = "No generator named \"mygen\" is defined in the persistence unit";
		String path = "/" + prj + "/src/org/validation";
		String resource = "GeneratorValidationEntity.java";
		String type = "JPA Problem";
		
		SWTBotTreeItem[] items = null;
		items = ProblemsView.getFilteredErrorsTreeItems(bot, desc, path, resource, type);				
		assertTrue(items.length == 1);
	}
}