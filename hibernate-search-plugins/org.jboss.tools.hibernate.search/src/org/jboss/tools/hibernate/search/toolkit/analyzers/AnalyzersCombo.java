package org.jboss.tools.hibernate.search.toolkit.analyzers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.jboss.tools.hibernate.search.HibernateSearchConsolePlugin;

public class AnalyzersCombo {
	
	protected Combo comboControl;
	public static final String DEFAULT_ANALYZER = "org.apache.lucene.analysis.standard.StandardAnalyzer";

	private String consoleConfigName;

	public AnalyzersCombo(Composite parent, GridData layout, String consoleConfigName) {
		this.consoleConfigName = consoleConfigName;
		this.comboControl = new Combo(parent, SWT.READ_ONLY);
		this.comboControl.setLayoutData(layout);
		populateComboBox();
	}

	protected void populateComboBox() {
		String projName = null;
		try {
			ILaunchConfiguration launchConfiguration = LaunchHelper.findHibernateLaunchConfig(this.consoleConfigName);
			projName = launchConfiguration.getAttribute(IConsoleConfigurationLaunchConstants.PROJECT_NAME, ""); //$NON-NLS-1$
		} catch (CoreException e) {
			HibernateSearchConsolePlugin.getDefault().log(e);
		}
		IJavaProject project = ProjectUtils.findJavaProject(projName);
		final IType analyzersType = ProjectUtils.findType(project, "org.apache.lucene.analysis.Analyzer");

		comboControl.getDisplay().syncExec(new Runnable() {

			public void run() {
				try {
					IType[] types = analyzersType.newTypeHierarchy(new NullProgressMonitor()).getAllSubtypes(analyzersType);
					List<String> typesList = new LinkedList<String>();
					for (IType type : types) {
						try {
							if (type.getMethod(type.getElementName(), new String[0]).isConstructor()) {
								typesList.add(type.getFullyQualifiedName());
								continue;
							}
							
						} catch (JavaModelException e) {
						}
						
						try {
							if (type.getMethod(type.getElementName(), new String[] {"Lorg.apache.lucene.util.Version;"}).isConstructor()) {
								typesList.add(type.getFullyQualifiedName());
								continue;
							}
						} catch (JavaModelException e) {
						}
						
					}
					comboControl.setItems(typesList.toArray(new String[0]));		
					comboControl.setText(DEFAULT_ANALYZER);
				} catch (JavaModelException e) {
					HibernateConsolePlugin.getDefault().log(e);
				}
			}
		});
	}

	public String getAnalyzer() {
		return comboControl.getText();
	}
}
