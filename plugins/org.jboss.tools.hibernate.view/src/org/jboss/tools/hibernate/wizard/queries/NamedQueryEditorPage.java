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
package org.jboss.tools.hibernate.wizard.queries;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.hibernate.query.NamedQueryDefinition;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.ColorManager;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.QLConfiguration;
import org.jboss.tools.hibernate.wizard.classloader.DefaultClassLoaderFactory;
import org.jboss.tools.hibernate.wizard.hibernateconnection.HibernateConnectionWizardPage2;


/**
 * @author yan
 *
 */
public class NamedQueryEditorPage extends WizardPage implements IRunnableWithProgress {
	
	/*private Text queryEditor,nameEditor*/;
	private NamedQueriesWizard wizard;
	private TreeViewer treeViewer;
	private Button execButton,closeTransaction;
	private NamedQueryDefinition propertiesSource;
	private Session session;
	private Connection connection;
	// $changed$ by Konstantin Mishin on 2005/08/01
	private Document document; 
	private ColorManager colorManager;
	private Text nameEditor; //queryEditor,
	// $changed$
	//$added$ by Konstantin Mishin on 2005/08/15 fixed for ORMIISTUD-637
	private Document consoleDocument;
	//$added$

	//$added$ by Konstantin Mishin on 2005/08/31 fixed for ORMIISTUD-637
	private PrintStream defaultPrintStream;
	private ByteArrayOutputStream bas;
	//$added$
	
	private PropertySheetPage propertySheetPage;
	private HibernateConnectionWizardPage2 connectionPage;
	
	private static final String CACHE_REGION_PROPERTY="cacheRegion";
	private static final String CACHEABLE_PROPERTY="cacheable";
	private static final String TIMEOUT_PROPERTY="timeout";
	private static final String FETCH_SIZE_PROPERTY="fetchSize";
	private static final String FLUSH_MODE_PROPERTY="flushMode";
	public static final int MAX_RESULTS=100;
	
	private PropertyDescriptorsHolder holder;
	private String queryName,queryString,driverClassName,connectionString,currentConnectionString,dialectString,userString,passwordString;
	private TabFolder resultTabFolder;
	
	private SessionFactory sessionFactory;
	private java.sql.Driver sqlDriver;
	private Configuration configuration;
	

	public NamedQueryEditorPage(final NamedQueriesWizard wizard) {
		super(wizard.getString("NamedQueryEditorPage.Title"));
		this.wizard=wizard;
		setTitle(wizard.getString("NamedQueryEditorPage.Title"));
		setDescription(wizard.getString("NamedQueryEditorPage.Description"+(wizard.isNewQueryMode()?"1":"2")));
		
		ICellEditorValidator numberValidator=new ICellEditorValidator() {
		
			public String isValid(Object value) {
				try {
					String s=(String)value;
					if (s.length()>0) Integer.decode(s); 
				} catch(Exception e) {
	            	//TODO (tau-tau) for Exception					
					ExceptionHandler.displayMessageDialog(e,wizard.getShell(),wizard.getString("InvalidInput"),null);
					return wizard.getString("InvalidInput");
				}
				return null;
			}
		
		};
		
		holder=new PropertyDescriptorsHolder();
		IPropertyDescriptor desc=null;
		
		holder.addPropertyDescriptor(
				desc=new ListPropertyDescriptor(
						CACHEABLE_PROPERTY,
						wizard.getString("NamedQueryEditorPage.Property.Cacheable"),
						new String[]{"true","false"}
				)
		);
		holder.setDefaultPropertyValue(desc.getId(),"false");
		holder.addPropertyDescriptor(
				desc=new TextPropertyDescriptor(CACHE_REGION_PROPERTY,wizard.getString("NamedQueryEditorPage.Property.CacheRegion"))
		);
		holder.setDefaultPropertyValue(desc.getId(),"");
		holder.addPropertyDescriptor(
				desc=new TextPropertyDescriptor(TIMEOUT_PROPERTY,wizard.getString("NamedQueryEditorPage.Property.Timeout"))
		);
		((TextPropertyDescriptor)desc).setValidator(numberValidator);
		holder.setDefaultPropertyValue(desc.getId(),"");
		holder.addPropertyDescriptor(
				desc=new TextPropertyDescriptor(FETCH_SIZE_PROPERTY,wizard.getString("NamedQueryEditorPage.Property.FetchSize"))
		);
		((TextPropertyDescriptor)desc).setValidator(numberValidator);
		holder.setDefaultPropertyValue(desc.getId(),"");
		holder.addPropertyDescriptor(
				desc=new ListPropertyDescriptor(
						FLUSH_MODE_PROPERTY,
						wizard.getString("NamedQueryEditorPage.Property.FlushMode"),
						new String[]{"","auto","never","always"}
				)
		);
		holder.setDefaultPropertyValue(desc.getId(),"");
		
		
 		connectionPage=new HibernateConnectionWizardPage2(wizard.getMapping(),wizard.getString("NamedQueryConnectionPage.Title"));
		connectionPage.setWizard(wizard);
		
		//$added$ by Konstantin Mishin on 2005/08/31 fixed for ORMIISTUD-637
		bas = new ByteArrayOutputStream();
		defaultPrintStream = System.out;
		System.setOut(new PrintStream(bas));	        
		//$added$		
	}

	public void createControl(Composite parent) {
		
		SashForm root=new SashForm(parent,SWT.VERTICAL);
		
		initializeDialogUnits(parent);
		
		makeEditorControl(root);
		makeResultControl(root);
		
		setControl(root);
		
		setupQuery();
		
		
		
	}
	
	private Control makeResultControl(Composite parent) {
		
		resultTabFolder=new TabFolder(parent,SWT.TOP);
		
		Composite container=new Composite(resultTabFolder, SWT.NULL);
		container.setLayout(new GridLayout(3,false));
		Tree resultTree=new Tree(container,SWT.BORDER|SWT.V_SCROLL|SWT.SINGLE);
		resultTree.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,3,3));
		//$changed$ by Konstantin Mishin on 2005/08/15 fixed for ORMIISTUD-637
		Composite consoleContainer=new Composite(resultTabFolder, SWT.NULL);
		consoleContainer.setLayout(new GridLayout(3,true));
		final SourceViewer console = new SourceViewer(consoleContainer, null, SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL|SWT.MULTI);
		consoleDocument = new Document();
		ColorManager colorManager = new ColorManager();
		console.configure(new QLConfiguration(colorManager,HQLSyntax.WORDS,HQLSyntax.HQL_SEPARATORS));
		console.setDocument(consoleDocument);
		Control control = console.getControl();
		control.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,3,3));
		


		console.setEditable(false);
		// #added# by Konstantin Mishin on 21.09.2005 fixed for ESORM-
	    Menu popupMenu = new Menu(control);
	    MenuItem formatItem = new MenuItem(popupMenu, SWT.NONE);
	    formatItem.setText(wizard.getString("NamedQueryEditorPage.Clear"));
	    formatItem.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    		bas.reset();
	    		consoleDocument.set("");
	    	}
	    });
	    MenuItem simpleQueryItem = new MenuItem(popupMenu, SWT.NONE);
	    simpleQueryItem.setText(wizard.getString("NamedQueryEditorPage.CopyAll"));
	    simpleQueryItem.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    		ISelection selection = console.getSelection();
	    		console.doOperation(SourceViewer.SELECT_ALL);
	    		console.doOperation(SourceViewer.COPY);
	    		console.setSelection(selection);
	    	}
	    });	 
	    control.setMenu(popupMenu);		
	    //#added# 		

		control.setBackground(Display.getCurrent().getSystemColor(1));
		//$changeded$
		closeTransaction=new Button(container,SWT.CHECK);
		closeTransaction.setText(wizard.getString("NamedQueryEditorPage.CloseTransaction"));
		closeTransaction.setLayoutData(new GridData(SWT.BEGINNING,SWT.CENTER,true,false,2,1));	
//		closeTransaction.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//			}
//		});
		// #deleted# by Konstantin Mishin on 23.12.2005 fixed for ESORM-228
		//closeTransaction.setSelection(true);
		// #deleted#

		treeViewer=new TreeViewer(resultTree);
		treeViewer.setContentProvider(new QueryResultContentProvider(wizard.getMapping()){
		//$added$ by Konstantin Mishin on 2005/08/31 fixed for ORMIISTUD-637
			public Object[] getChildren(Object parentElement) {
				Object o[] = super.getChildren(parentElement);
				consoleDocument.set(bas.toString());
				return o;
			}
		});
		//$added$
		treeViewer.setLabelProvider(new QueryResultLabelProvider());

		TabItem tabItem=new TabItem(resultTabFolder,SWT.NONE);
		tabItem.setControl(container);
		tabItem.setText(wizard.getString("NamedQueryEditorPage.Result"));
		
		connectionPage.createControl(resultTabFolder);
		tabItem=new TabItem(resultTabFolder,SWT.NONE);
		tabItem.setControl(connectionPage.getControl());
		tabItem.setText(wizard.getString("NamedQueryEditorPage.Connection"));
		
		//$added$ by Konstantin Mishin on 2005/08/12 fixed for ORMIISTUD-637
		tabItem=new TabItem(resultTabFolder,SWT.NONE);
		tabItem.setControl(consoleContainer);
		tabItem.setText(wizard.getString("NamedQueryEditorPage.Output"));
		//$added$ 
		
		return resultTabFolder;
		
	}
	
	private Control makeEditorControl(Composite parent) { 
		
		TabFolder tabFolder=null;
		
		Composite container=null;
		
		if (!wizard.readOnly()) {
			tabFolder=new TabFolder(parent,SWT.TOP);
			container=new Composite(tabFolder, SWT.NULL);
			container.setLayout(new GridLayout(3,false));
			Label label=new Label(container, SWT.NULL);
			label.setText(wizard.getString("NamedQueryEditorPage.Name"));
			GridData data=new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.horizontalSpan=3;
			label.setLayoutData(data);
			
			nameEditor=new Text(container,SWT.BORDER|SWT.SINGLE);
		   data=new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			data.horizontalSpan=3;
			data.verticalSpan=1;		
			data.grabExcessVerticalSpace=false;
			data.grabExcessHorizontalSpace=true;
			nameEditor.setLayoutData(data);
			
			nameEditor.addModifyListener(
					new ModifyListener() {
					
						public void modifyText(ModifyEvent e) {
							
							if (wizard.isExists(nameEditor.getText().trim())) {
								setErrorMessage(wizard.getString("DuplicateName"));
								wizard.setFinish(false);
							} else {
								setErrorMessage(null);
								wizard.setFinish(true);
							}
					
						}
					
					}
			);
		} else {
			container=new Composite(parent,SWT.TOP);
			container.setLayout(new GridLayout(3,false));
		}
		
		Label label=new Label(container,SWT.NULL);
		label.setText(wizard.getString("NamedQueryEditorPage.Query"));
		label.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false,3,1));
		
		// $changed$ by Konstantin Mishin on 2005/08/01
		SourceViewer queryEditor=new SourceViewer(container, null, SWT.BORDER|SWT.V_SCROLL|SWT.MULTI|SWT.WRAP);
		document = new Document();
		colorManager = new ColorManager();
		queryEditor.configure(new QLConfiguration(colorManager,HQLSyntax.WORDS,HQLSyntax.HQL_SEPARATORS));
		queryEditor.setDocument(document);
		// #changed# by Konstantin Mishin on 2005/08/08
		Control control = queryEditor.getControl();
		control.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,3,4));
		// #changed# 
		// $changed$		
		// #added# by Konstantin Mishin on 2005/08/08		
	    Menu popupMenu = new Menu(control);
	    final MenuItem formatItem = new MenuItem(popupMenu, SWT.NONE);
	    formatItem.setText(wizard.getString("NamedQueryEditorPage.FormatItem"));
	    formatItem.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    		int i=0;
	    		char c, quotesChar;
	    		String str = document.get();
	    		String newStr = new String();
	    		String wordStr = new String();
	    		do {
	    			c = str.charAt(i++);
	    			if (HQLSyntax.HQL_SEPARATORS.indexOf(c)==-1 && i<str.length()) {
	    				wordStr="";
	    				do {
	    					wordStr+=c;
	    					c = str.charAt(i++);
	    				} while (c!=' ' && c!='\'' && i<str.length());
	    				newStr+=(Arrays.binarySearch(HQLSyntax.WORDS,wordStr.toUpperCase())>=0)?wordStr.toUpperCase():wordStr;
	    			}
	    			if ((c=='"' || c=='\'') && i<str.length()) {
	    				quotesChar=c;
	    				do {
	    					newStr+=c;
	    					c = str.charAt(i++);
	    				} while (c!=quotesChar && i<str.length());
	    			}
	    			newStr+=c;
	    		} while (i<str.length());
	    		document.set(newStr);
	    	}
	    });
	    MenuItem simpleQueryItem = new MenuItem(popupMenu, SWT.NONE);
	    simpleQueryItem.setText(wizard.getString("NamedQueryEditorPage.SimpleQueryItem"));
	    simpleQueryItem.addSelectionListener(new SelectionAdapter(){
	    	public void widgetSelected(SelectionEvent e) {
	    		document.set("SELECT dragons FROM eg.dreamword.population.Dragons dragons WHERE dragons.name = 'Red Dragon'");
	    	}
	    });	 
	    // #added# by Konstantin Mishin on 21.11.2005 fixed for ESORM-385
	    popupMenu.addMenuListener(new MenuAdapter(){
	    	public void menuShown(MenuEvent e) {
	    		if(document.get().length()<1)
	    			formatItem.setEnabled(false);
	    		else
	    			formatItem.setEnabled(true);
	    	}
	    });
	    // #added#
	    control.setMenu(popupMenu);		
	    //#added# 		
		document.addDocumentListener(
				new IDocumentListener() {
					public void documentAboutToBeChanged(DocumentEvent event) {
					}
					public void documentChanged(DocumentEvent event) {
						if (event.getDocument().get().trim().length()==0) {
							//setErrorMessage(wizard.getString("EmptyQuery"));
							execButton.setEnabled(false);
						} else {
							//setErrorMessage(null);
							execButton.setEnabled(true);
							//execButton.setEnabled(wizard.canFinish());
						}
					}
				}
		);
		
		
		label=new Label(container,SWT.NULL);
		label.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false,2,1));
		
		execButton=new Button(container,SWT.PUSH);
		execButton.setText(wizard.getString("NamedQueryEditorPage.Run"));
		GridData gd=setButtonLayoutData(execButton);
		gd.verticalSpan=1;
		gd.horizontalSpan=1;
		gd.horizontalAlignment=SWT.END;
		//execButton.setLayoutData(new GridData(SWT.END,SWT.CENTER,true,false,1,1));	
		execButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				NamedQueryDefinition nqd=wizard.getCurrentQuery();
				if (wizard.getCurrentQuery()!=null) {
					
					execQuery();
					
				} else {
					setPageComplete(false);
				}
			}
		});
		
		if (wizard.readOnly()) {
			return container; 
		}
		
		TabItem tabItem=new TabItem(tabFolder,SWT.NONE);
		tabItem.setControl(container);
		tabItem.setText(wizard.getString("NamedQueryEditorPage.NamedQuery"));
		container=new Composite(tabFolder, SWT.NULL);
		container.setLayout(new GridLayout(3,true));
		
		propertySheetPage=new PropertySheetPage();
		propertySheetPage.createControl(container);
		propertySheetPage.getControl().setLayoutData(new  GridData(SWT.FILL,SWT.FILL,true,true,3,3));
		//akuzmin 29.07.2005
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		Menu menu = menuMgr.createContextMenu(propertySheetPage.getControl());
		propertySheetPage.getControl().setMenu(menu);
		//
		
		if (wizard.readOnly()) return tabFolder;
		
		tabItem=new TabItem(tabFolder,SWT.NONE);
		tabItem.setControl(container);
		tabItem.setText(wizard.getString("NamedQueryEditorPage.Properties"));
		
		return tabFolder;
		
	}

	private void setupQuery() {
		NamedQueryDefinition current=wizard.getCurrentQuery();
		if (current!=null) {
			String s=current.getQueryString()==null?"":current.getQueryString();
			document.set(s);
			execButton.setEnabled(s.trim().length()>0);
			if (!wizard.readOnly()) {
				nameEditor.setText(current.getName());
				propertiesSource=new NamedQueryDefinition(
						current.getStorage(),
						current.getName(),
						current.getQueryString(),
						current.isCacheable(),
						current.getCacheRegion(),
						current.getTimeout(),
						current.getFetchSize(),
						current.getFlushMode()
				);
				
				BeanPropertySourceBase source=new BeanPropertySourceBase(propertiesSource);
				source.setPropertyDescriptors(holder);
				
				propertySheetPage.selectionChanged(null, new StructuredSelection(source));
			}
			
			
		} else {
			document.set("");
			//queryEditor.setText("");
			nameEditor.setText("");
			execButton.setEnabled(false);
			BeanPropertySourceBase source=new BeanPropertySourceBase(null);
			source.setPropertyDescriptors(holder);
			propertySheetPage.selectionChanged(null, new StructuredSelection(source));
			setPageComplete(false);
		}
	}
	
	protected NamedQueryDefinition getQuery() {
		NamedQueryDefinition current=wizard.getCurrentQuery();
		String name=nameEditor.getText().trim();
		if (name.length()==0 || wizard.isExists(name)) {
			name=wizard.getCurrentQuery().getName();
			nameEditor.setText(name);
		}
		NamedQueryDefinition query=new NamedQueryDefinition(
				propertiesSource.getStorage(),
				name,
				propertiesSource.getQueryString(),
				propertiesSource.isCacheable(),
				propertiesSource.getCacheRegion(),
				propertiesSource.getTimeout(),
				propertiesSource.getFetchSize(),
				propertiesSource.getFlushMode()
		);
		query.setQueryString(getQueryString());
		return query;
		
	}
	
	private String getQueryString() {
		return document.get().trim();
	}
	
	private boolean isPrepared(String s) {
		return s!=null && s.trim().length()>0;
	}
	
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask(wizard.getString("Init"),3);
		final ClassLoader current=Thread.currentThread().getContextClassLoader();
		//closeQuerySession(); 20051020 yan
		try {
			
			setInput(null);
			
			IProject project=wizard.getMapping().getProject().getProject();
         // changed by Nick 30.08.2005
         //ClassLoader cl=EclipseResourceUtil.getClassLoader(project,Configuration.class.getClassLoader());
         ClassLoader cl = DefaultClassLoaderFactory.create(project,Configuration.class.getClassLoader());
         // by Nick
			Thread.currentThread().setContextClassLoader(cl);
			if (sessionFactory==null) {
				String cfg=wizard.getMapping().getConfiguration().getResource().getLocation().toString();
				
				Class clazz=cl.loadClass("org.hibernate.cfg.Configuration");
				configuration=(Configuration)clazz.newInstance();
				configuration.configure(new java.io.File(cfg));
				configuration.setProperty("hibernate.show_sql","true");
				
				configuration.getNamedQueries().clear();
				configuration.getNamedSQLQueries().clear();
				sessionFactory=configuration.buildSessionFactory();
				
			}
			
			if ((sqlDriver==null || session==null || connection==null ||
					!sqlDriver.getClass().getName().equals(driverClassName)) ||
					!userString.equals(configuration.getProperty("user")) ||
					!passwordString.equals(configuration.getProperty("password")) ||
					!connectionString.equals(currentConnectionString) ||
					!dialectString.equals(configuration.getProperty("hibernate.dialect"))
				) {
				closeQuerySession();
				configuration.setProperty("user",userString);
				configuration.setProperty("password",passwordString);
				configuration.setProperty("hibernate.dialect",dialectString);
				Class clazz=cl.loadClass(driverClassName);
				sqlDriver=(java.sql.Driver)clazz.newInstance();
				connection=sqlDriver.connect(currentConnectionString=connectionString,configuration.getProperties());
				session=sessionFactory.openSession(connection);
			}
			
			monitor.worked(1);
			
			if (monitor.isCanceled()) throw new InterruptedException();
			ArrayList list=new ArrayList(MAX_RESULTS);
//			Thread.sleep(2000);
			
			Query query=session.createQuery(getQueryString());
			query.setMaxResults(MAX_RESULTS);
			query.setCacheable(false);
//			query.list();
			Iterator it=query.iterate();
			while(it.hasNext()) {
				if (monitor.isCanceled()) {
					try {
						session.cancelQuery();
					} catch(HibernateException ex) {}
					throw new InterruptedException();
				}
				list.add(it.next());
				monitor.setTaskName(wizard.getString("Result",new String[]{Integer.toString(list.size()),""}));
			}
			
			monitor.worked(1);
			
//			Thread.sleep(2000);
			if (monitor.isCanceled()) throw new InterruptedException();

			setInput(
					new QueryResultItem(null,
							list.size()==0?null:list.toArray(),
							wizard.getString(
									"Result",
									new String[]{
											Integer.toString(list.size()),
											list.size()==MAX_RESULTS?wizard.getString("Limit",Integer.toString(MAX_RESULTS)):""
									}
							)
					)
			);
			
			monitor.worked(1);
			
//			Thread.sleep(2000);
		// #added# by Konstantin Mishin on 2005/08/23 fixed for ORMIISTUD-655
//        //TODO (tau-tau) for Exception - Start			
//		// edit tau 28.12.2005
//		//} catch(org.hibernate.hql.ast.QuerySyntaxError e) {
//		} catch(org.hibernate.hql.ast.QuerySyntaxException e) {
//			setInput(new QueryResultItem(null,e,wizard.getString("Error",queryName)));
//		// #added#
//		} catch(org.hibernate.JDBCException e) {
//			setInput(new QueryResultItem(null,e,wizard.getString("Error",queryName)));
//		} catch(org.hibernate.HibernateException e) {
//			setInput(new QueryResultItem(null,e,wizard.getString("Error",queryName)));			
//		} catch(SQLException sqlExc) {
//			//add tau 03.04.2006
//			//setInput(new QueryResultItem(null, sqlExc, wizard.getString("Error",queryName)));
//			setInput(new QueryResultItem(null, sqlExc, sqlExc.getLocalizedMessage()));			
//			ExceptionHandler.handleAsyncExec(sqlExc, getShell(), null, null, IStatus.WARNING);
			
		} catch(org.hibernate.HibernateException e) {
			setInput(new QueryResultItem(null, e, e.getLocalizedMessage()));			
			ExceptionHandler.handleAsyncExec(e, getShell(), null, null, IStatus.WARNING);				
		} catch(SQLException sqlExc) {
			setInput(new QueryResultItem(null, sqlExc, sqlExc.getLocalizedMessage()));			
			ExceptionHandler.handleAsyncExec(sqlExc, getShell(), null, null, IStatus.WARNING);			
		} catch(InterruptedException e) {
			QueryResultItem item=new QueryResultItem(null,e,wizard.getString("QueryCanceled"));
			item.setValue(null);
			setInput(item);			
		} catch(Throwable t) {
			setInput(new QueryResultItem(null,t,wizard.getString("Error",queryName)));
			ExceptionHandler.handleAsyncExec(new InvocationTargetException(t), getShell(), null, null, IStatus.ERROR);			
		} finally {
			getShell().getDisplay().asyncExec(
					new Runnable() {
						public void run() {
							if (closeTransaction.getSelection()) closeQuerySession();
							if (current!=null) Thread.currentThread().setContextClassLoader(current);
							treeViewer.getTree().setVisible(true);
						}
					}
			);
			monitor.done();
		}

	}
	
	private void setInput(final Object o) {
		getShell().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						treeViewer.setInput(o);
						treeViewer.expandToLevel(1);
					}
				}
		);
	}
	
	protected void closeQuerySession() {
		boolean callGC=false;
		if (session!=null) {
			session.clear();
			if (session.isConnected()) {
				try {
					session.disconnect();
				} catch(HibernateException e) {}
			}
			if (session.isOpen()) {
				session.close();
				session=null;
			}
		}
		try {
			if (connection!=null && !connection.isClosed()) {
				connection.close();
				connection=null;
			}
		} catch (SQLException e) {
        	//TODO (tau-tau) for Exception			
		}
	}
	
	
	public void execQuery() {
		//$deleted$ by Konstantin Mishin on 2005/08/31 fixed for ORMIISTUD-637
		//ByteArrayOutputStream bas = new ByteArrayOutputStream();
		//PrintStream defaultPrintStream = System.out;
		//System.setOut(new PrintStream(bas));	        
		//$deleted$
		// #added# by Konstantin Mishin on 16.09.2005 fixed for ESORM-105
		bas.reset();
		// #added#
		try {
			resultTabFolder.setSelection(0);
			if (nameEditor!=null) queryName=nameEditor.getText().trim();
			queryString=getQueryString();
			driverClassName=connectionPage.getDriverClassName();
			dialectString=connectionPage.getDialect();
			connectionString=connectionPage.getConnectionString();
			userString=connectionPage.getLogin();
			passwordString=connectionPage.getPassword();
			getContainer().run(true,true,this);
		} catch (Exception e) {
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.logThrowableWarning(e,null);		
		}
		//$added$ by Konstantin Mishin on 2005/08/15 fixed for ORMIISTUD-637
		consoleDocument.set(bas.toString());
		//$deleted$ by Konstantin Mishin on 2005/08/31 fixed for ORMIISTUD-637
		//System.setOut(defaultPrintStream);	        
		//$deleted$
		//$added$
	}

	//$added$ by Konstantin Mishin on 2005/08/31 fixed for ORMIISTUD-637
	public void dispose() {
		System.setOut(defaultPrintStream);
		super.dispose();
	}
	//$added$

}
