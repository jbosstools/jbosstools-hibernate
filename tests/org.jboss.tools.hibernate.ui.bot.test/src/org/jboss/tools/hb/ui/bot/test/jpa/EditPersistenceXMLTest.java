package org.jboss.tools.hb.ui.bot.test.jpa;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotMultiPageEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.jboss.tools.hb.ui.bot.common.Tree;
import org.jboss.tools.hb.ui.bot.test.HibernateBaseTest;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.TestConfigurator;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.helper.StringHelper;
import org.jboss.tools.ui.bot.ext.parts.ContentAssistBot;
import org.jboss.tools.ui.bot.ext.parts.SWTBotEditorExt;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.Test;

/**
 * Create JPA Project ui bot test
 * 
 * @author jpeterka
 * 
 */
@Require(clearProjects = true, perspective="JPA")
public class EditPersistenceXMLTest extends HibernateBaseTest {
	
	final String prj = "jpatest35";
	
	@Test
	public void createJPAProject() {
		importTestProject("/resources/prj/" + prj);
		openPersistenceXML();
		editPersistenceHibernatePageXML();
		checkCAInConfigurationEditorXML();
	}

	private void openPersistenceXML() {
		SWTBotView pe = open.viewOpen(ActionItem.View.GeneralProjectExplorer.LABEL);
		Tree.open(pe.bot(), prj,"JPA Content","persistence.xml");
	}

	private void editPersistenceHibernatePageXML() {
		SWTBotEditor editor = 	bot.editorByTitle("persistence.xml");
		editor.show();
		SWTBotMultiPageEditor mpe = new SWTBotMultiPageEditor(editor.getReference(), bot);
		mpe.activatePage("Hibernate");

		// Fill in 
		String dialect = DatabaseHelper.getDialect(TestConfigurator.currentConfig.getDB().dbType);
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.DATABASE_DIALECT).setSelection(dialect);
		String drvClass = DatabaseHelper.getDriverClass(TestConfigurator.currentConfig.getDB().dbType);
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.DRIVER_CLASS).setSelection(drvClass);		
		String jdbc = TestConfigurator.currentConfig.getDB().jdbcString;
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.CONNECTION_URL).setText(jdbc);
		bot.textWithLabel("Username:").setText("sa");
 
		editor.save();
		mpe.activatePage("Source");

		// Check xml content
		String text = mpe.toTextEditor().getText();
		StringHelper helper = new StringHelper(text);		
		String str  =  "<property name=\"hibernate.dialect\" value=\"org.hibernate.dialect.HSQLDialect\"/>";
		helper.getPositionBefore(str);
		str  =  "<property name=\"hibernate.connection.driver_class\" value=\"org.hsqldb.jdbcDriver\"/>";
		helper.getPositionBefore(str);	
	}
	
	private void checkCAInConfigurationEditorXML() {
		SWTBotEditor editor = 	bot.editorByTitle("persistence.xml");
		editor.show();
		SWTBotMultiPageEditor mpe = new SWTBotMultiPageEditor(editor.getReference(), bot);
		mpe.activatePage("Source");
		
		// Code completion
		String text = mpe.toTextEditor().getText();
		StringHelper helper = new StringHelper(text);
		Point p = helper.getPositionBefore("</persistence-unit>");
		editor.toTextEditor().selectRange(p.y, p.x, 0);
		editor.save();
		SWTBotEditorExt editorExt = new SWTBotEditorExt(editor.getReference(), bot);
		ContentAssistBot ca = new ContentAssistBot(editorExt);
		ca.useProposal("class");
		
		editor.save();
	}
}
