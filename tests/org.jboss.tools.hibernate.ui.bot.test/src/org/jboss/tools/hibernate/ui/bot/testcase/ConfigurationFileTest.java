 /*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.ui.bot.testcase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.hamcrest.Matcher;
import org.jboss.tools.hibernate.ui.bot.testsuite.HibernateTest;
import org.jboss.tools.hibernate.ui.bot.testsuite.Project;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.parts.ObjectMultiPageEditorBot;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ConfigurationFileTest extends HibernateTest {

	@BeforeClass
	public static void setUp() {
		HibernateTest.prepareProject();
	}
	
	@AfterClass
	public static void tearDown() { 
		HibernateTest.clean();
	}	
	
	/**
	 * TC 02 - Create Hibernate Configuration file with predefined variables
	 */
	@Test
	public void createFile() {
		eclipse.createNew(EntityType.HIBERNATE_CONFIGURATION_FILE);
		
		eclipse.selectTreeLocation(Project.PROJECT_NAME,"src");
		bot.textWithLabel(IDELabel.HBConfigurationWizard.FILE_NAME).setText(Project.CONF_FILE_NAME2);
		bot.button(IDELabel.Button.NEXT).click();

		// Create new configuration file
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.DATABASE_DIALECT).setSelection(Project.DB_DIALECT);
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.DRIVER_CLASS).setSelection(Project.DRIVER_CLASS);
		bot.comboBoxWithLabel(IDELabel.HBConsoleWizard.CONNECTION_URL).setText(Project.JDBC_STRING);
		
		// Create console configuration
		Matcher<Button> matcher = WidgetMatcherFactory.withText(IDELabel.HBConsoleWizard.CREATE_CONSOLE_CONFIGURATION);
		Button button = bot.widget(matcher);		
		SWTBotCheckBox cb = new SWTBotCheckBox(button); 
				
		if (!cb.isChecked())
			cb.click();
		
		SWTBotShell shell = bot.activeShell();
		bot.button(IDELabel.Button.FINISH).click();
		eclipse.waitForClosedShell(shell);				
	}

	/**
	 * TC 13
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws XPathExpressionException 
	 */
	@Test
	/*
	 * Edit Configuration file testcase. It checks: 1. Configuration file editor tabs availability,... 
	 */
	public void editFile() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		
		eclipse.openFile(Project.PROJECT_NAME, "src", Project.CONF_FILE_NAME2);
		SWTBotEditor editor = bot.editorByTitle(Project.CONF_FILE_NAME2);
		ObjectMultiPageEditorBot multiBot = new ObjectMultiPageEditorBot(Project.CONF_FILE_NAME2);

		// 1. Tabs availability		
		String[] pages = {"Session Factory","Security","Source"};
		for ( String page : pages ) {
			multiBot.selectPage(page);
			bot.sleep(TIME_500MS);
		}
		
		// 2. Test creating elements and properties
		// TODO creating element impl.
		
		// 3. Create new security element		
		SWTBot localBot = editor.bot();
		multiBot.selectPage(pages[1]);
		SWTBotTree secTree = localBot.tree().select("Security");

		ContextMenuHelper.clickContextMenu(secTree, "New", "Grant...");
		
		// Fillin Role, Entity-Name and Check action All *
		String roleName = "role1";
		String entityName = "entity1";
		
		bot.textWithLabel("Role*").setText("role1");
		bot.textWithLabel("Entity-Name*").setText("entity1");
		SWTBotShell shell = bot.shell("Add Grant");
		
		assertNotNull(shell);
		SWTBot shellBot = new SWTBot(shell.widget);
				
		SWTBotTree tree =  shellBot.tree();	
		SWTBotTreeItem[] items = tree.getAllItems();
		items[0].check();
		
		shellBot.button(IDELabel.Button.FINISH).click();
			
		// Click on Source tab for check
		multiBot.selectPage(pages[2]);
		List<String> lines = editor.toTextEditor().getLines();
		
		// XMLPath
		/*
		// Convert editor text into InputStreamReader		
		StringBuilder builder = new StringBuilder();
		for (String line : lines ) {
			builder.append(line);
			builder.append("\n");
		}				
		ByteArrayInputStream bs = new ByteArrayInputStream(builder.toString().getBytes());
			
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db;
		db = dbf.newDocumentBuilder();
		Document doc = db.parse(bs);
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("/person/security/grant");
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		for ( int i = 0; i < nodes.getLength(); i++ )
			System.out.println(nodes.item(i).getNodeValue());

		*/
		// Search for security grant
		
		boolean found = false;
		String wanted = "<grant actions=\"*\" entity-name=\"" + entityName + "\" role=\"" + roleName + "\"/>";
		System.out.println("Looking for:" + wanted);
		
		for (String line : lines) {
			System.out.println(line);
			if (line.trim().equals(wanted)) {
				found = true;
				System.out.println("Found");
			}			
		}		
		
		editor.saveAndClose();		
		assertTrue("Security element not found in xml", found);		
		bot.sleep(TIME_10S);
	}

}
