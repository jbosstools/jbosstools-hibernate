package org.jboss.tools.hb.ui.bot.suite;

import org.jboss.tools.hb.ui.bot.test.ant.ExportAntCodeGenerationTest;
import org.jboss.tools.hb.ui.bot.test.configuration.CreateConfigurationFileTest;
import org.jboss.tools.hb.ui.bot.test.configuration.EditConfigurationFileTest;
import org.jboss.tools.hb.ui.bot.test.console.CreateConsoleConfigurationTest;
import org.jboss.tools.hb.ui.bot.test.console.EditConsoleConfigurationTest;
import org.jboss.tools.hb.ui.bot.test.criteria.CriteriaEditorTest;
import org.jboss.tools.hb.ui.bot.test.diagram.MappingDiagramTest;
import org.jboss.tools.hb.ui.bot.test.generation.CreateCodeGenerationConfiguration;
import org.jboss.tools.hb.ui.bot.test.generation.JPADDLGenerationTest;
import org.jboss.tools.hb.ui.bot.test.generation.JPAEntitiesGenerationTest;
import org.jboss.tools.hb.ui.bot.test.generation.RunSchemaExportTest;
import org.jboss.tools.hb.ui.bot.test.hql.HQLEditorTest;
import org.jboss.tools.hb.ui.bot.test.jpa.CreateJPAEntityTest;
import org.jboss.tools.hb.ui.bot.test.jpa.CreateJPAProjectTest;
import org.jboss.tools.hb.ui.bot.test.jpa.EditPersistenceXMLTest;
import org.jboss.tools.hb.ui.bot.test.mappingfile.CreateMappingFileTest;
import org.jboss.tools.hb.ui.bot.test.mappingfile.EditMappingFileTest;
import org.jboss.tools.hb.ui.bot.test.perspective.JPAPerspectiveTest;
import org.jboss.tools.hb.ui.bot.test.perspective.PerspectiveTest;
import org.jboss.tools.hb.ui.bot.test.reveng.CreateRevengFileTest;
import org.jboss.tools.hb.ui.bot.test.validation.AnnotationValidationTest;
import org.jboss.tools.hb.ui.bot.test.view.JPADetailViewTest;
import org.jboss.tools.hb.ui.bot.test.view.PackageInfoTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(RequirementAwareSuite.class)
@SuiteClasses({
	/* Ant */
	ExportAntCodeGenerationTest.class,
	/* Configuration */
	CreateConfigurationFileTest.class, 
	EditConfigurationFileTest.class,
	/* console */
	CreateConsoleConfigurationTest.class,
	EditConsoleConfigurationTest.class,
	/* criteria */
	CriteriaEditorTest.class,
	/* diagram */
	MappingDiagramTest.class,
	/* generation */
	CreateCodeGenerationConfiguration.class,
	JPADDLGenerationTest.class,
	JPAEntitiesGenerationTest.class,
	RunSchemaExportTest.class,
	/* hql */
	HQLEditorTest.class,
	/* jpa */
	CreateJPAProjectTest.class,
	CreateJPAEntityTest.class,
	EditPersistenceXMLTest.class,
	/* mappingfile */
	CreateMappingFileTest.class,
	EditMappingFileTest.class,
	/* perspective */
	JPAPerspectiveTest.class,
	PerspectiveTest.class,
	/* reveng */
	CreateRevengFileTest.class,
	/* validation */
	AnnotationValidationTest.class,
	/* view */
	JPADetailViewTest.class,
	PackageInfoTest.class
 	})
public class HibernateSuite {

}
