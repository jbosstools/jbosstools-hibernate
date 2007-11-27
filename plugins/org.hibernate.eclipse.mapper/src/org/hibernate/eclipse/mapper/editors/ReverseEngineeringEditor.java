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
package org.hibernate.eclipse.mapper.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.INestableKeyBindingService;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.console.workbench.LazyDatabaseSchema;
import org.hibernate.eclipse.mapper.MapperPlugin;
import org.hibernate.eclipse.mapper.editors.reveng.RevEngOverviewPage;
import org.hibernate.eclipse.mapper.editors.reveng.RevEngTableFilterPage;
import org.hibernate.eclipse.mapper.editors.reveng.RevEngTablesPage;
import org.hibernate.eclipse.mapper.editors.reveng.RevEngTypeMappingPage;
import org.hibernate.eclipse.mapper.editors.xpl.XMLFormEditorPart;
import org.hibernate.eclipse.mapper.model.DOMReverseEngineeringDefinition;
import org.hibernate.eclipse.nature.HibernateNature;
import org.hibernate.util.StringHelper;
import org.w3c.dom.Document;

public class ReverseEngineeringEditor extends XMLFormEditorPart {

	private StructuredTextEditor sourcePage;
	private DOMReverseEngineeringDefinition definition;
	
	private RevEngTableFilterPage tableFilterPage;
	private RevEngTypeMappingPage typeMappingsPage;
	private RevEngOverviewPage overviewsPage;	
	private Map pageNameToIndex = new HashMap();
	private RevEngTablesPage tableProperties;
	
	public ReverseEngineeringEditor() {
		
	}
	
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init( site, input );
	}

	protected void addPages() {
		try {
			//getSite().getSelectionProvider().setSelection(StructuredSelection.EMPTY);
			super.addPages();
			initSourcePage();
			addFormPages();
		}
		catch (PartInitException e) {
			MapperPlugin.getDefault().getLogger().logException(
					"Could not create graphical viewer", e );
		}
	}

	private void addFormPages() throws PartInitException {
		int i = 0;
		overviewsPage = new RevEngOverviewPage(this);
		addPage( i, overviewsPage);
		setPageText(i, "Overview");
		pageNameToIndex.put(RevEngOverviewPage.PART_ID, new Integer(i));
		i++;
		
		typeMappingsPage = new RevEngTypeMappingPage( this );
		addPage( i, typeMappingsPage);
		setPageText( i, "Type Mappings" );
		pageNameToIndex.put(RevEngTypeMappingPage.PART_ID, new Integer(i));
		i++;
		

		tableFilterPage = new RevEngTableFilterPage( this );
		addPage( i, tableFilterPage);
		setPageText( i, "Table Filters" );
		pageNameToIndex.put(RevEngTableFilterPage.PART_ID, new Integer(i));		
		i++;
		
		tableProperties = new RevEngTablesPage(this );
		addPage( i, tableProperties);
		setPageText(i, "Table && Columns");
		pageNameToIndex.put(RevEngTablesPage.PART_ID, new Integer(i));
		i++;
		
	//	setActivePage( 0 );
	}

	/*public void setActivePage(String string) {
		Integer number = (Integer) pageNameToIndex.get(string);
		if(number!=null) {
			setActivePage(number.intValue());
		}		
	}*/
	
	private void initSourcePage() {
		int pageCount = getPageCount();
		for (int i = 0; i < pageCount; i++) {
			if ( getEditor( i ) instanceof StructuredTextEditor ) {
				sourcePage = (StructuredTextEditor) getEditor( i );
				IDOMDocument document = getDocument(sourcePage);
				definition = new DOMReverseEngineeringDefinition(document);				
			}
		}
	}

	private IDOMDocument getDocument(StructuredTextEditor source) {
		IDOMDocument document = (IDOMDocument) source
				.getAdapter( Document.class );
		return document;
	}

	protected void pageChange(int newPageIndex) {
		if (newPageIndex == 0) {
	        IKeyBindingService service = getSite().getKeyBindingService();
            if (service instanceof INestableKeyBindingService) {
                INestableKeyBindingService nestableService = (INestableKeyBindingService) service;
                nestableService.activateKeyBindingService(null);
            }	        
		}
		super.pageChange(newPageIndex);
	}
	
	public IReverseEngineeringDefinition getReverseEngineeringDefinition() {
		return definition;
	}
	
	public String getConsoleConfigurationName() {
		return overviewsPage.getConsoleConfigName();
	}
	
	protected void setConsoleConfigurationName(String name) {
		overviewsPage.setConsoleConfigName(name);
	}
	
	public HibernateNature getHibernateNature() throws CoreException {
		if(getEditorInput()!=null) {
			IJavaProject project = ProjectUtils.findJavaProject(getEditorInput());
			return HibernateNature.getHibernateNature( project );
		} else {
			return null;
		}
	}

	public LazyDatabaseSchema getLazyDatabaseSchema() {
		try {
			ConsoleConfiguration configuration = KnownConfigurations.getInstance().find( getConsoleConfigurationName() );
			if(configuration == null) {
				configuration = askForConsoleConfiguration();
				if(configuration==null) {
					return null;
				} else {
					setConsoleConfigurationName(configuration.getName());
				}
			}
			 
			ITableFilter[] tableFilters = getReverseEngineeringDefinition().getTableFilters();
			Configuration cfg = configuration.buildWith(null, false);
			Settings settings = configuration.getSettings(cfg);
			
			OverrideRepository repository = new OverrideRepository();///*settings.getDefaultCatalogName(),settings.getDefaultSchemaName()*/);
			boolean hasIncludes = false;
			for (int i = 0; i < tableFilters.length; i++) {
				ITableFilter filter = tableFilters[i];
				TableFilter tf = new TableFilter();
				tf.setExclude(filter.getExclude());
				if(filter.getExclude()!=null && !filter.getExclude().booleanValue()) {
					hasIncludes = true;
				}
				tf.setMatchCatalog(filter.getMatchCatalog());
				tf.setMatchName(filter.getMatchName());
				tf.setMatchSchema(filter.getMatchSchema());
				repository.addTableFilter(tf);
			}
			TableFilter tf = new TableFilter();
			tf.setExclude(Boolean.FALSE);
			tf.setMatchCatalog(".*");
			tf.setMatchSchema(".*");
			tf.setMatchName(".*");
			repository.addTableFilter(tf);
			if(tableFilters.length==0) {
				boolean b = MessageDialog.openQuestion(getContainer().getShell(), "No filters defined", "No filters has been defined.\n This can make the reading of the database schema very slow.\n Do you wish to continue reading the database schema ?");
				if(!b) {
					return null;
				}
			}
			//if(!hasIncludes && tableFilters.length>0) { // not true anymore since it is converted to a include everything...
				//boolean b = MessageDialog.openQuestion(getContainer().getShell(), "Only exclude filters defined", "Only exclude filters has been defined.\n This will result in no tables being read from the database schema.\n Do you wish to continue reading the database schema ?");
				//if(!b) {
				//	return null;
				//}
			//}
			
			LazyDatabaseSchema lazyDatabaseSchema = new LazyDatabaseSchema(configuration, repository.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy()));
			
			return lazyDatabaseSchema;
		} catch(HibernateException he) {
			HibernateConsolePlugin.getDefault().showError(getContainer().getShell(), "Error while refreshing database tree", he);
			return null;
		}
	}

	private ConsoleConfiguration askForConsoleConfiguration() {
		ChooseConsoleConfigurationDialog dialog = new ChooseConsoleConfigurationDialog(getContainer().getShell(),null);
		dialog.prompt();
		if(dialog.getSelectedConfigurationName()==null) {
			return null;
		} else {
			return KnownConfigurations.getInstance().find( dialog.getSelectedConfigurationName() ); // TODO: double check to see if an result is actually returned ?
		}		
	}
	
}