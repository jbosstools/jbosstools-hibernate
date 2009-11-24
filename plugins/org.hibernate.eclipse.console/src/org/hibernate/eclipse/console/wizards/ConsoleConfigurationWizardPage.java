/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.EclipseConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.AddConfigurationAction;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.launch.ConsoleConfigurationMainTab;
import org.hibernate.eclipse.launch.ConsoleConfigurationTabGroup;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.hibernate.util.StringHelper;

/**
 * This wizardpage wraps the LaunchConfiguration based tabs and thus mimicks the normal launch configuration ui.
 * Most logic should go to the launch config tabs, very little logic should need to be in this class (besides handling the tabs and name of the configuraiton).
 * 
 * @author max
 */
@SuppressWarnings("restriction")
public class ConsoleConfigurationWizardPage extends WizardPage implements
		ILaunchConfigurationDialog {

	/**
	 * Name label widget
	 */
	protected Label nameLabel;

	/**
	 * Name text widget
	 */
	protected Text nameWidget;

	/**
	 * custom tab folder control
	 */
	protected CTabFolder tabFolder;

	/**
	 * Tab controls
	 */
	protected ConsoleConfigurationTabGroup tabGroup;

	protected ILaunchConfigurationWorkingCopy currentLaunchConfig;

	protected int currentTabIndex;
	
	protected ISelection selection;

	private boolean initializingTabs;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ConsoleConfigurationWizardPage(ISelection selection) {
		super(HibernateConsoleMessages.ConsoleConfigurationWizardPage_config_page);
		setTitle(HibernateConsoleMessages.ConsoleConfigurationWizardPage_create_hibernate_console_config);
		setDescription(HibernateConsoleMessages.ConsoleConfigurationWizardPage_this_wizard_allows);
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL
				| SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		Composite container = new Composite(sc, SWT.NONE);

		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(gd);

		nameLabel = new Label(container, SWT.HORIZONTAL | SWT.LEFT);
		nameLabel.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_name);
		nameLabel.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING));

		nameWidget = new Text(container, SWT.SINGLE | SWT.BORDER);
		nameWidget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tabFolder = new CTabFolder(container, SWT.TOP | SWT.NO_REDRAW_RESIZE
				| SWT.NO_TRIM | SWT.FLAT);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		ColorRegistry reg = JFaceResources.getColorRegistry();
		Color c1 = reg.get("org.eclipse.ui.workbench.ACTIVE_TAB_BG_START"), //$NON-NLS-1$
		c2 = reg.get("org.eclipse.ui.workbench.ACTIVE_TAB_BG_END"); //$NON-NLS-1$
		tabFolder.setSelectionBackground(new Color[] { c1, c2 },
				new int[] { 100 }, true);
		tabFolder.setSelectionForeground(reg
				.get("org.eclipse.ui.workbench.ACTIVE_TAB_TEXT_COLOR")); //$NON-NLS-1$
		tabFolder.setSimple(PlatformUI.getPreferenceStore().getBoolean(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS));
		tabFolder.setLayoutData(gd);
		tabFolder.setBorderVisible(true);
		tabFolder.setFont(parent.getFont());

		sc.setContent(container);

		initTabs(tabFolder);

		try {
			performStart();
		} catch (CoreException ce) {
			HibernateConsolePlugin
				.getDefault().showError(getShell(),
					HibernateConsoleMessages.AddConfigurationAction_problem_add_console_config,
					ce);
		}
		try {
			initialize(currentLaunchConfig, selection);
		} catch (CoreException ce) {
			HibernateConsolePlugin
				.getDefault().logErrorMessage(
					HibernateConsoleMessages.ConsoleConfigurationWizardPage_problem_while_initializing_cc,
					ce);
		}
		performInit();

		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		};
		nameWidget.addModifyListener(modifyListener);
		
		setActiveTab(0);		
		sc.setMinSize(tabFolder.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
		dialogChanged();
	}

	protected void initTabs(CTabFolder folder) {

		tabGroup = new ConsoleConfigurationTabGroup();
		tabGroup.createTabs(this, null); // TODO: set the proper mode here
		ILaunchConfigurationTab[] tabs = tabGroup.getTabs();
		for (int i = 0; i < tabs.length; i++) {
			tabs[i].setLaunchConfigurationDialog(this);
		}
		for (int i = 0; i < tabs.length; i++) {
			CTabItem item = new CTabItem(folder, SWT.BORDER);
			item.setText(tabs[i].getName());
			item.setImage(tabs[i].getImage());
			tabs[i].createControl(item.getParent());
			Control control = tabs[i].getControl();
			if (control != null) {
				item.setControl(control);
			}
		}
	}

	public void performStart() throws CoreException {
		currentLaunchConfig = AddConfigurationAction.createTemporaryLaunchConfiguration().getWorkingCopy();		
	}
	
	public void performCancel() throws CoreException {
		AddConfigurationAction.deleteTemporaryLaunchConfigurations();		
	}

	public void performFinish() throws CoreException {
		currentLaunchConfig.rename(nameWidget.getText().trim());
		currentLaunchConfig.doSave();
		AddConfigurationAction.makeTemporaryLaunchConfigurationsPermanent();		
	}

	public void performInit() {
		initializingTabs = true;
		nameWidget.setText(currentLaunchConfig.getName());
		tabGroup.initializeFrom(currentLaunchConfig);
		initializingTabs = false;
	}

	/**
	 * Ensures that both text fields are set.
	 */
	protected void dialogChanged() {
		if(initializingTabs) {
			return;
		}
		String messageWarning = null;
		String messageError = null;
		if (tabGroup == null || currentLaunchConfig == null) {
			setMessage(messageWarning);
			updateStatus(messageError);
			return;
		}
		String name = getConfigurationName();
		if (name != null) {
			name = name.trim();
		}
		messageError = LaunchHelper.verifyConfigurationName(name);
		if (messageError != null) {
			setMessage(messageWarning);
			updateStatus(messageError);
			return;
		}
		
		tabGroup.performApply(currentLaunchConfig);
		
		ILaunchConfigurationTab[] tabs = tabGroup.getTabs();		
		for (int i = 0; i < tabs.length; i++) {
			if (tabs[i].isValid(currentLaunchConfig)) {
				if (messageWarning == null) {
					messageWarning = tabs[i].getMessage();
					if(messageWarning!=null) {
						System.out.println(NLS.bind(HibernateConsoleMessages.ConsoleConfigurationWizardPage_warnings, tabs[i].getName(), messageWarning));
					}
				}
			} else {
				if (messageError == null) {
					messageError = tabs[i].getErrorMessage();
					if(messageError!=null) {
						System.out.println(NLS.bind(HibernateConsoleMessages.ConsoleConfigurationWizardPage_errors, tabs[i].getName(), messageError));
					}
				}
			}
		}
		setMessage(messageWarning);
		updateStatus(messageError);
	}

	/**
	 * Notification that a tab has been selected
	 * 
	 * Disallow tab changing when the current tab is invalid. Update the config
	 * from the tab being left, and refresh the tab being entered.
	 */
	protected void handleTabSelected() {
		if(tabGroup==null) return;
		ILaunchConfigurationTab[] tabs = tabGroup.getTabs();
		if (currentTabIndex == tabFolder.getSelectionIndex() || tabs == null
				|| tabs.length == 0 || currentTabIndex > (tabs.length - 1)) {
			return;
		}
		if (currentTabIndex != -1) {
			ILaunchConfigurationTab tab = tabs[currentTabIndex];
			if (currentLaunchConfig != null) {
				tab.deactivated(currentLaunchConfig);
				getActiveTab().activated(currentLaunchConfig);				
			}			
		}
		currentTabIndex = tabFolder.getSelectionIndex();
	}

	
	public void setActiveTab(int index) {
		if(tabGroup==null) return;
		ILaunchConfigurationTab[] tabs = tabGroup.getTabs();		
		if (tabs != null && index >= 0 && index < tabs.length) {
			tabFolder.setSelection(index);
			handleTabSelected();
			dialogChanged();
		}
	}

	/**
	 * Returns the currently active <code>ILaunchConfigurationTab</code> being
	 * displayed, or <code>null</code> if there is none.
	 * 
	 * @return currently active <code>ILaunchConfigurationTab</code>, or
	 *         <code>null</code>.
	 */
	public ILaunchConfigurationTab getActiveTab() {
		if(tabGroup==null) return null;
		ILaunchConfigurationTab[] tabs = tabGroup.getTabs();		
		if (tabFolder != null && tabs != null) {
			int pageIndex = tabFolder.getSelectionIndex();
			if (pageIndex >= 0) {
				return tabs[pageIndex];
			}
		}
		return null;
	}

	private String getConfigurationName() {
		return nameWidget.getText();
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String generateName(String name) {
		// empty ILaunchConfigurationDialog method stub
		return null;
	}

	public String getMode() {
		// empty ILaunchConfigurationDialog method stub
		return null;
	}

	public ILaunchConfigurationTab[] getTabs() {
		if(tabGroup==null) {
			return null;
		} else {
			return tabGroup.getTabs();
		}
	}

	public void setActiveTab(ILaunchConfigurationTab tab) {
		// empty ILaunchConfigurationDialog method stub
	}

	public void setName(String name) {
		nameWidget.setText(name);		
	}

	public void updateButtons() {
		// empty ILaunchConfigurationDialog method stub
	}

	public void updateMessage() {
		dialogChanged();
	}

	public void run(boolean fork, boolean cancelable,
			IRunnableWithProgress runnable) throws InvocationTargetException,
			InterruptedException {
		// empty ILaunchConfigurationDialog method stub
	}

	static protected String nonEmptyTrimOrNull(Text t) {
		return nonEmptyTrimOrNull( t.getText() );
	}

	static String nonEmptyTrimOrNull(String str) {
		if(StringHelper.isEmpty( str )) {
			return null;
		} else {
			return str.trim();
		}
	}

	/////
	// auxiliary functions to setup config parameters
	// BEGIN
	static protected void setPathAttribute(ILaunchConfigurationWorkingCopy currentLaunchConfig, String attr, IPath path) {
		if (path != null) {
			currentLaunchConfig.setAttribute(attr, nonEmptyTrimOrNull(path.toOSString()));
		}
		else {
			currentLaunchConfig.setAttribute(attr, (String)null);
		}
	}
	
	static protected void setProjAttribute(ILaunchConfigurationWorkingCopy currentLaunchConfig, String attr, IJavaProject proj) {
		if (proj != null) {
			currentLaunchConfig.setAttribute(attr, nonEmptyTrimOrNull(proj.getElementName()));
			currentLaunchConfig.setAttribute(LaunchConfiguration.ATTR_MAPPED_RESOURCE_PATHS,
					Collections.singletonList(new Path(nonEmptyTrimOrNull(proj.getElementName())).makeAbsolute().toString()));
			currentLaunchConfig.setAttribute(LaunchConfiguration.ATTR_MAPPED_RESOURCE_TYPES, Collections.singletonList(Integer.toString(IResource.PROJECT)));
		}
		else {
			currentLaunchConfig.setAttribute(attr, (String)null);
			currentLaunchConfig.removeAttribute(LaunchConfiguration.ATTR_MAPPED_RESOURCE_PATHS);
			currentLaunchConfig.removeAttribute(LaunchConfiguration.ATTR_MAPPED_RESOURCE_TYPES);
		}
	}
	
	static protected void setStrAttribute(ILaunchConfigurationWorkingCopy currentLaunchConfig, String attr, String str) {
		if (str != null) {
			currentLaunchConfig.setAttribute(attr, nonEmptyTrimOrNull(str));
		}
		else {
			currentLaunchConfig.setAttribute(attr, (String)null);
		}
	}
	// END
	/////

	/**
	 * Init launch config parameters from proper selection
	 * 
	 * @param currentSelection
	 */
	static public void initialize(ILaunchConfigurationWorkingCopy launchConfig, ISelection currentSelection) throws CoreException {
		BestGuessConsoleConfigurationVisitor v = new BestGuessConsoleConfigurationVisitor();
		// use selection to build configuration from it...
		if (currentSelection != null && currentSelection.isEmpty() == false
				&& currentSelection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) currentSelection;
			if (ssel.size() > 1) {
				return;
			}
			Object obj = ssel.getFirstElement();

			IContainer container = null;
			if (obj instanceof IJavaElement) {
				v.setJavaProject(((IJavaElement) obj).getJavaProject());
				if (v.getJavaProject() != null) {
					container = v.getJavaProject().getProject();
				}
			}
			if (obj instanceof IResource) {
				IResource res = (IResource) obj;
				if (obj instanceof IContainer) {
					container = (IContainer) res;
				} else {
					container = res.getParent();
				}
				if (res.getProject() != null) {
					IJavaProject project = JavaCore.create(res.getProject());
					if (project.exists()) {
						v.setJavaProject(project);
					}
				}
			}
			if (container != null && v.getJavaProject() == null) {
				IProject project = container.getProject();
				v.setJavaProject(JavaCore.create(project));
			}
			if (container != null) {
				container.accept(v, IResource.NONE);
				if (v.getJavaProject() == null) {
					IProject project = container.getProject();
					v.setJavaProject(JavaCore.create(project));
				}
				setProjAttribute(launchConfig, IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, v.getJavaProject());
				
				if (v.getJavaProject() != null) {
					ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
					String uniqName = lm.generateUniqueLaunchConfigurationNameFrom(v.getJavaProject().getElementName());
					launchConfig.rename(uniqName);							
				}
				setPathAttribute(launchConfig, IConsoleConfigurationLaunchConstants.PROPERTY_FILE, v.getPropertyFile());
				setPathAttribute(launchConfig, IConsoleConfigurationLaunchConstants.CFG_XML_FILE, v.getConfigFile());
				if (v.getPersistencexml() != null) {
					setStrAttribute(launchConfig, IConsoleConfigurationLaunchConstants.CONFIGURATION_FACTORY, ConfigurationMode.JPA.toString());
				}
				else {
					setStrAttribute(launchConfig, IConsoleConfigurationLaunchConstants.CONFIGURATION_FACTORY, ConfigurationMode.CORE.toString());
				}
				if (!v.getMappings().isEmpty() && v.getConfigFile() == null && v.getPersistencexml() == null) {
					IPath[] mappings = v.getMappings().toArray(new IPath[]{});
					List<String> l = new ArrayList<String>();
					for (int i = 0; i < mappings.length; i++) {
						IPath path = mappings[i];
						l.add(path.toPortableString());
					}
				
					launchConfig.setAttribute(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, l);
				}
				else {
					launchConfig.setAttribute(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, (List<String>)null);
				}
				if (!v.getClasspath().isEmpty()) {
					launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
					IPath[] custClasspath = v.getClasspath().toArray(new IPath[]{});
					List<String> mementos = new ArrayList<String>(custClasspath.length);
					for (int i = 0; i < custClasspath.length; i++) {
						mementos.add(custClasspath[i].toOSString());
					}
					launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, mementos);
				}
				else {
					launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true);
					launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, (List<String>)null);
				}
			} else if (obj instanceof EclipseConsoleConfiguration) {
				throw new IllegalStateException("This should never happen!"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Init path to cfg.xml for ConsoleConfigurationMainTab,
	 * init all tabs parameters
	 * 
	 * @param configFullPath full path to cfg.xml file
	 */
	public void setConfigurationFilePath(IPath configFullPath) {
		boolean flagFileWillBeCreated = false;
		try {
			IPath path = configFullPath;
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (resource == null) {
				flagFileWillBeCreated = true;
			}
			while (resource == null && path != null) {
				path = path.removeLastSegments(1);
				resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			}
			initialize(currentLaunchConfig, new StructuredSelection(resource));
		} catch (CoreException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage(
				HibernateConsoleMessages.ConsoleConfigurationWizardPage_problem_while_initializing_cc, e);
		}
		if (flagFileWillBeCreated) {
			setPathAttribute(currentLaunchConfig, IConsoleConfigurationLaunchConstants.CFG_XML_FILE, configFullPath);
			setStrAttribute(currentLaunchConfig, IConsoleConfigurationLaunchConstants.CONFIGURATION_FACTORY, ConfigurationMode.CORE.toString());
			ConsoleConfigurationMainTab ccmt = null;
			ILaunchConfigurationTab[] tabs = tabGroup.getTabs();
			for (int i = 0; i < tabs.length; i++) {
				if (tabs[i] instanceof ConsoleConfigurationMainTab) {
					ccmt = (ConsoleConfigurationMainTab)tabs[i];
					break;
				}
			}
			if (ccmt != null) {
				ccmt.markConfigurationFileWillBeCreated();
			}
		}
		performInit();
		dialogChanged();
	}


}
