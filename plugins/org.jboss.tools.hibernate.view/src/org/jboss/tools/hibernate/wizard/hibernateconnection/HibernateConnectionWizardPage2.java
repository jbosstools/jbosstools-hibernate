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
package org.jboss.tools.hibernate.wizard.hibernateconnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.dialog.xpl.ClassPathPropertyDialogAction;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;
import org.jboss.tools.hibernate.view.ViewPlugin;



/**
 * @author sushko
 * Hibernate Connection Wizard page2(non- managed environment)
 */
public class HibernateConnectionWizardPage2 extends WizardPage {
	
	public static final String BUNDLE_NAME = "hibernateconnection"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(HibernateConnectionWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);
	private Text UserIdText;
	private Text PasswordText;
	private Combo HibDialectText;
	private Text HibDriverClassText;
	private Combo HibConnUrlText;
	private Text DriverLocationText;
	 private IWizardPage previousPage = null;
	//20050712 <yan>
	private  String dialectStr="",hibConnUrlStr="",driverLocationStr="",hibDriverClassStr="",userIdStr="",passwordStr="";
	// </yan>
	//$added$ by Konstantin Mishin on 2005/08/16 fixed for ORMIISTUD-544
	private int heshSetSize;
	//$added$
	private Connection connection;
	private IMapping mapping;
	private Button BrowseButton,TestButton;
	public String [] locationnames, locationpath;
	private String prevDialect;
	private boolean isDialectChange=false;

	private String [] dialects =new String[]{
			//"",
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectDB2").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectDB2AS/400").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectDB2OS390").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectPostgreSQL").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectMySQL").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectMySQLwithInnoDB").trim(),
 			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectMySQLwithMyISAM").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectOracle(anyversion)").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectOracle9i/10g").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectSybase").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectSybaseAnywhere").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectMicrosoftSQLServer").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectSAPDB").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectInformix").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectHypersonicSQL").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectIngres").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectProgress").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectMckoiSQL").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectInterbase").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectPointbase").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectFrontBase").trim(),
			BUNDLE.getString("HibernateConnectionWizardPage2.hibernatedialectFirebird").trim()	
			};

	private String [] urls =OrmConfiguration.urls;
		
	private String [] drivers =OrmConfiguration.drivers;
	
	
	
	/** constructor for the  Page2
	 * @param ormmodel
	 **/
	public HibernateConnectionWizardPage2 (IMapping mapping,String title) {
		super(BUNDLE.getString("HibernateConnectionWizardPage2.Title"));
		setTitle(title);
		setDescription(BUNDLE.getString("HibernateConnectionWizardPage2.Description"));
		this.mapping = mapping;
		init();
					
	}
		public HibernateConnectionWizardPage2 (IMapping mapping) {
			super(BUNDLE.getString("HibernateConnectionWizardPage2.Title"));
			setTitle(BUNDLE.getString("HibernateConnectionWizardPage2.Title"));
			setDescription(BUNDLE.getString("HibernateConnectionWizardPage2.Description"));
			this.mapping = mapping;
			init();
						
		}
		
		public void init (){
			prevDialect=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.dialect"));			
			driverLocationStr="";
			//HibDriverClassStr="";
		}
	
	/** createControl for the  Page2
	 * @param parent
	 **/
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		initializeDialogUnits(parent);
		GridLayout layout = new GridLayout();                                                                                                              
		container.setLayout(layout);
		
	    Group grUrlInf = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		Grlayout.numColumns = 6;
		grUrlInf.setLayout(Grlayout);
	    
		grUrlInf.setText(BUNDLE.getString("HibernateConnectionWizardPage2.GrUrlInformationText"));
	    GridData groupData =new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
	    grUrlInf.setLayoutData(groupData);

	 
		Label lblHibDialect = new Label(grUrlInf, SWT.NULL);		
		lblHibDialect.setText(BUNDLE.getString("HibernateConnectionWizardPage2.HibernateDialect"));
		lblHibDialect.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		HibDialectText = new Combo(grUrlInf, SWT.BORDER);
		HibDialectText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 4, 1));
		HibDialectText.setItems(dialects);
	    dialectStr=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.dialect"));
		if(dialectStr!=null)
			HibDialectText.setText(dialectStr);
		else HibDialectText.setText("");
		HibDialectText.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e) {
				// added 8/3/2005
			//	String str=HibDialectText.getItem(HibDialectText.getSelectionIndex());
			//	if(str.equals(mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.dialect"))) && !mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.url")).equals(""))
				//	HibConnUrlText.setText(mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.url")));
//				 added 8/3/2005
			//	else 
				//$changed$ by Konstantin Mishin on 2005/08/16 fixed for ORMIISTUD-544
				//HibConnUrlText.select(HibDialectText.getSelectionIndex());
				HibConnUrlText.select(HibDialectText.getSelectionIndex()+heshSetSize);
				//$changed$
				HibDriverClassText.setText(drivers[HibDialectText.getSelectionIndex()]);
				setDriverLocation();
				getWizard().getContainer().updateButtons();
				dialectStr=HibDialectText.getText();
				isDialectChange=true;
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				getWizard().getContainer().updateButtons();
			}}
		);

		Label lblHibempty = new Label(grUrlInf, SWT.NULL);		
		lblHibempty.setText("");
		lblHibempty.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		Label lblHibDriverClass = new Label(grUrlInf, SWT.NULL);		
		lblHibDriverClass.setText(BUNDLE.getString("HibernateConnectionWizardPage2.HibernateConnectionDriverClass"));
		lblHibDriverClass.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		HibDriverClassText = new Text(grUrlInf, SWT.BORDER | SWT.SINGLE);
		HibDriverClassText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 4, 1));
		HibDriverClassText.setText(mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.driver_class")));
		HibDriverClassText.addModifyListener(new ModifyListener()
					{
			public void modifyText(ModifyEvent e) {
					Text text = (Text) e.widget;
					if(text.getText()!=null)
						hibDriverClassStr=text.getText();
						isPageComplete();
				}
		}); 

		
		
		Label lblHibempty1 = new Label(grUrlInf, SWT.NULL);		
		lblHibempty1.setText("");
		lblHibempty1.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		
		Label lblHibConnUrl = new Label(grUrlInf, SWT.NULL);		
		lblHibConnUrl.setText(BUNDLE.getString("HibernateConnectionWizardPage2.HibernateConnectionUrl"));
		lblHibConnUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		HibConnUrlText = new Combo(grUrlInf, SWT.BORDER);
		
		HibConnUrlText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 4, 1));
		//HibConnUrlText.setItems(urls);
		//20050712 <yan>
		
		//urls[0]=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.url"));
		HibConnUrlText.setItems(urls);
		//$added$ by Konstantin Mishin on 2005/08/15 fixed for ORMIISTUD-544
		IMapping mappings[] = mapping.getProject().getMappings();
		HashSet<String> heshSet = new HashSet<String>();
		for(int i=0;i<mappings.length;i++)
			heshSet.add(mappings[i].getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.url")));
		heshSet.remove("");
		// $added$ by Konstantin Mishin on 2005/08/16 fixed for ORMIISTUD-544
		heshSetSize = heshSet.size();
		// $added$
		Iterator iterator = heshSet.iterator();
		while(iterator.hasNext())
			HibConnUrlText.add((String)iterator.next(),0);
		//$added$
		HibConnUrlText.setText(mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.url")));
		// </yan>
		
		HibConnUrlText.addModifyListener(new ModifyListener()
					{
			public void modifyText(ModifyEvent e) {
				Combo Ctext = (Combo) e.widget;
				if(Ctext.getText()!=null)
					hibConnUrlStr=Ctext.getText();
					isPageComplete();
			}
		}); 

		
		Label lblHibempty2 = new Label(grUrlInf, SWT.NULL);		
		lblHibempty2.setText("");
		lblHibempty2.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		
		Label lblDrvLocation = new Label(grUrlInf, SWT.NULL);		
		lblDrvLocation.setText(BUNDLE.getString("HibernateConnectionWizardPage2.DriverLocation"));
		lblDrvLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		DriverLocationText = new Text(grUrlInf, SWT.BORDER | SWT.SINGLE);
		
		DriverLocationText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 4, 1));
		//DriverLocationText.setSize(10,15);
		DriverLocationText.setEditable(false);
		setDriverLocation();
		DriverLocationText.addModifyListener(new ModifyListener()
					{
			public void modifyText(ModifyEvent e) {
				Text text = (Text) e.widget;
				if(text.getText()!=null)
					driverLocationStr=text.getText();
					isPageComplete();
			}
		}); 

		
		BrowseButton= new Button(grUrlInf, SWT.PUSH);
		BrowseButton.setText(BUNDLE.getString("HibernateConnectionWizardPage2.BrowseButton"));
		BrowseButton.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false, 1, 3));
		GridData d=setButtonLayoutData( BrowseButton);
		BrowseButton.setLayoutData(d);
		BrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IProject project = mapping.getProject().getProject();
				if (project == null)
					return;

				final SelProvider selProvider = new SelProvider();
				
                selProvider.projectSelection = new StructuredSelection(project);
				
//			    final IJavaProject javaProject =  JavaCore.create(mapping.getProject().getProject());	
//				selProvider.projectSelection = new StructuredSelection(oldClasspath);
				
//				PropertyDialogAction propAction = new PropertyDialogAction(getShell(), selProvider);

                ClassPathPropertyDialogAction propAction = new ClassPathPropertyDialogAction(getShell(), selProvider);
                propAction.run(BUNDLE.getString("HibernateConnectionWizardPage2.showedNodePage"));

                IJobManager jobMngr = Platform.getJobManager();
                if (jobMngr != null)
                {
                    Job[] jobs = jobMngr.find(null);
                    
                    Job toJoinJob = null;
                    
                    if (jobs != null && jobs.length != 0)
                    {
                        int i = jobs.length;

                        while (toJoinJob == null && i > 0) {
                            i--;
                            Job job = jobs[i];
                            if ("Setting build path".equals(job.getName()))
                                toJoinJob = job;
                        }
                    }
                    
                    if (toJoinJob != null)
                    {
                        try {
                            toJoinJob.join();
                        } catch (InterruptedException e1) {
                        	//TODO (tau-tau) for Exception                        	
                            ExceptionHandler.logThrowableError(e1,e1.getMessage());
                        }
                    }
                }
                
                // add tau 06.03.2006
                //ESORM-535 - HCW can't find JDBC class driver in Drivers jar file
                mapping.getProject().setDirty(true);
                
                setDriverLocation();
                //setDialect();
                getWizard().getContainer().updateButtons();         
                
                //theJob.schedule();
                
//				}				
			}
		});		
		
		
// 		User Information Group		
	    Group grUserInf = new Group(container, SWT.FILL);
		GridLayout GrlayoutUI = new GridLayout();
		GrlayoutUI.numColumns = 8;
		GrlayoutUI.makeColumnsEqualWidth=true;
		grUserInf.setLayout(GrlayoutUI);
		grUserInf .setText(BUNDLE.getString("HibernateConnectionWizardPage2.GrUserInformationText"));
		
	    GridData groupDataUI =   new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
	    grUserInf.setLayoutData(groupDataUI);

		Label lblUserId = new Label(grUserInf, SWT.NULL);		
		lblUserId.setText(BUNDLE.getString("HibernateConnectionWizardPage2.UserIdText"));
		lblUserId.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, false, false, 1, 1));
		//lblUserId.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		UserIdText = new Text(grUserInf, SWT.BORDER | SWT.SINGLE);
		UserIdText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
		Label empty = new Label(grUserInf, SWT.NULL);
		empty.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, false, false, 5, 1));
		UserIdText.setText(mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.username")));
		UserIdText.addModifyListener(new ModifyListener()
					{
			public void modifyText(ModifyEvent e) {
				Text text = (Text) e.widget;
				if(text.getText()!=null)
					userIdStr=text.getText();
					//isPageComplete();
			}
		}); 
		UserIdText.addKeyListener(new KeyListener()
				{
					public void keyPressed(KeyEvent e) {
//						getWizard().getContainer().updateButtons();			
					}

					public void keyReleased(KeyEvent e) {
				//		getWizard().getContainer().updateButtons();			
					}}
				);

		Label lblPassword = new Label(grUserInf, SWT.NULL);		
		lblPassword.setText(BUNDLE.getString("HibernateConnectionWizardPage2.PasswordText"));
		lblPassword.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, false, false, 1, 1));
		PasswordText = new Text(grUserInf, SWT.BORDER | SWT.SINGLE);
		PasswordText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
		PasswordText.setEchoChar('*');
		PasswordText.setText(mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.password")));
		PasswordText.addModifyListener(new ModifyListener()
					{
			public void modifyText(ModifyEvent e) {
				Text text = (Text) e.widget;
				if(text.getText()!=null)
					passwordStr=text.getText();
					//isPageComplete();
			}
		}); 
		Label empty2 = new Label(grUserInf, SWT.NULL);
		empty2.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, false, false, 5, 1));
		
		TestButton= new Button(container, SWT.PUSH);
		TestButton.setText(BUNDLE.getString("HibernateConnectionWizardPage2.TestButton"));
		GridData data= new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1);
		TestButton.setLayoutData(data);
		TestButton.setEnabled(false);
		TestButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
					 testConnection(true);//locationpath,locationnames,				
			}});	
	    setControl(container);
	    
	 	//20050712 <yan>
	    hibDriverClassStr=HibDriverClassText.getText();
	  //  driverClassStr=HibDialectText.getText();
	    hibConnUrlStr=HibConnUrlText.getText();
	    driverLocationStr=DriverLocationText.getText();
	   // dialectStr=HibDialectText.getText();
	    userIdStr=UserIdText.getText();
	    passwordStr=PasswordText.getText();
	   // </yan>
	    isPageComplete();
	  // getWizard().getContainer().updateButtons();  
	}

	

public String getDialect(){
	return dialectStr;
}
	
	

/** isPageComplete() for the  Page2
 * 
 **/
	public boolean isPageComplete() {//UserIdText.getText()!="" &&////HibConnUrlText.getText()!=""
		if (  HibDialectText.getText()!=""
		   && !hibDriverClassStr.equals("") && !hibConnUrlStr.equals("")  &&  !driverLocationStr.equals("")){//DriverLocationText.getText()!=""){	
				setErrorMessage(null);
				TestButton.setEnabled(true);
				return true;
		}
	else{
		if (DriverLocationText.getText()==""){
			setErrorMessage(BUNDLE.getString("HibernateConnectionWizardPage2.addjdbcdriverclass"));
		}
		else{
			setErrorMessage(BUNDLE.getString("HibernateConnectionWizardPage2.errormessageallfields"));
		}
		TestButton.setEnabled(false);		
		return false;
		}
	}
	
/** isPageComplete() for the  Page2
 * @param newMessage
 **/
	public void setErrorMessage(String newMessage) {
			super.setErrorMessage(newMessage);
	}
	

	/** testConnection() return connection for the database
	 * @param location
	 * @return Connection
	 **///String[] locationpath,  String[] locationnames,
public  Connection  testConnection(boolean noDialogOk){
    TestUrlClassLoader cl=null;	
	try {

        ArrayList<URL> urls1 = new ArrayList<URL>();
        for (int i=0;i<locationpath.length;i++)
        {
            urls1.add(new URL("file:"+locationpath[i]+"/" +locationnames[i]));
        }
/*		URL [] urls=new URL [locationpath.length+1];
		for (int i=0;i<locationpath.length;i++)
		{
			urls[i]=new URL("file://"+locationpath[i]+"\\" +locationnames[i]);
		}
*/	
        URL binURL = ViewPlugin.getDefault().getBundle().getEntry("/bin");
		if (binURL != null)
		{
            binURL = FileLocator.resolve(binURL);
            urls1.add(binURL);
        }

        URL jarURL = ViewPlugin.getDefault().getBundle().getEntry("/view.jar");
        if (jarURL != null){
        
            jarURL = FileLocator.resolve(jarURL);
            urls1.add(jarURL);
        }
		cl = new TestUrlClassLoader(((URL[])urls1.toArray(new URL[0])) ,this.getClass().getClassLoader());
		
//		cl = new TestUrlClassLoader(new URL[]{new URL("file://"+location),new URL("file://"+strfinal+"bin/")},this.getClass().getClassLoader());
	} catch (MalformedURLException e) {
		ExceptionHandler.handle(e,getShell(),BUNDLE.getString("HibernateConnectionWizard.Title"), null);
		return null;
	} catch (IOException e) {
		ExceptionHandler.handle(e,getShell(),BUNDLE.getString("HibernateConnectionWizard.Title"), null);
    }
		Object cls=null;
		
    	//TODO (tau-tau) for Exception		
		try {
			cls = cl.findClass(TestConnectionFactory.class.getName()).newInstance();
		} catch (InstantiationException e) {
			ExceptionHandler.handle(e,getShell(),BUNDLE.getString("HibernateConnectionWizard.Title"), null);
			return null;
			
		} catch (IllegalAccessException e) {
			ExceptionHandler.handle(e,getShell(),BUNDLE.getString("HibernateConnectionWizard.Title"), null);
			return null;
			
		} catch (ClassNotFoundException e) {
			ExceptionHandler.handle(e,getShell(),BUNDLE.getString("HibernateConnectionWizard.Title"), null);
			return null;
			
		}
		ITestConnection itestconnection = (ITestConnection)cls;
			Connection conn=null;
        	//TODO (tau-tau) for Exception			
			try {
				//if(userIdStr.equals(""))
				//if(UserIdText.getText().equals(""))
					//conn = itestconnection.TestConnection(HibDriverClassText.getText(),HibConnUrlText.getText());
				//else conn = itestconnection.TestConnection(HibDriverClassText.getText(),HibConnUrlText.getText(),UserIdText.getText(),PasswordText.getText());
				if(userIdStr.equals(""))
					conn = itestconnection.TestConnection(hibDriverClassStr,hibConnUrlStr);
				else conn = itestconnection.TestConnection(hibDriverClassStr,hibConnUrlStr,userIdStr,passwordStr);
			} catch (final Exception e) {
				// #added# by Konstantin Mishin on 30.08.2005 fixed for ORMIISTUD-703
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));			
				if (e.getMessage()!=null)				
					ExceptionHandler.logThrowableWarning(e, System.getProperty("line.separator")+sw.toString()); // tau 15.09.2005
				else
					ExceptionHandler.logThrowableWarning(e,System.getProperty("line.separator")+sw.toString()); // tau 15.09.2005
				// #added#
				//change dialog
				this.getControl().getDisplay().syncExec(new Runnable(){
					public void run(){
						//ExceptionHandler.displayMessageDialog(e,ViewPlugin.getActiveWorkbenchShell(),BUNDLE.getString("HibernateConnectionWizard.Title"), e.getMessage());//e.getClass().getName());
						// #changed# by Konstantin Mishin on 29.08.2005 fixed for ORMIISTUD-690
						//ExceptionHandler.displayMessageDialog(e,ViewPlugin.getActiveWorkbenchShell(),BUNDLE.getString("HibernateConnectionWizard.Title"), "Error:");//e.getClass().getName());
						//akuzmin 15.09.2005 ESORM-71 
						//ExceptionHandler.displayMessageDialog(e,getShell(),BUNDLE.getString("HibernateConnectionWizard.Title"), "Error:");//e.getClass().getName());
						ExceptionHandler.displayMessageDialog(e,getShell(),getTitle(), "Error:");//e.getClass().getName());
						// #changed#
					}
					
				});
				return null;
			
			}catch(final Throwable  e){
				this.getControl().getDisplay().syncExec(new Runnable(){
					public void run(){
						//ErrorDialog dlg;
						//EventDetailsDialogAction dlg=new EventDetailsDialogAction()
						//EventDetailsDialog dlg=  new EventDetailsDialog(ViewPlugin.getActiveWorkbenchShell(), element, provider);
						ExceptionHandler.displayMessageDialog(e,ViewPlugin.getActiveWorkbenchShell(),BUNDLE.getString("HibernateConnectionWizard.Title"), e.getClass().getName());
						//ErrorDialog1(e,ViewPlugin.getActiveWorkbenchShell());
					}
					
				});
				//ExceptionHandler.displayMessageDialog(e,getShell(),"Hibernate Connection Wizard","" +e.getClass().getName());//.handle(e,getShell(),null, null);
				return null;
			}
			
			if(noDialogOk)
				MessageDialog.openInformation(getShell(), "Information", BUNDLE.getString("HibernateConnectionWizardPage2.testsuccess"));
			setConnection(conn);
			return conn;
	
}	

/** setDriverLocation() for the  Page2
 * This method sets Driver Location path from the project classpath
 * 
 **/
public void setDriverLocation(){
	String strTypeBase="",strTypeUtil="";
	
	try {
		
		IType itype = null;
		
		if (!HibDriverClassText.getText().equals("")) {
		
			itype = ScanProject.findClass(HibDriverClassText.getText(),mapping.getProject().getProject());
		
			if(HibDialectText.getText().equals("org.hibernate.dialect.SQLServerDialect")){
				IType itype2=ScanProject.findClass("com.microsoft.util.UtilLocalMessages",mapping.getProject().getProject());
				if(itype2!=null){	
					strTypeUtil=";";
					strTypeUtil+=itype2.getPackageFragment().getPath().lastSegment().toString();
				}
				IType itype1=ScanProject.findClass("com.microsoft.jdbc.base.BaseDriver",mapping.getProject().getProject());
				if(itype1!=null){	
					strTypeBase=";";
					strTypeBase+=itype1.getPackageFragment().getPath().lastSegment().toString();
				}
			}
		}
			
		
		if (itype!=null)
		{
			if(HibDriverClassText.getText().equals("com.microsoft.jdbc.sqlserver.SQLServerDriver"))
				DriverLocationText.setText(itype.getPackageFragment().getPath().toString()+strTypeBase+strTypeUtil);
			//DriverLocationText.setText(itype.getPackageFragment().getPath().toString());//+strTypeBase+strTypeUtil
			else
			DriverLocationText.setText(itype.getPackageFragment().getPath().toString());
			
			
//			locationpath=new String[1];
//			locationpath[0]=itype.getPackageFragment().getPath().removeLastSegments(1).toString();
			
		    IJavaProject javaProject =  JavaCore.create(mapping.getProject().getProject());
			
			ArrayList<String> locationsList = new ArrayList<String>();
            ArrayList<String> locationsNamesList = new ArrayList<String>();

            IProject project = mapping.getProject().getProject();
            IWorkspaceRoot wrkRoot = project.getWorkspace().getRoot();
            
			for (int i=0; i<javaProject.getAllPackageFragmentRoots().length; i++)
			{
                IPath path = javaProject.getAllPackageFragmentRoots()[i].getPath(); 
                if (path != null)
                {
                    IResource rsrc = wrkRoot.findMember(path);
                    IPath thePath = path;
                    if (rsrc != null)
                    {
                        thePath = rsrc.getLocation();
                    }
                    if (thePath != null)
                    {
                        locationsList.add(thePath.removeLastSegments(1).toString());
                        locationsNamesList.add(thePath.lastSegment().toString());
                    }
                }
			}

            locationpath = (String[]) locationsList.toArray(new String[0]);
            locationnames = (String[]) locationsNamesList.toArray(new String[0]);
            

			setErrorMessage(null);
		}else	
		{	
			driverLocationStr="";
			DriverLocationText.setText(driverLocationStr);
			setErrorMessage(BUNDLE.getString("HibernateConnectionWizardPage2.addjdbcdriverclass"));
			}
	} catch (CoreException e) {
    	//TODO (tau-tau) for Exception		
		//ExceptionHandler.handle(e1, ViewPlugin.getActiveWorkbenchShell(), e1.getMessage(),e1.getMessage());
		ExceptionHandler.handle(e,getShell(),BUNDLE.getString("HibernateConnectionWizard.Title"), null);
	}
	
	}
	
/** getConnection() 
 * This method gets conection from internal variable
 * @return Connection
 **/
	public Connection getConnection() {
		return connection;
	}
	
/** setConnection
 * This method sets connection of the page
 * @param connection
 **/
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	/** saveConfiguration()
	 * The all necessary data are saved here in to OrmModel
	 **/
	public void saveConfiguration() {
		if(!HibDialectText.getText().equals(""))
			mapping.getConfiguration().setProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.dialect"),HibDialectText.getText());
		mapping.getConfiguration().setProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.driver_class"),HibDriverClassText.getText());
		mapping.getConfiguration().setProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.url"),HibConnUrlText.getText());
		mapping.getConfiguration().setProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.username"),UserIdText.getText());
		mapping.getConfiguration().setProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.password"),PasswordText.getText());
		//if(!isDialectSelected)
		prevDialect=HibDialectText.getText();//mapping.getConfiguration().getProperty("hibernate.dialect");
	}

	
	public int setDialect(){
		String  urlFromModel=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.url"));
		String dialFromModel=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.dialect"));
		// #added# by Konstantin Mishin on 27.12.2005 fixed for ESORM-442
		String driverFromModel=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.driver_class"));
		// #added#		
		userIdStr=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.username"));
		passwordStr=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.connection.password"));
		if(!dialFromModel.equals("")){
			HibDialectText.setText(dialFromModel);
			for (int i = 0; i < dialects.length; i++){
				
				if(dialects[i].compareTo(dialFromModel)==0)
					HibDialectText.select(i);
				
			}
			if(urlFromModel.equals("") || !dialFromModel.equals(prevDialect)){
				if(HibDialectText.getSelectionIndex()==-1)
					return -1;
				HibConnUrlText.setText(urls[HibDialectText.getSelectionIndex()]);
			}
			else 
				HibConnUrlText.setText(urlFromModel);
			// #changed# by Konstantin Mishin on 27.12.2005 fixed for ESORM-442
			if(driverFromModel.equals("") || !dialFromModel.equals(prevDialect)){
				if(HibDialectText.getSelectionIndex()==-1)
					return -1;
				HibDriverClassText.setText(drivers[HibDialectText.getSelectionIndex()]);
			} else
				HibDriverClassText.setText(driverFromModel);				
			// #changed#
			
			PasswordText.setText(passwordStr);
			UserIdText.setText(userIdStr);
			setDriverLocation();
			
		}
		return 0;
	}
	public boolean isSupport(){
		boolean supported=true;
		String dialFromModel=mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.dialect"));
			if(!dialFromModel.equals("")){
				HibDialectText.setText(dialFromModel);
				for (int i = 0; i < dialects.length; i++){
			 	
			 		if(dialects[i].compareTo(dialFromModel)==0)
			 			HibDialectText.select(i);
			 			 
			 	}
				if(HibDialectText.getSelectionIndex()==-1)
					supported=false;			
			}
		return supported;
	}
	//20050712 <yan>
	public boolean  getIsDialectChange(){
		return isDialectChange;
	}
	public  void setIsDialectChange(boolean set){
		isDialectChange=set;
	}
	
	public String getPassword() {
		return PasswordText.getText();
	}
	
	public String getLogin() {
		return UserIdText.getText();
	}
	
	public String getHibernateDialect() {
		return HibDialectText.getText();
	}
	
	public String getDriverClassName() {
		return HibDriverClassText.getText();
	}
	
	public String getDriverLocation() {
		return DriverLocationText.getText();
	}
	
	public String getConnectionString() {//</yan>
		return HibConnUrlText.getText();
	}
	
	/* (non-Javadoc)   Added 11/22/2005
	 * @see org.eclipse.jface.wizard.IWizardPage#getPreviousPage()
	 */
	public IWizardPage getPreviousPage() {
		previousPage=this.getWizard().getPreviousPage(this);
		if (previousPage instanceof HibernateConnectionWizardPage1)//{
				saveConfiguration();
			
        return previousPage;
    }
}
