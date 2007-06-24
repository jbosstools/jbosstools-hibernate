/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.wizard.generateDDL;


import java.util.Properties;
import java.util.ResourceBundle;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.wizard.classloader.DefaultClassLoaderFactory;


public class GenerateDDLPage1 extends WizardPage {
	public static final String BUNDLE_NAME = "generate";
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(GenerateDDLPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping ormmodel;
    private Button browseuBtton,isDrop;
	public boolean check,isAdditionalMappings;
	private Combo dialectText;
	private Text  delimiterText,inputfileName;
	private IHibernateDDLGenerator generator;
	private Properties properties=new Properties();
	private IProject project;
	private String [] dialects ;
	private String strPath;
	private String dialect;
	private IFile file;
	private IProject projectFromWSpace;
	private  IPersistentClassMapping[] classes;
	private IType itype;
	
	Exception ee= new UnsupportedOperationException("Cant work with null project");;
	/**
	 * constructor of the GenerateDDLPage1
	 * @param ormmodel
	 */
	public GenerateDDLPage1(IMapping ormmodel){
		super("wizardPage");
		setTitle(BUNDLE.getString("GenerateDDLPage1.Title"));
		setDescription(BUNDLE.getString("GenerateDDLPage1.Description"));
		this.ormmodel = ormmodel;
		initGenerator();
		
	}
    
	/**
	 * createControl() of the wizard
	 * @param parent
	 */
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing=10;
		layout.verticalSpacing=10;
		layout.marginHeight=15;
		container.setLayout(layout);
		
		Label labelDialect = new Label(container, SWT.NULL);		
		labelDialect.setText(BUNDLE.getString("GenerateDDLPage1.label"));
		dialectText = new Combo(container, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL );
		data.horizontalSpan = 2;
		dialectText.setLayoutData(data);
		dialects=generator.getDialects();
		dialectText.setItems(dialects);
	   // dialect=ormmodel.getConfiguration().getProperty(BUNDLE.getString("GenerateDDLPage1.property_hibernate.dialect"));
		if(dialect!=null){
			dialectText.setText(dialect.substring(dialect.indexOf("dialect")+8,dialect.lastIndexOf("Dialect")));
			setDialect();
		}
		dialectText.addSelectionListener(new SelectionListener()
				{
			public void widgetSelected(SelectionEvent e) {
		
				setDialect();
				getWizard().getContainer().updateButtons();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				getWizard().getContainer().updateButtons();
			}}
		);
		
		
		Label labelDelimetr = new Label(container, SWT.NULL);
		labelDelimetr.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false, 1, 1));
		labelDelimetr.setText(BUNDLE.getString("GenerateDDLPage1.Delimetr"));
		
		Label labelHelp = new Label(container, SWT.NULL);
		labelHelp.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false, 2, 1));
		labelHelp.setText(BUNDLE.getString("GenerateDDLPage1.Help"));
		
		
		Label empty1 = new Label(container, SWT.NULL);
		empty1.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false, 1, 1));
		
		delimiterText = new Text(container, SWT.BORDER | SWT.SINGLE);
		delimiterText.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false, 2, 1));
		delimiterText.addModifyListener(new ModifyListener()
					{
			public void modifyText(ModifyEvent e) {
				Text text = (Text) e.widget;
				setDelimeter(text.getText());
			}
		}); 

		Label empty = new Label(container, SWT.NULL);
		empty.setLayoutData(new GridData(SWT.BEGINNING, SWT.END, false, false, 1, 1));
		isDrop= new Button(container, SWT.CHECK);
		isDrop.setText(BUNDLE.getString("GenerateDDLPage1.isDrop"));
		data= new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		isDrop.setLayoutData(data);
		isDrop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DoCheck();
			}
		});
		
		
		Label labelSaveAs=new Label(container, SWT.NULL);
		labelSaveAs.setText(BUNDLE.getString("GenerateDDLPage1.SaveAs"));
		
		inputfileName=new Text(container, SWT.BORDER | SWT.SINGLE);
		inputfileName.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));
		      //int count=project.getLocation().segmentCount();//+"/"+"schema.sql";
		//strPath=project.getLocation().lastSegment().toString()+"/"+"schema.sql";
		      //strPath=project.getLocation().removeFirstSegments(count-1).toString()+"/"+"schema.sql";
		strPath=project.getName()+"/"+"schema.sql";
		inputfileName.setText(strPath);
		inputfileName.addModifyListener(new ModifyListener()
 					{
						public void modifyText(ModifyEvent e) {
							 Text text = (Text) e.widget;
							 strPath=text.getText();	
						}
					}); 
 					
		browseuBtton=new Button(container,SWT.PUSH);
		GridData d=setButtonLayoutData( browseuBtton);
		//data= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING );
		browseuBtton.setLayoutData(d);
		browseuBtton.setText(BUNDLE.getString("GenerateDDLPage1.Browse"));
		browseuBtton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DoBrowse();
				getWizard().getContainer().updateButtons();
			}
		});		
		this.setPageComplete(false);
		setControl(container);		
	}

	/**
	 * DoCheck() of the GenerateDDLPage1
	 */
	private void DoCheck() {
		String str=null;
		check=!check;
		if(check)
			str="true";
		else str="false";
		properties.setProperty("drop",str);
	}	
	
	
	/**
	 * canFlipToNextPage of the GenerateDDLPage1
	 */
	public boolean canFlipToNextPage() {
		return this.isPageComplete();
		
	}
	
	/**
	 * isPageComplete()of the GenerateDDLPage1
	 */
	public boolean isPageComplete() {
//		added 8/3/2005
		if(itype==null && classes.length!=0){
			setErrorMessage(BUNDLE.getString("GenerateDDLPage1.ErrorMessage"));
			return false;
			
		}//added 8/3/2005
		if (dialectText.getText()!="" &&  inputfileName.getText()!="" )
			return true;
		
		else
			return false;
	}
	

	
	
	private void initGenerator() {
		 project=ormmodel.getProject().getProject();
		 projectFromWSpace=project;
		 dialect=ormmodel.getConfiguration().getProperty(BUNDLE.getString("GenerateDDLPage1.property_hibernate.dialect"));
		//added 8/3/2005
		 classes=ormmodel.getPersistentClassMappings();//classes[0].getName()
		 
     	//TODO (tau-tau) for Exception		 
		try {
			if(classes.length!=0)
				itype=ScanProject.findClass(classes[0].getName(),ormmodel.getProject().getProject());
		} catch (CoreException e) {
			ExceptionHandler.handle(e,getShell(),BUNDLE.getString("GenerateDDLPage1.Title"), null);
		}////added 8/3/2005
	
		try {
			generator = new HibernateDDLGenerator();
		} catch (NoClassDefFoundError e) {
			
		}
	}

	private void setDialect() {
		properties.setProperty("dialect",dialectText.getText());
	}
	private void setDelimeter(String str){
		properties.setProperty("delimiter",str);
	}
	

	public boolean DoBrowse(){
		
	String str=null;
	
		SaveAsDialog dialog=new SaveAsDialog(getShell());
		dialog.setOriginalName("schema.sql");	
		dialog.create(); 
		if (dialog.open() == Dialog.CANCEL)
			return false;
		str=dialog.getResult().toString();
		IPath filePath=dialog.getResult();
		projectFromWSpace = ResourcesPlugin.getWorkspace().getRoot().getProject(filePath.segment(0));
		if(str!=null);
        	inputfileName.setText(str);
		return true;
	}
	
	public void execute()throws Exception{
		
		//IWorkbenchPage page = ViewPlugin.getPage();
	    
        // changed by Nick 30.08.2005 - code moved to DefaultClassLoaderFactory#create

/*		ClassLoader cl = EclipseResourceUtil.getClassLoader(project, generator.getClassLoader());

        Vector outputUrls = new Vector();
        
		IJavaProject prj = JavaCore.create(project);
        IPackageFragmentRoot[] entries = prj.getAllPackageFragmentRoots();
        if (entries != null)
        {
            for (int i = 0; i < entries.length; i++) {
                IPackageFragmentRoot entry = entries[i];
                if (entry.getKind() == IPackageFragmentRoot.K_SOURCE)
                {
                    IPath output = entry.getRawClasspathEntry().getOutputLocation();

                    // added by Nick 30.08.2005
                    if (output == null)
                    {
                        IJavaProject jProject = entry.getJavaProject();
                        if (jProject != null)
                        {
                            output = jProject.getOutputLocation();
                        }
                    }
                    // by Nick
                    
                    if (output != null)
                    {
                        IPath fullPath = ScanProject.relativePathToAbsolute(output,project);
                        if (fullPath != null)
                        {
                            if (fullPath.toFile().isDirectory())
                            {
                                String osPath = "file:///"+fullPath.toOSString();
                                if (!fullPath.hasTrailingSeparator())
                                    osPath += "/";
                                
                                URL url = new URL(osPath);
                                // added by Nick 30.08.2005
                                if (!outputUrls.contains(url))
                                // by Nick
                                    outputUrls.add(url);
                            }
                        }
                    }
                }
            }
        }

        URL[] urls = (URL[]) outputUrls.toArray(new URL[0]);
        if (urls != null && urls.length != 0)
            cl = new URLClassLoader(urls,cl);
*/        

        ClassLoader cl = DefaultClassLoaderFactory.create(project,generator.getClassLoader());
        
        properties.put("classloader", cl);
		properties.put("url",ormmodel.getConfiguration().getResource().getRawLocation().toString());
		setPathFileSql();
		try{
			
		generator.generate(properties);
		String root = projectFromWSpace.getLocation().toString();		
		String filename = properties.getProperty("filename");
		filename = filename.substring(root.length());

		// add tau for ESORM-614: schema.sql was generateg, but in Navigator appears only after manually refresh. After DDL generation occure message, that file schema.sql doesn't exists... 
		if(!projectFromWSpace.getProject().isSynchronized(IResource.DEPTH_INFINITE)) {
        	try {
        		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("GenerateDDLPage1 -> refreshLocal for: " + projectFromWSpace);                		
        		projectFromWSpace.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
				//throw new CoreException(Status.CANCEL_STATUS); //for test only
			} catch (CoreException e) {
                ExceptionHandler.handle(e,
                		ViewPlugin.getActiveWorkbenchShell(),
                		BUNDLE.getString("Explorer.GenerateDDLErrorTitle"),
                		e.getMessage());
                return;
            }
        }
		
		
		file = projectFromWSpace.getFile(filename);
		if(file != null)
			this.getControl().getDisplay().syncExec(new Runnable(){
				public void run(){
					IWorkbenchPage page = ViewPlugin.getPage();
					try {
					IDE.openEditor(page,file);
					} catch (PartInitException e) {
						ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),"Error","Error at opening a file " +properties.getProperty("filename"));
					}
				}});
		}catch( final Exception   ee){
			this.getControl().getDisplay().syncExec(new Runnable(){
				public void run(){
					ExceptionHandler.handle(ee,ViewPlugin.getActiveWorkbenchShell(),"Generate DDL Wizard", null);
				}
				
			});
		}
		
	}
	public void setErrorMessage(String newMessage) {
		super.setErrorMessage(newMessage);
}
	public void setMessage(String newMessage,int type){
		super.setMessage(newMessage,type);
	}
	

	private void setPathFileSql(){
		try{
			IPath pWithoutProject=new Path(strPath);
			pWithoutProject=pWithoutProject.removeFirstSegments(1);			
			IPath pathToSqlFile=projectFromWSpace.getLocation().append(pWithoutProject);
					//IPath p=project.getLocation().removeLastSegments(1).append(strPath);
			properties.put("filename",pathToSqlFile.toString());
			//properties.put("filename",filePath.toString());
			
		}catch( final Exception e){
			this.getControl().getDisplay().syncExec(new Runnable(){
				public void run(){
					ExceptionHandler.handle(ee,ViewPlugin.getActiveWorkbenchShell(),null, null);
				}
				
			});
			
		}
	}


	

	

}