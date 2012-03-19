package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.configuration.CreateConfigurationFileTest;
import org.jboss.tools.hb.ui.bot.test.configuration.EditConfigurationFileTest;
import org.jboss.tools.hb.ui.bot.test.console.CreateConsoleConfigurationTest;
import org.jboss.tools.hb.ui.bot.test.console.EditConsoleConfigurationTest;
import org.jboss.tools.hb.ui.bot.test.diagram.MappingDiagramTest;
import org.jboss.tools.hb.ui.bot.test.generation.CreateCodeGenerationConfiguration;
import org.jboss.tools.hb.ui.bot.test.generation.JPADDLGenerationTest;
import org.jboss.tools.hb.ui.bot.test.generation.JPAEntitiesGenerationTest;
import org.jboss.tools.hb.ui.bot.test.jpa.CreateJPAProjectTest;
import org.jboss.tools.hb.ui.bot.test.jpa.EditPersistenceXMLTest;
import org.jboss.tools.hb.ui.bot.test.mappingfile.CreateMappingFileTest;
import org.jboss.tools.hb.ui.bot.test.mappingfile.EditMappingFileTest;
import org.jboss.tools.hb.ui.bot.test.perspective.JPAPerspectiveTest;
import org.jboss.tools.hb.ui.bot.test.perspective.PerspectiveTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RequirementAwareSuite.class)
@SuiteClasses({
	/* Configuration */
	CreateConfigurationFileTest.class, 
	EditConfigurationFileTest.class,
	/* console */
	CreateConsoleConfigurationTest.class,
	EditConsoleConfigurationTest.class,
	/* diagram */
	MappingDiagramTest.class,
	/* generation */
	CreateCodeGenerationConfiguration.class,
	JPADDLGenerationTest.class,
	JPAEntitiesGenerationTest.class,
	/* jpa */
	CreateJPAProjectTest.class,
	EditPersistenceXMLTest.class,
	/* mappingfile */
	CreateMappingFileTest.class,
	EditMappingFileTest.class,
	/* perspective */
	JPAPerspectiveTest.class,
	PerspectiveTest.class,
 	})
public class HibernateSuite {

}
