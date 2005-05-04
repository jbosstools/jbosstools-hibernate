/*
 * Created on 2004-10-13
 *
 * 
 */
package org.hibernate.eclipse.console.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.EclipseConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;

/**
 * @author max
 *
 * 
 */
public class ConsoleConfigurationWizardPage extends WizardPage {
	
	private Text propertyFileText;
	private Text configurationFileText;
	private Text configurationNameText;
	private EclipseConsoleConfiguration oldConfiguaration = null;
	
	private ISelection selection;
	private UpDownList mappingFilesViewer;
	private UpDownList classPathViewer;
	
	/**
	 * Constructor for SampleNewWizardPage.
	 * @param pageName
	 */
	public ConsoleConfigurationWizardPage(ISelection selection) {
		super("configurationPage");
		setTitle("Create Hibernate Console Configuration");
		setDescription("This wizard allows you to create a configuration for Hibernate Console.");
		this.selection = selection;		
	}
	
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		Label label;
		Button button;
		GridData gd;
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		};
		
		label = new Label(container, SWT.NULL);
		label.setText("&Name:");
		
		configurationNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		configurationNameText.setLayoutData(gd);		
		configurationNameText.addModifyListener(modifyListener);
		
		label = new Label(container, SWT.NULL);
		label.setText("&Property file:");
		
		propertyFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		propertyFileText.setLayoutData(gd);
		propertyFileText.addModifyListener(modifyListener);
		
		
		button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handlePropertyFileBrowse();
			}
		});
		
		label = new Label(container, SWT.NULL);
		label.setText("&Configuration file:");
		
		configurationFileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		configurationFileText.setLayoutData(gd);
		configurationFileText.addModifyListener(modifyListener);
		
		button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleConfigurationFileBrowse();
			}
		});
		
		buildMappingFileTable(container);
		buildClassPathTable(container);
		initialize();
		dialogChanged();
		setControl(container);
	}
	
	
	private void buildClassPathTable(Composite parent) {
		classPathViewer = new UpDownList(parent, getShell(), "Classpath (only add path for POJO and driver - No Hibernate jars!)") {
			protected Object[] handleAdd(int idx) {

				TableItem[] items = getTable().getItems();
				IPath[] exclude = new IPath[items.length];
				
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					exclude[i] = (IPath) item.getData();			
				}
				
				switch (idx) {
				case 0:
					return DialogSelectionHelper.chooseFileEntries(getShell(), null, exclude, "Add classpath entry", "Add a directory, .zip or .jar file", new String[] { ".jar", ".zip" }, true, true, true);					
				case 1:
					return BuildPathDialogAccess.chooseExternalJAREntries(getShell());
				default:
					return null;
				}
				
			}

			protected String[] getAddButtonLabels() {
				return new String[] { "Add JARS...", "Add External JARS..." };				
			}
			protected void listChanged() {
				dialogChanged();
			}
			
		};
		
	}

	private void buildMappingFileTable(Composite parent) {
		mappingFilesViewer = new UpDownList(parent, getShell(), "Mapping files") {
			protected Object[] handleAdd(int idx) {
				TableItem[] items = getTable().getItems();
				IPath[] exclude = new IPath[items.length];
				
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					exclude[i] = (IPath) item.getData();			
				}
				
				return DialogSelectionHelper.chooseFileEntries(getShell(), null, exclude, "Add hbm.xml file", "Add a Hibernate Mapping file", new String[] { "hbm.xml" }, true, false, true);
			}

			protected void listChanged() {
				dialogChanged();
			}
		};
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
		public IJavaProject javaProject;
		public List    classpath = new ArrayList();
		public List    mappings = new ArrayList();
		
		public boolean visit(IResourceProxy proxy) throws CoreException {
			//System.out.println("visit: " + proxy.getName());
			IPath fullPath = proxy.requestFullPath();
			if(proxy.getType() == IResource.FILE) {
				if("hibernate.properties".equals(proxy.getName())) {
					propertyFile = fullPath;
					return false;
				}
				
				if("hibernate.cfg.xml".equals(proxy.getName())) {
					configFile = fullPath;
					mappings.clear(); // we prefer af cfg.xml over mappings
					return false;
				}				
				
				// only add mappings if we don't have a config file.
				if(configFile==null && proxy.getName().endsWith(".hbm.xml")) {
					mappings.add(fullPath);
					return false;
				}
			} else if(proxy.getType() == IResource.FOLDER) {
				if(javaProject!=null) {
					if(javaProject.getOutputLocation().isPrefixOf(fullPath)) {
						classpath.add(fullPath);
						//System.out.println("SKIPPING " + proxy.getName());
						return false; // skip output locations
					}
				}
			}
			return true;
		}
	}


	/**
	 * Tests if the current workbench selection is a suitable
	 * container to use.
	 * @throws 
	 */
	
	private void initialize() {
		try {
			Visitor v = new Visitor();
		// use selection to build configuration from it...
		if (selection!=null && selection.isEmpty()==false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection)selection;
			if (ssel.size()>1) return;
			Object obj = ssel.getFirstElement();
			
			IContainer container = null;
			if (obj instanceof IJavaProject) {
				v.javaProject = (IJavaProject) obj;
				container = ((IJavaProject)obj).getProject();
			} else if (obj instanceof IResource) {
				if (obj instanceof IContainer)
					container = (IContainer)obj;
				else
					container = ((IResource)obj).getParent();
			}
			
			if(container!=null) {
				container.accept(v, IResource.NONE);
				
                if(v.javaProject==null) {
                    IProject project = container.getProject();
                    v.javaProject = JavaCore.create(project);
                }
                
				if(v.javaProject!=null) configurationNameText.setText(v.javaProject.getElementName());
				if (v.propertyFile!=null) propertyFileText.setText(v.propertyFile.toOSString());
				if (v.configFile!=null) configurationFileText.setText(v.configFile.toOSString());
				if (!v.mappings.isEmpty()) mappingFilesViewer.add(v.mappings.toArray(), false);
				if (!v.classpath.isEmpty()) classPathViewer.add(v.classpath.toArray(), false);

                
				if(v.javaProject!=null) {
					classPathViewer.add(locateTypes(v.javaProject).toArray(), false);				
				}
			} else if (obj instanceof EclipseConsoleConfiguration) {
				// trying to edit an EXISTING consoleconfiguration
				EclipseConsoleConfiguration cc = (EclipseConsoleConfiguration) obj;
				EclipseConsoleConfigurationPreferences prefs = (EclipseConsoleConfigurationPreferences) cc.getPreferences();
				
				configurationNameText.setText(prefs.getName());
				if(prefs.getPropertyFilename()!=null) propertyFileText.setText(prefs.getPropertyFilename().toOSString());
				if(prefs.getCfgFile()!=null) configurationFileText.setText(prefs.getCfgFile().toOSString());
				if(prefs.getMappings()!=null) mappingFilesViewer.add(prefs.getMappings(),false);
				if(prefs.getCustomClasspath()!=null) classPathViewer.add(prefs.getCustomClasspath(),false);
				
				oldConfiguaration = cc;
			}
			
			
			
			
		}
		} catch (CoreException ce) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Problem while initializing ConsoleConfigurationWizardPage", ce);
		}
		
	}

	List locateTypes(final IJavaProject javaProject) {
	    
		try {
			String typeName = "java.sql.Driver";
            final SearchPattern pattern = SearchPattern.createPattern(typeName, IJavaSearchConstants.TYPE, IJavaSearchConstants.IMPLEMENTORS, SearchPattern.R_EXACT_MATCH);
			final IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] {javaProject });
			
			final SearchEngine engine = new SearchEngine();
			
			final CollectingSearchRequestor sr = new CollectingSearchRequestor();
			final SearchParticipant[] participants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
			
			final ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
			
			
			dialog.run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					try {
						engine.search(pattern, participants, scope, sr, monitor);
					} catch (CoreException ce) {
						HibernateConsolePlugin.getDefault().logErrorMessage(
								"Problem while locating jdbc drivers", ce);									
					}
				}
			});
			
			
			List resources = new ArrayList();
			Iterator iter = sr.getResults().iterator();
			while (iter.hasNext()) {
				SearchMatch match = (SearchMatch) iter.next();
				if(match.getResource() instanceof File) { // what happens if a actual class implements java.sql.driver ?
					resources.add(match.getResource().getFullPath());
				}
			}
			
			return resources;
		} catch (InvocationTargetException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage(
					"Problem while locating jdbc drivers", e);
			} catch (InterruptedException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(
						"Problem while locating jdbc drivers", e);
		}
		
		
		return Collections.EMPTY_LIST;
	}
	
	IPath[] getMappingFiles() {
		return tableItems2File(mappingFilesViewer.getTable());
	}

	IPath[] getClassPath() {
		return tableItems2File(classPathViewer.getTable());
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
		IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(),  getPropertyFilePath(), new IPath[0], "Select property file", "Choose file to use as hibernate.properties", new String[] {"properties"}, false, false, true);
		if(paths!=null && paths.length==1) {
			propertyFileText.setText((paths[0]).toOSString());
		}
	}
	
	private void handleConfigurationFileBrowse() {
		IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(),  getConfigurationFilePath(), new IPath[0], "Select hibernate.cfg.xml file", "Choose file to use as hibernate.cfg.xml", new String[] {"cfg.xml"}, false, false, true);
		if(paths!=null && paths.length==1) {
			configurationFileText.setText((paths[0]).toOSString());
		}
	}
	
	/**
	 * Ensures that both text fields are set.
	 */
	
	private void dialogChanged() {
		String propertyFilename = propertyFileText.getText();
		String configurationFilename = configurationFileText.getText();
		
		
		if(getConfigurationName()==null || getConfigurationName().trim().length() == 0) {
			updateStatus("A name must be specificed");
			return;
		} else {
			if(oldConfiguaration==null && KnownConfigurations.getInstance().find(getConfigurationName())!=null) {
				updateStatus("A configuration with that name already exists!");
				return;
			}
		}
		
		if (propertyFilename.length() == 0 && configurationFilename.trim().length() == 0) {
			updateStatus("Property or Configuration file must be specified");
			return;
		} 
		
		if (propertyFilename.length() > 0) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(propertyFilename);
			String msg = checkForFile("Property file", resource);
			if(msg!=null) {
				updateStatus(msg);
				return;
			}
		}
		
		if (configurationFilename.length() > 0) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(configurationFilename);
			String msg = checkForFile("Configuration file",resource);
			if(msg!=null) {
				updateStatus(msg);
				return;
			}
		} else if(mappingFilesViewer.getTable().getItemCount()==0) {
			//TODO: removed to provide a way to create a non-mapping base configuration
			//updateStatus("Need to specify one or more mapping files");
			//return;
		}
		
		updateStatus(null);
	}

	String getConfigurationName() {
		return configurationNameText.getText();
	}

	private String checkForFile(String msgPrefix, IResource resource) {
		if(resource!=null) {
			if(resource instanceof File) {
				
				return null;
			} else {
				return msgPrefix + " is not a file";
			}				
		} else {
			return msgPrefix + " does not exist";
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
		return pathOrNull(configurationFileText.getText());
	}

	/**
	 * @return
	 */
	public EclipseConsoleConfiguration getOldConfiguration() {
		return oldConfiguaration;
	}

}


