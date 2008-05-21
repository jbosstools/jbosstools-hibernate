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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingConfiguration;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.hibernate.XMLFileStorage;
import org.jboss.tools.hibernate.internal.core.hibernate.query.NamedQueryDefinition;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.view.views.ReadOnlyWizard;


/**
 * @author yan
 *
 */
public class NamedQueriesWizard extends ReadOnlyWizard {
	
	private IMapping iMapping;
	private XMLFileStorage queryStorage;
	private NamedQueryDefinition currentQuery;
	private NamedQueryEditorPage editPage;
	private HashMap namedQueries;
	private boolean canFinish,newQueryMode;
	private String messageKey;
	
	private static final String BUNDLE_NAME="messages"; 
	protected static final ResourceBundle BUNDLE=ResourceBundle.getBundle(NamedQueriesWizard.class.getPackage().getName() + "." + BUNDLE_NAME); 
	
	public NamedQueriesWizard(IMapping mapping,XMLFileStorage storage,NamedQueryDefinition query,boolean newquery, TreeViewer viewer) throws DuplicateQueryNameException {
		
		// add tau 15.02.2006
		super(storage, viewer);
		
		iMapping=mapping;
		currentQuery=query;
		queryStorage=storage;
		newQueryMode=newquery;
		messageKey=readOnly()?"QueryTest.":"NamedQueriesWizard.";
		
		setNeedsProgressMonitor(true);
		setWindowTitle(getString("Title",readOnly()?query.getName():queryStorage.getName()));
		
		
		namedQueries=new HashMap();
		if (!readOnly()) {
			IMappingConfiguration conf=iMapping.getConfiguration();
			if (conf instanceof HibernateConfiguration) {
				IMappingStorage[] ms=((HibernateConfiguration)conf).getMappingStorages();
				for(int i=0; i<ms.length; i++) {
					if (ms[i] instanceof XMLFileStorage) {
						XMLFileStorage fs=(XMLFileStorage)ms[i];
						Iterator it=fs.getQueries().keySet().iterator();
						while(it.hasNext()) {
							String s=(String)it.next();
							if (namedQueries.containsKey(s)/* && !currentQuery.getName().equals(s)*/) {
								throw new DuplicateQueryNameException(
										getString(
												"DuplicateName2",
												new String[]{
														s,
														((XMLFileStorage)namedQueries.get(s)).getResource().getProjectRelativePath().toString(),
														fs.getResource().getProjectRelativePath().toString()
												}
										)
								);
							} else {
								namedQueries.put(s,fs);
							}
						}
						
					}
				}
			}
		}
		
		
	}
	
	
	public String getString(String name) {
		return getString(name,(String)null);
	}
	
	public String getString(String name,String param) {
		return getString(name,new Object[]{param});
	}
	
	public String getString(String name,Object[] params) {
		//return BUNDLE.getString("NamedQueriesWizard."+name);
		return MessageFormat.format(BUNDLE.getString(messageKey+name),params);
	}
	


//	public boolean performCancel() {
////		editPage.closeQuerySession();
//		return true;
//	}

	public boolean performFinish() {
		if (readOnly()) return true;
		NamedQueryDefinition query=editPage.getQuery();
		queryStorage.removeQuery(currentQuery.getName());
		queryStorage.addQuery(query.getName(),query);
//		editPage.closeQuerySession();
		try {
			// edit tau 29.03.2006
			//queryStorage.save();
			queryStorage.save(true);
			iMapping.resourcesChanged();
		} catch(Exception t) {
			ExceptionHandler.handle(t,getShell(),getWindowTitle(),getString("SaveQueryError",query.getName()));
			return false;
		}
		return true;
	}

	public void addPages() {
		addPage(editPage=new NamedQueryEditorPage(this));
		setDefaultPageImageDescriptor(ViewPlugin.getImageDescriptor(ViewPlugin.BUNDLE_IMAGE.getString("Wizard.Title")));
	}

	public NamedQueryDefinition getCurrentQuery() {
		return currentQuery;
	}


	public IMapping getMapping() {
		return iMapping;
	}
	
	// edit tau 15.02.2006 - add result
	public boolean canFinish() {
		
		boolean result = true;
		
		result = canFinish && !readOnly();
		
		// add tau 13.02.2006
		// for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?		
		if (this.readOnly) result = false;
		
		return result;
		
	}
	
	public boolean readOnly() {
		return queryStorage==null;
	}
	
	protected void setFinish(boolean finish) {
		canFinish=finish;
		this.getContainer().updateButtons();
	}

	protected boolean isExists(String name) {
		XMLFileStorage fs=(XMLFileStorage)namedQueries.get(name);
		return fs!=null && fs!=queryStorage;
	}
	public void dispose() {
		super.dispose();
		editPage.closeQuerySession();
	}


	protected boolean isNewQueryMode() {
		return newQueryMode;
	}

}
