package org.hibernate.eclipse.console.test.utils;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.tools.ant.filters.StringInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.test.mappingproject.Customization;
import org.hibernate.eclipse.console.wizards.ConsoleConfigurationCreationWizard;
import org.hibernate.eclipse.console.wizards.ConsoleConfigurationWizardPage;
import org.hibernate.eclipse.launch.ConsoleConfigurationMainTab;

/**
 * Test utility class to operate with Hibernate Console configuration
 *   
 * @author vitali
 */
public class ConsoleConfigUtils {
	
	private ConsoleConfigUtils() {}

	private static final String XML_HEADER = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n") //$NON-NLS-1$
													.append("<!DOCTYPE hibernate-configuration PUBLIC\n") //$NON-NLS-1$
													.append("\"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"\n") //$NON-NLS-1$
													.append("\"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">\n").toString(); //$NON-NLS-1$

	private static final String XML_CFG_START = new StringBuilder("<hibernate-configuration>\n") //$NON-NLS-1$
													.append("<session-factory>\n") //$NON-NLS-1$
													.append("<property name=\"hibernate.dialect\">") //$NON-NLS-1$
													.append(Customization.HIBERNATE_DIALECT)
													.append("</property>").toString(); //$NON-NLS-1$

	private static final String XML_CFG_END = new StringBuilder("</session-factory>\n") //$NON-NLS-1$
													.append("</hibernate-configuration>\n").toString();	 //$NON-NLS-1$

	public static final String CFG_FILE_NAME = "hibernate.cfg.xml"; //$NON-NLS-1$

	/**
	 * Create hibernate.cfg.xml file content for the particular test package content.
	 * @param pack
	 * @return a string, which is hibernate.cfg.xml content
	 * @throws CoreException
	 */
	public static String createCfgXmlContent(IPackageFragment pack) throws CoreException {
		StringBuilder str = new StringBuilder();
		str.append(XML_HEADER);
		str.append(XML_CFG_START);
		if (pack.getNonJavaResources().length > 0){
			Object[] ress = pack.getNonJavaResources();
			for (int i = 0; i < ress.length; i++) {
				if (!(ress[i] instanceof IFile)) {
					continue;
				}
				IFile file = (IFile)ress[i];
				if (file.getName().endsWith(".hbm.xml")){ //$NON-NLS-1$
					str.append("<mapping resource=\"");//$NON-NLS-1$
					str.append(pack.getElementName().replace('.', '/'));
					str.append('/');
					str.append(file.getName());
					str.append("\"/>\n");  //$NON-NLS-1$
				}
			}
		}
		str.append(XML_CFG_END);
		return str.toString();
	}
	
	/**
	 * Customize hibernate.cfg.xml file for the particular test package content.
	 * @param pack
	 * @throws CoreException
	 */
	public static void customizeCfgXmlForPack(IPackageFragment pack) throws CoreException {
		StringInputStream sis = new StringInputStream(createCfgXmlContent(pack));
		IFolder srcFolder = (IFolder) pack.getParent().getResource();
		IFile iFile = srcFolder.getFile(CFG_FILE_NAME);
		if (iFile.exists()) {
			iFile.delete(true, null);
		}
		iFile.create(sis, true, null);
		try {
			sis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * "Launch" the wizard and create hibernate console configuration in the current workspace. 
	 * @param name - console configuration name
	 * @param cfgFilePath - path to hibernate.cfg.xml
	 * @param project - name of java project selected for console configuration
	 * @throws CoreException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static void createConsoleConfig(String name, IPath cfgFilePath, String project) throws CoreException, NoSuchFieldException, IllegalAccessException {
		final IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final ConsoleConfigurationCreationWizard wiz = new ConsoleConfigurationCreationWizard();
		final WizardDialog wdialog = new WizardDialog(win.getShell(), wiz);
		wdialog.create();
		ConsoleConfigurationWizardPage page = ((ConsoleConfigurationWizardPage)wiz.getPages()[0]);			
		ILaunchConfigurationTab[] tabs = page.getTabs();
		ConsoleConfigurationMainTab main = (ConsoleConfigurationMainTab) tabs[0];
		Class<? extends ConsoleConfigurationMainTab> clazz = main.getClass();
		Field projectName = clazz.getDeclaredField("projectNameText"); //$NON-NLS-1$
		projectName.setAccessible(true);
		Text text = (Text) projectName.get(main);
		text.setText(project);
		page.setConfigurationFilePath(cfgFilePath);
		page.setName(name);
		page.performFinish();
		wdialog.close();
	}

	/**
	 * "Launch" the wizard and create hibernate jpa console configuration in the current workspace. 
	 * @param name - console configuration name
	 * @param cfgFilePath - path to hibernate.cfg.xml
	 * @param project - name of java project selected for console configuration
	 * @throws CoreException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static void createJpaConsoleConfig(String name, String project, String persistenceUnitName) throws CoreException, NoSuchFieldException, IllegalAccessException {
		final IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final ConsoleConfigurationCreationWizard wiz = new ConsoleConfigurationCreationWizard();
		final WizardDialog wdialog = new WizardDialog(win.getShell(), wiz);
		wdialog.create();
		ConsoleConfigurationWizardPage page = ((ConsoleConfigurationWizardPage)wiz.getPages()[0]);			
		ILaunchConfigurationTab[] tabs = page.getTabs();
		ConsoleConfigurationMainTab main = (ConsoleConfigurationMainTab) tabs[0];
		Class<? extends ConsoleConfigurationMainTab> clazz = main.getClass();
		//
		Field projectName = clazz.getDeclaredField("projectNameText"); //$NON-NLS-1$
		projectName.setAccessible(true);
		Text text = (Text)projectName.get(main);
		text.setText(project);
		//
		Field persistenceUnitNameText = clazz.getDeclaredField("persistenceUnitNameText"); //$NON-NLS-1$
		persistenceUnitNameText.setAccessible(true);
		text = (Text)persistenceUnitNameText.get(main);
		text.setText(persistenceUnitName);
		//
		Field jpaMode = clazz.getDeclaredField("jpaMode"); //$NON-NLS-1$
		Field coreMode = clazz.getDeclaredField("coreMode"); //$NON-NLS-1$
		jpaMode.setAccessible(true);
		coreMode.setAccessible(true);
		Button button = (Button)coreMode.get(main);
		button.setSelection(false);
		button = (Button)jpaMode.get(main);
		button.setSelection(true);
		//
		page.setName(name);
		page.performFinish();
		wdialog.close();
	}

	/**
	 * Delete console configuration with given name. 
	 * @param name
	 */
	public static void deleteConsoleConfig(String name) {
		final KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		final ConsoleConfiguration consoleConfig = knownConfigurations.find(name);
		if (consoleConfig != null) {
			consoleConfig.reset();
		}
		knownConfigurations.removeConfiguration(consoleConfig, false);
	}
}
