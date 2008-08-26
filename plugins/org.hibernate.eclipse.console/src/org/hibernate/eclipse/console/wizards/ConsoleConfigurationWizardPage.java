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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.ui.wizards.BuildPathDialogAccess;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.EclipseConsoleConfiguration;
import org.hibernate.eclipse.console.EclipseConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.util.StringHelper;
import org.xml.sax.EntityResolver;


/**
 * @author max
 *
 *
 */
public class ConsoleConfigurationWizardPage extends WizardPage {

	private Text propertyFileText;
	private Text configurationFileText;
	private Text configurationNameText;
	private Text projectNameText;
	private Text persistenceUnitNameText;


	private EclipseConsoleConfiguration oldConfiguaration = null;
	Button coreMode;
	Button jpaMode;
	Button annotationsMode;

	private Text entityResolverClassNameText;
	private Text namingStrategyClassNameText;

	private ISelection selection;
	private UpDownListComposite mappingFilesViewer;
	private UpDownListComposite classPathViewer;
	private boolean configurationFileWillBeCreated;
	private Button confbutton;
	private Button entbutton;
	private Button useProjectClassPath;
	private Button nambutton;

	/**
	 * Constructor for SampleNewWizardPage.
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
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		TabFolder folder = new TabFolder(sc, SWT.TOP);
		sc.setContent(folder);

		//Composite container = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		//container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		GridData gd;

		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		};


		Composite general = createGeneral( folder, modifyListener );
		TabItem item = new TabItem(folder, SWT.NONE);
		item.setControl( general );
		item.setText( HibernateConsoleMessages.ConsoleConfigurationWizardPage_general );

		Composite composite = buildClassPathTable(folder);
		item = new TabItem(folder, SWT.NONE);
		item.setControl( composite );
		item.setText( HibernateConsoleMessages.ConsoleConfigurationWizardPage_classpath );

		composite = buildMappingFileTable(folder);
		item = new TabItem(folder, SWT.NONE);
		item.setControl( composite );
		item.setText( HibernateConsoleMessages.ConsoleConfigurationWizardPage_mappings );

		initialize(selection);
		dialogChanged();
		sc.setMinSize(folder.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

	private Composite createGeneral(Composite parent, ModifyListener modifyListener) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.verticalSpacing = 9;

		container.setLayout(gridLayout);
		Label label;
		Button button;
		GridData gd;
		label = new Label(container, SWT.NULL);
		label.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_name);

		configurationNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		configurationNameText.setLayoutData(gd);
		configurationNameText.addModifyListener(modifyListener);

		label = new Label(container, SWT.NULL);
		label.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_project);

		projectNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		projectNameText.setLayoutData(gd);
		projectNameText.addModifyListener(modifyListener);

		button = new Button(container, SWT.PUSH);
		button.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_browse);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleProjectBrowse();
			}
		});

		//label = new Label(container, SWT.NULL);
		createConfigurationMode( container );


		label = new Label(container, SWT.NULL);
		label.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_property_file_2);

		propertyFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		propertyFileText.setLayoutData(gd);
		propertyFileText.addModifyListener(modifyListener);


		button = new Button(container, SWT.PUSH);
		button.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_browse);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handlePropertyFileBrowse();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_config_file);

		configurationFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		configurationFileText.setLayoutData(gd);
		configurationFileText.addModifyListener(modifyListener);

		confbutton = new Button(container, SWT.PUSH);
		confbutton.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_browse);
		confbutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleConfigurationFileBrowse();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_persistence_unit);

		persistenceUnitNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		persistenceUnitNameText.setLayoutData(gd);
		persistenceUnitNameText.addModifyListener(modifyListener);

		label = new Label(container, SWT.NULL);
		label.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_naming_strategy);

		namingStrategyClassNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		namingStrategyClassNameText.setLayoutData(gd);
		namingStrategyClassNameText.addModifyListener(modifyListener);

		nambutton = new Button(container, SWT.PUSH);
		nambutton.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_browse);
		nambutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleNamingStrategyBrowse();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_entity_resolver);

		entityResolverClassNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		entityResolverClassNameText.setLayoutData(gd);
		entityResolverClassNameText.addModifyListener(modifyListener);

		entbutton = new Button(container, SWT.PUSH);
		entbutton.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_browse);
		entbutton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleEntityResolverBrowse();
			}
		});


		return container;
	}


	private void createConfigurationMode(Composite container) {
		SelectionListener sl = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialogChanged();
			}
		};
		new Label(container, SWT.NULL).setText( HibernateConsoleMessages.ConsoleConfigurationWizardPage_type );
		Group group = new Group( container, SWT.SHADOW_IN);
		//group.setText("Choose Hibernate configuration");
		group.setLayout( new RowLayout( SWT.HORIZONTAL ) );
		coreMode = new Button(group, SWT.RADIO);
		coreMode.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_core);
		coreMode.addSelectionListener( sl );
		coreMode.setSelection( true );
		annotationsMode = new Button(group, SWT.RADIO);
		annotationsMode.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_annotations);
		annotationsMode.addSelectionListener( sl );
		jpaMode = new Button(group, SWT.RADIO);
		jpaMode.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_jpa);
		jpaMode.addSelectionListener( sl );
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData( gd );
	}

	protected void handleEntityResolverBrowse() {
		String string = DialogSelectionHelper.chooseImplementation(EntityResolver.class.getName(), entityResolverClassNameText.getText(), HibernateConsoleMessages.ConsoleConfigurationWizardPage_select_entity_resolver_class, getShell());
		if(string!=null) {
			entityResolverClassNameText.setText(string);
		}
	}

	protected void handleNamingStrategyBrowse() {
		String string = DialogSelectionHelper.chooseImplementation(NamingStrategy.class.getName(), namingStrategyClassNameText.getText(), HibernateConsoleMessages.ConsoleConfigurationWizardPage_select_naming_strategy_class, getShell());
		if(string!=null) {
			namingStrategyClassNameText.setText(string);
		}
	}


	private Composite buildClassPathTable(Composite parent) {
		Composite c = new Composite(parent, SWT.None);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 9;
		c.setLayout( gridLayout );

		classPathViewer = new UpDownListComposite(c, SWT.NONE, HibernateConsoleMessages.ConsoleConfigurationWizardPage_additional_classpath) {
			protected Object[] handleAdd(int idx) {

				TableItem[] items = getTable().getItems();
				IPath[] exclude = new IPath[items.length];

				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					exclude[i] = (IPath) item.getData();
				}

				switch (idx) {
				case 0:
					return DialogSelectionHelper.chooseFileEntries(getShell(), null, exclude, HibernateConsoleMessages.ConsoleConfigurationWizardPage_add_classpath_entry, HibernateConsoleMessages.ConsoleConfigurationWizardPage_add_directory_zip_jar, new String[] { HibernateConsoleMessages.ConsoleConfigurationWizardPage_27, HibernateConsoleMessages.ConsoleConfigurationWizardPage_28 }, true, true, true);
				case 1:
					return BuildPathDialogAccess.chooseExternalJAREntries(getShell() );
				default:
					return null;
				}

			}

			protected String[] getAddButtonLabels() {
				return new String[] { HibernateConsoleMessages.ConsoleConfigurationWizardPage_add_jar_dir, HibernateConsoleMessages.ConsoleConfigurationWizardPage_add_external_jars };
			}
			protected void listChanged() {
				dialogChanged();
			}

		};
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalAlignment = GridData.FILL;
		classPathViewer.setLayoutData( gd );

		useProjectClassPath = new Button(c, SWT.CHECK);
		useProjectClassPath.setSelection( true );
		useProjectClassPath.setText(HibernateConsoleMessages.ConsoleConfigurationWizardPage_include_default_classpath);
		useProjectClassPath.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				dialogChanged();
			}

			public void widgetSelected(SelectionEvent e) {
				dialogChanged();
			}
		});


		return c;
	}

	private UpDownListComposite buildMappingFileTable(Composite parent) {
		mappingFilesViewer = new UpDownListComposite(parent, SWT.NONE, HibernateConsoleMessages.ConsoleConfigurationWizardPage_additional_mapping_files) {
			protected Object[] handleAdd(int idx) {
				TableItem[] items = getTable().getItems();
				IPath[] exclude = new IPath[items.length];

				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					exclude[i] = (IPath) item.getData();
				}

				return DialogSelectionHelper.chooseFileEntries(getShell(), null, exclude, HibernateConsoleMessages.ConsoleConfigurationWizardPage_add_hbmxml_file, HibernateConsoleMessages.ConsoleConfigurationWizardPage_add_hibernate_mapping_file, new String[] { HibernateConsoleMessages.ConsoleConfigurationWizardPage_35 }, true, false, true);
			}

			protected void listChanged() {
				dialogChanged();
			}
		};

		GridData gd;
		gd = new GridData(GridData.FILL_BOTH);

		gd.horizontalSpan = 3;
		gd.verticalSpan = 1;

		mappingFilesViewer.setLayoutData( gd );
		return mappingFilesViewer;
	}



	/**
	 * A visitor class that will make a "best guess" on which files the
	 * user want for the properties and config file.
	 *
	 * @author max
	 *
	 */
	static class Visitor implements IResourceProxyVisitor {

		public IPath   propertyFile;
		public IPath   configFile;
		public IPath   persistencexml;
		public IJavaProject javaProject;
		public List    classpath = new ArrayList();
		public List    mappings = new ArrayList();

		public boolean visit(IResourceProxy proxy) throws CoreException {
			//System.out.println("visit: " + proxy.getName() );
			IPath fullPath = proxy.requestFullPath();
			if(proxy.getType() == IResource.FILE) {
				if("hibernate.properties".equals(proxy.getName() ) ) { //$NON-NLS-1$
					propertyFile = fullPath;
					return false;
				}

				if("hibernate.cfg.xml".equals(proxy.getName() ) ) { //$NON-NLS-1$
					configFile = fullPath;
					mappings.clear(); // we prefer af cfg.xml over mappings
					return false;
				}

				if("persistence.xml".equals( proxy.getName() )) { //$NON-NLS-1$
					if(javaProject!=null && javaProject.isOnClasspath( proxy.requestResource() )) {
						persistencexml = fullPath;
						mappings.clear();
						return false;
					}
				}

				// only add mappings if we don't have a config file.
				if((configFile==null || persistencexml==null) && proxy.getName().endsWith(".hbm.xml") ) { //$NON-NLS-1$
					mappings.add(fullPath);
					return false;
				}
			} else if(proxy.getType() == IResource.FOLDER) {
				if(javaProject!=null) {
					if(javaProject.getOutputLocation().isPrefixOf(fullPath) ) {
						//classpath.add(fullPath);
						return false; // skip output locations
					}
				}
			}
			return true;
		}
	}



	public void initialize(ISelection currentSelection) {
		try {
			Visitor v = new Visitor();
		// use selection to build configuration from it...
		if (currentSelection!=null && currentSelection.isEmpty()==false && currentSelection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection)currentSelection;
			if (ssel.size()>1) return;
			Object obj = ssel.getFirstElement();

			IContainer container = null;
			if (obj instanceof IJavaElement) {
				v.javaProject = ((IJavaElement) obj).getJavaProject();
				if(v.javaProject!=null) {
					container = v.javaProject.getProject();
				}
			}
			if (obj instanceof IResource) {
				IResource res = (IResource) obj;
				if (obj instanceof IContainer) {
					container = (IContainer)res;
				} else {
					container = res.getParent();
				}

				if(res.getProject()!=null) {
					IJavaProject project = JavaCore.create(res.getProject());
					if(project.exists()) {
						v.javaProject = project;
					}
				}
			}

			if(container!=null) {
				container.accept(v, IResource.NONE);

                if(v.javaProject==null) {
                    IProject project = container.getProject();
                    v.javaProject = JavaCore.create(project);
                }

				if(v.javaProject!=null) {
					ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
					String uniqName = lm.generateUniqueLaunchConfigurationNameFrom(v.javaProject.getElementName());
					configurationNameText.setText(uniqName);
					projectNameText.setText(v.javaProject.getElementName());
				}
				if (v.propertyFile!=null) {
					propertyFileText.setText(v.propertyFile.toOSString() );
				}

				if (v.configFile!=null) {
					configurationFileText.setText(v.configFile.toOSString() );
				}

				if (v.persistencexml!=null) {
					jpaMode.setSelection( true );
					coreMode.setSelection( false );
					annotationsMode.setSelection( false );
				}
				if (!v.mappings.isEmpty() && v.configFile==null && v.persistencexml==null) mappingFilesViewer.add(v.mappings.toArray(), false);
				if (!v.classpath.isEmpty() ) classPathViewer.add(v.classpath.toArray(), false);
				useProjectClassPath.setSelection( true );
                //if(v.javaProject!=null) {
					//classPathViewer.add(locateTypes(v.javaProject).toArray(), false);
				//}
			} else if (obj instanceof EclipseConsoleConfiguration) {
				// trying to edit an EXISTING consoleconfiguration
				EclipseConsoleConfiguration cc = (EclipseConsoleConfiguration) obj;
				EclipseConsoleConfigurationPreferences prefs = (EclipseConsoleConfigurationPreferences) cc.getPreferences();

				configurationNameText.setText(prefs.getName() );
				if(prefs.getProjectName()!=null) projectNameText.setText( prefs.getProjectName() );
				useProjectClassPath.setSelection( prefs.useProjectClasspath() );
				if(prefs.getPropertyFilename()!=null) propertyFileText.setText(prefs.getPropertyFilename().toOSString() );
				if(prefs.getCfgFile()!=null) configurationFileText.setText(prefs.getCfgFile().toOSString() );
				if(prefs.getMappings()!=null) mappingFilesViewer.add(prefs.getMappings(),false);
				if(prefs.getCustomClasspath()!=null) classPathViewer.add(prefs.getCustomClasspath(),false);
				if(prefs.getEntityResolverName()!=null) entityResolverClassNameText.setText(prefs.getEntityResolverName());
				if(prefs.getNamingStrategy() !=null) namingStrategyClassNameText.setText(prefs.getNamingStrategy());
				if(prefs.getPersistenceUnitName()!=null) persistenceUnitNameText.setText( prefs.getPersistenceUnitName() );
				jpaMode.setSelection( prefs.getConfigurationMode().equals( ConfigurationMode.JPA ) );
				coreMode.setSelection( prefs.getConfigurationMode().equals( ConfigurationMode.CORE ) );
				annotationsMode.setSelection( prefs.getConfigurationMode().equals( ConfigurationMode.ANNOTATIONS ) );

				oldConfiguaration = cc;
			}




		}
		} catch (CoreException ce) {
			HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.ConsoleConfigurationWizardPage_problem_while_initializing_cc, ce);
		}

	}

	List locateTypes(final IJavaProject javaProject) {

		try {
			String typeName = "java.sql.Driver"; //$NON-NLS-1$
            final SearchPattern pattern = SearchPattern.createPattern(typeName, IJavaSearchConstants.TYPE, IJavaSearchConstants.IMPLEMENTORS, SearchPattern.R_EXACT_MATCH);
			final IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] {javaProject });

			final SearchEngine engine = new SearchEngine();

			final CollectingSearchRequestor sr = new CollectingSearchRequestor();
			final SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};

			final ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell() );


			dialog.run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					try {
						engine.search(pattern, participants, scope, sr, monitor);
					} catch (CoreException ce) {
						HibernateConsolePlugin.getDefault().logErrorMessage(
								HibernateConsoleMessages.ConsoleConfigurationWizardPage_problem_while_locating_jdbc_drivers, ce);
					}
				}
			});


			List resources = new ArrayList();
			Iterator iter = sr.getResults().iterator();
			while (iter.hasNext() ) {
				SearchMatch match = (SearchMatch) iter.next();
				if(match.getResource() instanceof IFile) { // what happens if a actual class implements java.sql.driver ?
					resources.add(match.getResource().getFullPath() );
				}
			}

			return resources;
		} catch (InvocationTargetException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage(
					HibernateConsoleMessages.ConsoleConfigurationWizardPage_problem_while_locating_jdbc_drivers, e);
			} catch (InterruptedException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(
						HibernateConsoleMessages.ConsoleConfigurationWizardPage_problem_while_locating_jdbc_drivers, e);
		}


		return Collections.EMPTY_LIST;
	}

	IPath[] getMappingFiles() {
		return tableItems2File(mappingFilesViewer.getTable() );
	}

	IPath[] getClassPath() {
		return tableItems2File(classPathViewer.getTable() );
	}

	private IPath[] tableItems2File(Table table) {
		TableItem[] items = table.getItems();
		IPath[] str = new IPath[items.length];
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			IPath path = (IPath) item.getData();
			str[i] = path;
		}
		return str;
	}

	private void handlePropertyFileBrowse() {
		IPath[] paths = org.hibernate.eclipse.console.utils.xpl.DialogSelectionHelper.chooseFileEntries(getShell(),  getPropertyFilePath(), new IPath[0], HibernateConsoleMessages.ConsoleConfigurationWizardPage_select_property_file, HibernateConsoleMessages.ConsoleConfigurationWizardPage_choose_file_to_use, new String[] {"properties"}, false, false, true); //$NON-NLS-1$
		if(paths!=null && paths.length==1) {
			propertyFileText.setText( (paths[0]).toOSString() );
		}
	}

	private void handleProjectBrowse() {
		IJavaProject paths = DialogSelectionHelper.chooseJavaProject( getShell(), ProjectUtils.findJavaProject( getProjectName() ), HibernateConsoleMessages.ConsoleConfigurationWizardPage_select_java_project, HibernateConsoleMessages.ConsoleConfigurationWizardPage_select_java_project_to_classpath );
		if(paths!=null) {
			projectNameText.setText( paths.getProject().getName() );
		} else {
			projectNameText.setText(""); //$NON-NLS-1$
		}
	}


	private void handleConfigurationFileBrowse() {
		IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(),  getConfigurationFilePath(), new IPath[0], HibernateConsoleMessages.ConsoleConfigurationWizardPage_select_hibcfgxml_file, HibernateConsoleMessages.ConsoleConfigurationWizardPage_choose_file_to_use_hibcfgxml_file, new String[] {"cfg.xml"}, false, false, true); //$NON-NLS-1$
		if(paths!=null && paths.length==1) {
			configurationFileText.setText( (paths[0]).toOSString() );
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		String propertyFilename = propertyFileText.getText();
		String configurationFilename = configurationFileText.getText();
		setMessage(null);

		configurationFileText.setEnabled( !configurationFileWillBeCreated && !getConfigurationMode().equals( ConfigurationMode.JPA ) );
		confbutton.setEnabled( !getConfigurationMode().equals( ConfigurationMode.JPA ) );

		persistenceUnitNameText.setEnabled( getConfigurationMode().equals( ConfigurationMode.JPA) );

		String error = verifyConfigurationName();
		if (error != null){
			updateStatus(error);
			return;
		}

		if(getProjectName()!=null && StringHelper.isNotEmpty(getProjectName().trim())) {
			IJavaProject findJavaProject = ProjectUtils.findJavaProject( getProjectName() );
			if(findJavaProject==null || !findJavaProject.exists()) {
				String out = NLS.bind(HibernateConsoleMessages.ConsoleConfigurationWizardPage_the_java_project_does_not_exist, getProjectName());
				updateStatus(out);
				return;
			}
		}

		/* TODO: warn about implicit behavior of loading /hibernate.cfg.xml, /hibernate.properties and /META-INF/persistence.xml
		 * if (propertyFilename.length() == 0 && configurationFilename.trim().length() == 0) {
			updateStatus("Property or Configuration file must be specified");
			return;
		} */

		if (propertyFilename.length() > 0) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(propertyFilename);
			String msg = checkForFile(HibernateConsoleMessages.ConsoleConfigurationWizardPage_property_file_1, resource);
			if(msg!=null) {
				updateStatus(msg);
				return;
			}
		}

		if (!configurationFileWillBeCreated && configurationFilename.length() > 0) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(configurationFilename);
			String msg = checkForFile(HibernateConsoleMessages.ConsoleConfigurationWizardPage_configuration_file,resource);
			if(msg!=null) {
				updateStatus(msg);
				return;
			}
		} else if(mappingFilesViewer.getTable().getItemCount()==0) {
			//TODO: removed to provide a way to create a non-mapping base configuration
			//updateStatus("Need to specify one or more mapping files");
			//return;
		}

		if((useProjectClassPath() && StringHelper.isEmpty( getProjectName() )) && classPathViewer.getTable().getItemCount()==0) {
			updateStatus( HibernateConsoleMessages.ConsoleConfigurationWizardPage_need_to_specify_project );
			return;
		}

		if((!useProjectClassPath() && classPathViewer.getTable().getItemCount()==0)) {
			updateStatus( HibernateConsoleMessages.ConsoleConfigurationWizardPage_need_to_specify_classpath );
			return;
		}
		
		if ((configurationFilename != null && configurationFilename.trim().length() > 0) &&
				(propertyFilename != null && propertyFilename.trim().length() > 0)) {
			setMessage(HibernateConsoleMessages.ConsoleConfigurationWizardPage_both_hibernate_properties_and_hibernate_cfg_xml, WARNING);
			return;
		}

		updateStatus(null);
	}
	
	private String verifyConfigurationName(){
		String currentName = getConfigurationName().trim();

		if (currentName == null || currentName.length() < 1) {
			return HibernateConsoleMessages.ConsoleConfigurationWizardPage_name_must_specified;
		}

		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		try {
			if (lm.isExistingLaunchConfigurationName(currentName)) {
				return HibernateConsoleMessages.ConsoleConfigurationWizardPage_config_name_already_exist;
			}
		} catch (CoreException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage(e.getMessage(), e);
		}

		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			String[] badnames = new String[] { "aux", "clock$", "com1", "com2", "com3", "com4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ 
					"com5", "com6", "com7", "com8", "com9", "con", "lpt1", "lpt2", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
					"lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9", "nul", "prn" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			for (int i = 0; i < badnames.length; i++) {
				if (currentName.equals(badnames[i])) {
					return NLS.bind(HibernateConsoleMessages.ConsoleConfigurationWizardPage_bad_name, currentName);
				}
			}
		}
		// See if name contains any characters that we deem illegal.
		// '@' and '&' are disallowed because they corrupt menu items.
		char[] disallowedChars = new char[] { '@', '&', '\\', '/', ':', '*', '?', '"', '<', '>', '|', '\0' };
		for (int i = 0; i < disallowedChars.length; i++) {
			char c = disallowedChars[i];
			if (currentName.indexOf(c) > -1) {
				return NLS.bind(HibernateConsoleMessages.ConsoleConfigurationWizardPage_bad_char, c);
			}
		}

		return null;
	}

	String getProjectName() {
		return projectNameText.getText();
	}

	String getConfigurationName() {
		return configurationNameText.getText();
	}

	private String checkForFile(String msgPrefix, IResource resource) {
		if(resource!=null) {
			if(resource instanceof IFile) {

				return null;
			} else {
				return msgPrefix + HibernateConsoleMessages.ConsoleConfigurationWizardPage_is_not_a_file;
			}
		} else {
			return msgPrefix + HibernateConsoleMessages.ConsoleConfigurationWizardPage_does_not_exist;
		}
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public Path getPropertyFilePath() {
		String p = propertyFileText.getText();
		return pathOrNull(p);
	}
	private Path pathOrNull(String p) {
		if(p==null || p.trim().length()==0) {
			return null;
		} else {
			return new Path(p);
		}
	}

	public Path getConfigurationFilePath() {
		return pathOrNull(configurationFileText.getText() );
	}

	/**
	 * @return
	 */
	public EclipseConsoleConfiguration getOldConfiguration() {
		return oldConfiguaration;
	}

	public void setConfigurationFilePath(IPath containerFullPath) {
		if(!configurationFileWillBeCreated) {
			initialize( new StructuredSelection(containerFullPath) );
		}
		configurationFileText.setText(containerFullPath.toPortableString());

		configurationFileWillBeCreated = true;
		configurationFileText.setEnabled(false);
		confbutton.setEnabled(false);
	}

	public String getEntityResolverClassName() {
		return entityResolverClassNameText.getText();
	}

	public boolean useProjectClassPath() {
		return useProjectClassPath.getSelection();
	}

	public ConfigurationMode getConfigurationMode() {
		if(annotationsMode.getSelection()) {
			return ConfigurationMode.ANNOTATIONS;
		} else if(jpaMode.getSelection()) {
			return ConfigurationMode.JPA;
		} else {
			return ConfigurationMode.CORE;
		}
	}

	public String getNamingStrategy() {
		return namingStrategyClassNameText.getText();
	}

	public String getPersistenceUnitName() {
		return persistenceUnitNameText.getText();
	}



}


